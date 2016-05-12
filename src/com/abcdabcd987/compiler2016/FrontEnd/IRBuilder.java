package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.IR.BinaryOperation.BinaryOp;
import com.abcdabcd987.compiler2016.IR.IntComparison.Condition;
import com.abcdabcd987.compiler2016.IR.UnaryOperation.UnaryOp;
import com.abcdabcd987.compiler2016.Symbol.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class IRBuilder implements IASTVisitor {
    private BasicBlock curBB;
    private BasicBlock curLoopStepBB;
    private BasicBlock curLoopAfterBB;
    private Function curFunction;
    private boolean getAddress = false;
    private boolean isFunctionArgDecl = false;
    private IRRoot irRoot = new IRRoot();
    private GlobalSymbolTable sym;
    private Map<StaticData, VirtualRegister> curFuncStaticMap = new HashMap<>();

    public IRBuilder(GlobalSymbolTable sym) {
        this.sym = sym;
    }

    public IRRoot getIRRoot() {
        return irRoot;
    }

    private void registerBuiltinFunction(FunctionType func) {

    }

    private boolean isLogicalExpression(Expr node) {
        if (node instanceof BinaryExpr) {
            BinaryExpr.BinaryOp op = ((BinaryExpr) node).op;
            return op == BinaryExpr.BinaryOp.LOGICAL_AND || op == BinaryExpr.BinaryOp.LOGICAL_OR;
        } else if (node instanceof UnaryExpr) {
            return ((UnaryExpr) node).op == UnaryExpr.UnaryOp.LOGICAL_NOT;
        } else {
            return false;
        }
    }

    private boolean needMemoryAccess(Expr node) {
        if (node instanceof MemberAccess) return true;
        if (node instanceof ArrayAccess) return true;
        return false;
    }

    @Override
    public void visit(Program node) {
        node.decls.forEach(x -> x.accept(this));
        irRoot.functions.values().forEach(Function::updateCalleeSet);
        irRoot.calcRecursiveCalleeSet();
    }

    @Override
    public void visit(StructDecl node) {
        // no actions
    }

    @Override
    public void visit(VariableDecl node) {
        SymbolInfo info = node.scope.getInfo(node.name);
        boolean isGlobalVariable = node.scope == sym.globals;
        if (isGlobalVariable) {
            StaticData data = new StaticSpace(info.getType().getRegisterSize(), node.name);
            info.register = data;
            irRoot.dataList.add(data);
            // global variable init has been thrown to `__init` function in AST
        } else {
            VirtualRegister reg = new VirtualRegister(node.name);
            if (isFunctionArgDecl) {
                curFunction.argVarRegList.add(reg);
            }
            info.register = reg;
            if (node.init != null) {
                if (isLogicalExpression(node.init)) {
                    node.init.ifTrue = new BasicBlock(curFunction, null);
                    node.init.ifFalse = new BasicBlock(curFunction, null);
                }
                node.init.accept(this);
                assign(false, node.init.exprType.getRegisterSize(), reg, 0, node.init);
            } else if (!isFunctionArgDecl) {
                // set 0 if no initial value
                curBB.append(new Move(curBB, reg, new IntImmediate(0)));
            }
        }
    }

    @Override
    public void visit(FunctionDecl node) {
        curFuncStaticMap.clear();
        curFunction = new Function(node.functionType);
        irRoot.functions.put(node.name, curFunction);
        curBB = curFunction.getStartBB();
        isFunctionArgDecl = true;
        node.argTypes.forEach(x -> x.accept(this));
        isFunctionArgDecl = false;

        node.body.accept(this);
        if (!curBB.isEnded()) {
            if (curFunction.getType().returnType.type == Type.Types.VOID) {
                curBB.end(new Return(curBB, null));
            } else {
                //assert node.name.equals("main");
                curBB.end(new Return(curBB, new IntImmediate(0)));
            }
        }

        // if multiple return instruction, merge to an exit block
        if (curFunction.retInstruction.size() > 1) {
            BasicBlock exitBB = new BasicBlock(curFunction, curFunction.getName() + ".exit");
            VirtualRegister retReg = node.functionType.returnType == GlobalSymbolTable.voidType ? null : new VirtualRegister("retvalue");
            List<Return> retInstructions = new ArrayList<>(curFunction.retInstruction);
            for (Return ret : retInstructions) {
                BasicBlock BB = ret.getBasicBlock();
                if (ret.getRet() != null) ret.prepend(new Move(BB, retReg, ret.getRet()));
                ret.remove();
                BB.end(new Jump(BB, exitBB));
            }
            assert curFunction.retInstruction.size() == 0;
            exitBB.end(new Return(exitBB, retReg));
            curFunction.exitBB = exitBB;
        } else {
            curFunction.exitBB = curFunction.retInstruction.get(0).getBasicBlock();
        }
        assert curFunction.retInstruction.size() == 1;

        // remove unreachable block
        curFunction.exitBB.getPred().retainAll(curFunction.getReversePostOrder());

        curFunction = null;
    }

    @Override
    public void visit(ArrayTypeNode node) {
        // no actions
    }

    @Override
    public void visit(PrimitiveTypeNode node) {
        // no actions
    }

    @Override
    public void visit(BreakStmt node) {
        curBB.end(new Jump(curBB, curLoopAfterBB));
    }

    @Override
    public void visit(ContinueStmt node) {
        curBB.end(new Jump(curBB, curLoopStepBB));
    }

    @Override
    public void visit(ReturnStmt node) {
        if (curFunction.getType().returnType.type == Type.Types.VOID) {
            curBB.end(new Return(curBB, null));
        } else {
            if (isLogicalExpression(node.value)) {
                // build value
                node.value.ifTrue = new BasicBlock(curFunction, null);
                node.value.ifFalse = new BasicBlock(curFunction, null);
                visit(node.value);

                // build assignment
                VirtualRegister reg = new VirtualRegister("retvalue");
                assign(false, CompilerOptions.getSizeInt(), reg, 0, node.value);

                // return result
                curBB.end(new Return(curBB, reg));
            } else {
                visit(node.value);
                curBB.end(new Return(curBB, node.value.intValue));
            }
        }
    }

    @Override
    public void visit(FunctionTypeNode node) {
        // no actions
    }

    @Override
    public void visit(StructTypeNode node) {
        // no actions
    }

    @Override
    public void visit(CompoundStmt node) {
        node.stmts.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(IfStmt node) {
        BasicBlock BBTrue = new BasicBlock(curFunction, "if_true");
        BasicBlock BBFalse = node.otherwise != null ? new BasicBlock(curFunction, "if_false") : null;
        BasicBlock BBMerge = new BasicBlock(curFunction, "if_merge");

        // branch instruction should be added by logical expression
        node.cond.ifTrue = BBTrue;
        node.cond.ifFalse = node.otherwise != null ? BBFalse : BBMerge;
        visit(node.cond);

        // generate then
        curBB = BBTrue;
        visit(node.then);
        if (!curBB.isEnded()) curBB.end(new Jump(curBB, BBMerge));

        // generate else
        if (node.otherwise != null) {
            curBB = BBFalse;
            visit(node.otherwise);
        }
        if (BBFalse != null && !curBB.isEnded()) curBB.end(new Jump(curBB, BBMerge));

        // merge
        curBB = BBMerge;
    }

    @Override
    public void visit(ForLoop node) {
        BasicBlock BBCond = new BasicBlock(curFunction, "for_cond");
        BasicBlock BBLoop = new BasicBlock(curFunction, "for_loop");
        BasicBlock BBStep = new BasicBlock(curFunction, "for_step");
        BasicBlock BBAfter = new BasicBlock(curFunction, "for_after");

        if (node.cond == null) BBCond = BBLoop;
        if (node.step == null) BBStep = BBCond;

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBStep;
        curLoopAfterBB = BBAfter;

        // generate init
        if (node.init != null) visit(node.init);
        else if (node.initWithDecl != null) node.initWithDecl.forEach(x -> x.accept(this));

        // jump to condition check
        curBB.end(new Jump(curBB, BBCond));

        // generate condition check
        if (node.cond != null) {
            curBB = BBCond;
            node.cond.ifTrue = BBLoop;
            node.cond.ifFalse = BBAfter;
            visit(node.cond);
        }

        // generate loop
        curBB = BBLoop;
        visit(node.body);
        curBB.end(new Jump(curBB, BBStep));

        // generate step
        if (node.step != null) {
            curBB = BBStep;
            visit(node.step);
            curBB.end(new Jump(curBB, BBCond));
        }

        // exit loop
        curLoopStepBB = oldLoopCondBB;
        curLoopAfterBB = oldLoopAfterBB;
        curBB = BBAfter;
    }

    @Override
    public void visit(WhileLoop node) {
        BasicBlock BBCond = new BasicBlock(curFunction, "while_cond");
        BasicBlock BBLoop = new BasicBlock(curFunction, "while_loop");
        BasicBlock BBAfter = new BasicBlock(curFunction, "while_after");

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBCond;
        curLoopAfterBB = BBAfter;

        // jump to condition check
        curBB.end(new Jump(curBB, BBCond));

        // generate condition check
        curBB = BBCond;
        node.cond.ifTrue = BBLoop;
        node.cond.ifFalse = BBAfter;
        visit(node.cond);

        // generate loop
        curBB = BBLoop;
        visit(node.body);
        curBB.end(new Jump(curBB, BBCond));

        // exit loop
        curLoopStepBB = oldLoopCondBB;
        curLoopAfterBB = oldLoopAfterBB;
        curBB = BBAfter;
    }

    @Override
    public void visit(VariableDeclStmt node) {
        visit(node.decl);
    }

    @Override
    public void visit(ArrayAccess node) {
        boolean getaddr = getAddress;
        getAddress = false;
        visit(node.array);
        visit(node.subscript);
        getAddress = getaddr;

        IntValue tmp1 = new IntImmediate(((ArrayType)node.array.exprType).bodyType.getRegisterSize());
        VirtualRegister reg = new VirtualRegister(null);
        curBB.append(new BinaryOperation(curBB, reg, BinaryOp.MUL, node.subscript.intValue, tmp1));
        curBB.append(new BinaryOperation(curBB, reg, BinaryOp.ADD, node.array.intValue, reg));
        if (getAddress) {
            node.addressValue = reg;
            node.addressOffset = CompilerOptions.getSizeInt();
        } else {
            curBB.append(new Load(curBB, reg, node.exprType.getRegisterSize(), reg, CompilerOptions.getSizeInt()));
            node.intValue = reg;
            if (node.ifTrue != null) {
                curBB.end(new Branch(curBB, node.intValue, node.ifTrue, node.ifFalse));
            }
        }
    }

    @Override
    public void visit(UnaryExpr node) {
        // logical unary expression
        if (node.op == UnaryExpr.UnaryOp.LOGICAL_NOT) {
            node.body.ifTrue = node.ifFalse;
            node.body.ifFalse = node.ifTrue;
            visit(node.body);
            return;
        }

        // other unary expression
        visit(node.body);
        VirtualRegister reg;
        switch (node.op) {
            case INC:
                processSelfIncDec(node.body, node, true, false);
                break;
            case DEC:
                processSelfIncDec(node.body, node, false, false);
                break;

            case POS:
                node.intValue = node.body.intValue;
                break;
            case NEG:
                reg = new VirtualRegister(null);
                node.intValue = reg;
                curBB.append(new UnaryOperation(curBB, reg, UnaryOp.NEG, node.body.intValue));
                break;

            case BITWISE_NOT:
            default:
                reg = new VirtualRegister(null);
                node.intValue = reg;
                curBB.append(new UnaryOperation(curBB, reg, UnaryOp.NOT, node.body.intValue));
                break;
        }
    }

    private void processLogicalBinaryExpr(BinaryExpr node) {
        // check lhs
        if (node.op == BinaryExpr.BinaryOp.LOGICAL_AND) {
            node.lhs.ifTrue = new BasicBlock(curFunction, "lhs_true");
            node.lhs.ifFalse = node.ifFalse;
            visit(node.lhs);
            curBB = node.lhs.ifTrue;
        } else {
            node.lhs.ifTrue = node.ifTrue;
            node.lhs.ifFalse = new BasicBlock(curFunction, "lhs_false");
            visit(node.lhs);
            curBB = node.lhs.ifFalse;
        }

        // check rhs
        node.rhs.ifTrue = node.ifTrue;
        node.rhs.ifFalse = node.ifFalse;
        visit(node.rhs);
    }

    private void processIntRelationalExpr(BinaryExpr node) {
        visit(node.lhs);
        visit(node.rhs);

        Condition cond = null;
        switch (node.op) {
            case EQ: cond = Condition.EQ; break;
            case NE: cond = Condition.NE; break;
            case LT: cond = Condition.LT; break;
            case GT: cond = Condition.GT; break;
            case LE: cond = Condition.LE; break;
            case GE: cond = Condition.GE; break;
        }

        // generate cmp and br instructions
        VirtualRegister reg = new VirtualRegister(null);
        curBB.append(new IntComparison(curBB, reg, cond, node.lhs.intValue, node.rhs.intValue));
        if (node.ifTrue != null) {
            // if the expression is in a condition part
            curBB.end(new Branch(curBB, reg, node.ifTrue, node.ifFalse));
        } else {
            // if the expression is in an assignment
            node.intValue = reg;
        }
    }

    private void processStringBinaryExpr(BinaryExpr node) {
        visit(node.lhs);
        visit(node.rhs);
        VirtualRegister reg = new VirtualRegister(null);
        node.intValue = reg;
        Call call = null;
        switch (node.op){
            case ADD: call = new Call(curBB, reg, irRoot.builtinStringConcat); break;
            case EQ:  call = new Call(curBB, reg, irRoot.builtinStringEqual); break;
            case LT:  call = new Call(curBB, reg, irRoot.builtinStringLess); break;
            case GT:
                Expr t = node.lhs;
                node.lhs = node.rhs;
                node.rhs = t;
                call = new Call(curBB, reg, irRoot.builtinStringLess);
                break;
            default:
                assert false;
        }
        call.appendArg(node.lhs.intValue);
        call.appendArg(node.rhs.intValue);
        curBB.append(call);

        // br instructions
        if (node.ifTrue != null) {
            // if the expression is in a condition part
            curBB.end(new Branch(curBB, reg, node.ifTrue, node.ifFalse));
        } else {
            // if the expression is in an assignment
            node.intValue = reg;
        }
    }

    private void processIntArithmeticExpr(BinaryExpr node) {
        visit(node.lhs);
        visit(node.rhs);
        BinaryOp op = null;
        switch (node.op) {
            case SHL: op = BinaryOp.SHL; break;
            case SHR: op = BinaryOp.SHR; break;
            case ADD: op = BinaryOp.ADD; break;
            case SUB: op = BinaryOp.SUB; break;
            case MUL: op = BinaryOp.MUL; break;
            case DIV: op = BinaryOp.DIV; break;
            case MOD: op = BinaryOp.MOD; break;
            case XOR: op = BinaryOp.XOR; break;
            case BITWISE_OR: op = BinaryOp.OR; break;
            case BITWISE_AND: op = BinaryOp.AND; break;
        }
        VirtualRegister reg = new VirtualRegister(null);
        node.intValue = reg;
        curBB.append(new BinaryOperation(curBB, reg, op, node.lhs.intValue, node.rhs.intValue));
    }

    private void assign(boolean isMemOp, int size, IntValue addr, int offset, Expr rhs) {
        if (rhs.ifTrue != null) {
            // for short-circuit evaluation
            BasicBlock merge = new BasicBlock(curFunction, null);
            if (isMemOp) {
                rhs.ifTrue.append(new Store(curBB, new IntImmediate(1), size, addr, offset));
                rhs.ifFalse.append(new Store(curBB, new IntImmediate(0), size, addr, offset));
            } else {
                rhs.ifTrue.append(new Move(curBB, (VirtualRegister)addr, new IntImmediate(1)));
                rhs.ifFalse.append(new Move(curBB, (VirtualRegister)addr, new IntImmediate(0)));
            }
            rhs.ifTrue.end(new Jump(curBB, merge));
            rhs.ifFalse.end(new Jump(curBB, merge));
            curBB = merge;
        } else {
            if (isMemOp) {
                curBB.append(new Store(curBB, rhs.intValue, size, addr, offset));
            } else {
                curBB.append(new Move(curBB, (Register) addr, rhs.intValue));
            }
        }
    }

    private void processAssign(BinaryExpr node) {
        // build rhs
        if (isLogicalExpression(node.rhs)) {
            node.rhs.ifTrue = new BasicBlock(curFunction, null);
            node.rhs.ifFalse = new BasicBlock(curFunction, null);
        }
        visit(node.rhs);

        // build the address of lhs
        boolean isMemOp = needMemoryAccess(node.lhs);
        getAddress = isMemOp;
        visit(node.lhs);
        getAddress = false;

        // build assignment
        IntValue addr = isMemOp ? node.lhs.addressValue : node.lhs.intValue;
        int offset = isMemOp ? node.lhs.addressOffset : 0;
        assign(isMemOp, node.rhs.exprType.getRegisterSize(), addr, offset, node.rhs);
        node.intValue = node.rhs.intValue;
    }

    @Override
    public void visit(BinaryExpr node) {
        switch (node.op) {
            case ASSIGN:
                processAssign(node);
                break;

            case LOGICAL_OR:
            case LOGICAL_AND:
                processLogicalBinaryExpr(node);
                break;

            case EQ:
            case NE:
            case LT:
            case GT:
            case LE:
            case GE:
                if (node.lhs.exprType.type == Type.Types.STRING) processStringBinaryExpr(node);
                else processIntRelationalExpr(node);
                break;

            case SHL:
            case SHR:
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case XOR:
            case BITWISE_OR:
            case BITWISE_AND:
                if (node.lhs.exprType.type == Type.Types.STRING) processStringBinaryExpr(node);
                else processIntArithmeticExpr(node);
                break;
        }
    }

    @Override
    public void visit(EmptyExpr node) {
        // do nothing
    }



    /**
     * print & println optimization
     * print(A + B + C);   =>  print(A); print(B); print(C);
     * print(toString(i)); =>  printInt(i);
     * @param lastNewLine if print new line for the last element
     */
    private void processPrint(Expr node, boolean lastNewLine) {
        if (node instanceof BinaryExpr) {
            // print(A + B)
            BinaryExpr expr = (BinaryExpr) node;
            assert expr.op == BinaryExpr.BinaryOp.ADD;
            processPrint(expr.lhs, false);
            processPrint(expr.rhs, lastNewLine);
        } else if (node instanceof FunctionCall && ((FunctionCall) node).name.exprType == GlobalSymbolTable.toStringFunc) {
            // print(toString(intExpr))
            Expr intExpr = ((FunctionCall) node).parameters.get(0);
            visit(intExpr);
            Call call = new Call(curBB, null, lastNewLine ? irRoot.builtinPrintlnInt : irRoot.builtinPrintInt);
            call.appendArg(intExpr.intValue);
            curBB.append(call);
        } else {
            // print(stringExpr)
            visit(node);
            Call call = new Call(curBB, null, lastNewLine ? irRoot.builtinPrintlnString : irRoot.builtinPrintString);
            call.appendArg(node.intValue);
            curBB.append(call);
        }
    }

    private boolean processBuiltinFunctionCall(FunctionCall node, FunctionType type) {
        boolean bakGetAddress = getAddress;
        getAddress = false;
        if (type == GlobalSymbolTable.arraySize || type == GlobalSymbolTable.stringLength) {
            // array.size, string.length
            visit(node.argThis);
            VirtualRegister reg = new VirtualRegister("size");
            curBB.append(new Load(curBB, reg, CompilerOptions.getSizeInt(), node.argThis.intValue, 0));
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.stringOrd) {
            // string.ord
            visit(node.argThis);
            node.parameters.get(0).accept(this);
            VirtualRegister reg = new VirtualRegister("ord");
            curBB.append(new BinaryOperation(curBB, reg, BinaryOp.ADD, node.argThis.intValue, node.parameters.get(0).intValue));
            curBB.append(new Load(curBB, reg, 1, reg, 4));
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.printStringFunc || type == GlobalSymbolTable.printlnStringFunc) {
            // print & println (with optimization)
            processPrint(node.parameters.get(0), type == GlobalSymbolTable.printlnStringFunc);
        } else if (type == GlobalSymbolTable.stringSubString) {
            // string.subString
            visit(node.argThis);
            node.parameters.get(0).accept(this);
            node.parameters.get(1).accept(this);
            VirtualRegister reg = new VirtualRegister("substr");
            Call call = new Call(curBB, reg, irRoot.builtinStringSubString);
            call.appendArg(node.argThis.intValue);
            call.appendArg(node.parameters.get(0).intValue);
            call.appendArg(node.parameters.get(1).intValue);
            curBB.append(call);
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.stringParseInt) {
            // string.parseInt
            visit(node.argThis);
            VirtualRegister reg = new VirtualRegister("parsedInt");
            Call call = new Call(curBB, reg, irRoot.builtinStringParseInt);
            call.appendArg(node.argThis.intValue);
            curBB.append(call);
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.toStringFunc) {
            // toString
            visit(node.parameters.get(0));
            VirtualRegister reg = new VirtualRegister("tostring");
            Call call = new Call(curBB, reg, irRoot.builtinToString);
            call.appendArg(node.parameters.get(0).intValue);
            curBB.append(call);
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.getStringFunc) {
            // getString
            VirtualRegister reg = new VirtualRegister("gottenString");
            Call call = new Call(curBB, reg, irRoot.builtinGetString);
            curBB.append(call);
            node.intValue = reg;
        } else if (type == GlobalSymbolTable.getIntFunc) {
            // getInt
            VirtualRegister reg = new VirtualRegister("gottenInt");
            Call call = new Call(curBB, reg, irRoot.builtinGetInt);
            curBB.append(call);
            node.intValue = reg;
        } else {
            return false;
        }
        getAddress = bakGetAddress;
        return true;
    }

    @Override
    public void visit(FunctionCall node) {
        FunctionType type = (FunctionType) node.name.exprType;
        if (processBuiltinFunctionCall(node, type)) return;

        // for other function calls
        Function func = irRoot.functions.get(type.name);
        node.parameters.forEach(x -> x.accept(this));
        VirtualRegister reg = new VirtualRegister(null);
        Call call = new Call(curBB, reg, func);
        if (node.argThis != null) { // for builtin string & array function
            visit(node.argThis);
            call.appendArg(node.argThis.intValue);
        }
        node.parameters.forEach(x -> call.appendArg(x.intValue));
        curBB.append(call);
        node.intValue = reg;


        if (node.ifTrue != null) {
            curBB.end(new Branch(curBB, node.intValue, node.ifTrue, node.ifFalse));
        }
    }

    @Override
    public void visit(NewExpr node) {
        Type type = node.exprType;
        VirtualRegister reg = new VirtualRegister(null);
        if (type.type == Type.Types.STRUCT) {
            // struct
            StructType t = (StructType) type;
            curBB.append(new HeapAllocate(curBB, reg, new IntImmediate(t.getMemorySize())));
        } else {
            // array
            Expr dim = node.dim.get(0);
            boolean getaddr = getAddress;
            getAddress = false;
            visit(dim);
            getAddress = getaddr;

            ArrayType t = (ArrayType) type;
            curBB.append(new BinaryOperation(curBB, reg, BinaryOp.MUL, dim.intValue, new IntImmediate(t.bodyType.getRegisterSize())));
            curBB.append(new BinaryOperation(curBB, reg, BinaryOp.ADD, reg, new IntImmediate(CompilerOptions.getSizeInt())));
            curBB.append(new HeapAllocate(curBB, reg, reg));
            curBB.append(new Store(curBB, dim.intValue, CompilerOptions.getSizeInt(), reg, 0));
        }
        node.intValue = reg;
    }

    @Override
    public void visit(MemberAccess node) {
        boolean getaddr = getAddress;
        getAddress = false;
        visit(node.record);
        getAddress = getaddr;

        IntValue addr = node.record.intValue;
        StructType t = (StructType) node.record.exprType;
        SymbolInfo info = t.members.getInfo(node.member);
        if (getAddress) {
            node.addressValue = addr;
            node.addressOffset = info.getOffset();
        } else {
            VirtualRegister reg = new VirtualRegister(null);
            node.intValue = reg;
            curBB.append(new Load(curBB, reg, info.getType().getRegisterSize(), addr, info.getOffset()));

            if (node.ifTrue != null) {
                curBB.end(new Branch(curBB, node.intValue, node.ifTrue, node.ifFalse));
            }
        }
    }

    private void processSelfIncDec(Expr body, Expr node, boolean isInc, boolean isPostfix) {
        boolean isMemOp = needMemoryAccess(body);
        // get address
        boolean getaddr = getAddress;
        getAddress = isMemOp;
        visit(body);
        IntValue addr = body.addressValue;
        int offset = body.addressOffset;

        // get value
        getAddress = false;
        visit(body);
        getAddress = getaddr;

        // stuffs
        BinaryOp op = isInc ? BinaryOp.ADD : BinaryOp.SUB;
        IntImmediate one = new IntImmediate(1);
        VirtualRegister reg;

        // if postfix, backup old value
        if (isPostfix) {
            reg = new VirtualRegister(null);
            curBB.append(new Move(curBB, reg, body.intValue));
            node.intValue = reg;
        } else {
            node.intValue = body.intValue;
        }

        // if need memory operation, introduce temporary register
        if (isMemOp) {
            reg = new VirtualRegister(null);
            curBB.append(new BinaryOperation(curBB, reg, op, body.intValue, one));
            curBB.append(new Store(curBB, reg, body.exprType.getRegisterSize(), addr, offset));
            if (!isPostfix) node.intValue = reg;
        } else {
            curBB.append(new BinaryOperation(curBB, (Register) body.intValue, op, body.intValue, one));
        }
    }

    @Override
    public void visit(SelfDecrement node) {
        processSelfIncDec(node.self, node, false, true);
    }

    @Override
    public void visit(SelfIncrement node) {
        processSelfIncDec(node.self, node, true, true);
    }

    @Override
    public void visit(Identifier node) {
        SymbolInfo info = node.symbolInfo;
        node.intValue = info.register;
        if (node.ifTrue != null) {
            curBB.end(new Branch(curBB, node.intValue, node.ifTrue, node.ifFalse));
        }
    }

    @Override
    public void visit(BoolConst node) {
        node.intValue = new IntImmediate(node.value ? 1 : 0);
    }

    @Override
    public void visit(IntConst node) {
        node.intValue = new IntImmediate(node.value);
    }

    @Override
    public void visit(StringConst node) {
        StaticString ss = irRoot.stringPool.get(node.value);
        if (ss == null) {
            ss = new StaticString(node.value);
            irRoot.stringPool.put(node.value, ss);
        }
        node.intValue = ss;
    }

    @Override
    public void visit(NullLiteral node) {
        node.intValue = new IntImmediate(0);
    }

    @Override
    public void visit(Expr node) {
        node.accept(this);
    }

    @Override
    public void visit(Stmt node) {
        node.accept(this);
    }

    @Override
    public void visit(Decl node) {
        node.accept(this);
    }

    @Override
    public void visit(ASTNode node) {

    }

    @Override
    public void visit(BinaryExpr.BinaryOp node) {

    }

    @Override
    public void visit(UnaryExpr.UnaryOp node) {

    }
}
