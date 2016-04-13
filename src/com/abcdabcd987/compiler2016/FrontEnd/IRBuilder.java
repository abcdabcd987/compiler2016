package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.IR.BinaryOperation.BinaryOp;
import com.abcdabcd987.compiler2016.IR.IntComparison.Condition;
import com.abcdabcd987.compiler2016.IR.UnaryOperation.UnaryOp;
import com.abcdabcd987.compiler2016.Symbol.ArrayType;
import com.abcdabcd987.compiler2016.Symbol.Type;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class IRBuilder implements IASTVisitor {
    private BasicBlock curBB;
    private BasicBlock curLoopStepBB;
    private BasicBlock curLoopAfterBB;
    private Function curFunction;
    private boolean getAddress = false;
    private IRRoot irRoot = new IRRoot();

    public IRRoot getIRRoot() {
        return irRoot;
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

    @Override
    public void visit(Program node) {
        node.decls.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(StructDecl node) {

    }

    @Override
    public void visit(VariableDecl node) {
        Alloca addr = new Alloca(node.name);
        curFunction.defineVarAddr(node.name, addr);
        curBB.append(addr);
        if (node.init != null) {
            if (isLogicalExpression(node.init)) {
                node.init.ifTrue = new BasicBlock(null);
                node.init.ifFalse = new BasicBlock(null);
            }
            node.init.accept(this);
            assign(addr, node.init);
        }
    }

    @Override
    public void visit(FunctionDecl node) {
        curFunction = new Function(node.name);
        irRoot.functions.add(curFunction);
        curBB = curFunction.getStartBB();
        node.argTypes.forEach(x -> x.accept(this));
        node.body.accept(this);
        curFunction = null;
    }

    @Override
    public void visit(ArrayTypeNode node) {

    }

    @Override
    public void visit(PrimitiveTypeNode node) {

    }

    @Override
    public void visit(BreakStmt node) {
        curBB.end(new Jump(curLoopAfterBB));
    }

    @Override
    public void visit(ContinueStmt node) {
        curBB.end(new Jump(curLoopStepBB));
    }

    @Override
    public void visit(ReturnStmt node) {
        visit(node.value);
        curBB.end(new Return(node.value.wordValue));
    }

    @Override
    public void visit(FunctionTypeNode node) {

    }

    @Override
    public void visit(StructTypeNode node) {

    }

    @Override
    public void visit(CompoundStmt node) {
        node.stmts.forEach(x -> {
            x.accept(this);
            if (x instanceof Expr) {
                Expr expr = (Expr) x;
                curBB.append(expr.wordValue.getIRNode());
            }
        });
    }

    @Override
    public void visit(IfStmt node) {
        BasicBlock BBTrue = new BasicBlock("if_true");
        BasicBlock BBFalse = new BasicBlock("if_false");
        BasicBlock BBMerge = new BasicBlock("if_merge");

        // branch instruction should be added by logical expression
        node.cond.ifTrue = BBTrue;
        node.cond.ifFalse = BBFalse;
        visit(node.cond);

        // generate then
        curBB = BBTrue;
        visit(node.then);
        BBTrue.append(new Jump(BBMerge));

        // generate else
        if (node.otherwise != null) {
            curBB = BBFalse;
            visit(node.otherwise);
        }
        BBFalse.append(new Jump(BBMerge));

        // merge
        curBB = BBMerge;
    }

    @Override
    public void visit(ForLoop node) {
        BasicBlock BBCond = new BasicBlock("for_cond");
        BasicBlock BBLoop = new BasicBlock("for_loop");
        BasicBlock BBStep = new BasicBlock("for_step");
        BasicBlock BBAfter = new BasicBlock("for_after");

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBStep;
        curLoopAfterBB = BBAfter;

        // generate init
        if (node.init != null) visit(node.init);
        else node.initWithDecl.forEach(x -> x.accept(this));

        // jump to condition check
        curBB.end(new Jump(BBCond));

        // generate condition check
        curBB = BBCond;
        node.cond.ifTrue = BBStep;
        node.cond.ifFalse = BBAfter;
        visit(node.cond);

        // generate loop
        curBB = BBLoop;
        visit(node.body);

        // generate step
        if (node.step != null) {
            curBB = BBStep;
            visit(node.step);
        }

        // exit loop
        curLoopStepBB = oldLoopCondBB;
        curLoopAfterBB = oldLoopAfterBB;
        curBB = BBAfter;
    }

    @Override
    public void visit(WhileLoop node) {
        BasicBlock BBCond = new BasicBlock("while_cond");
        BasicBlock BBLoop = new BasicBlock("while_loop");
        BasicBlock BBAfter = new BasicBlock("while_after");

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBCond;
        curLoopAfterBB = BBAfter;

        // jump to condition check
        curBB.end(new Jump(BBCond));

        // generate condition check
        curBB = BBCond;
        node.cond.ifTrue = BBLoop;
        node.cond.ifFalse = BBAfter;
        visit(node.cond);

        // generate loop
        curBB = BBLoop;
        visit(node.body);

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
        visit(node.array);
        visit(node.subscript);
        WordValue tmp1 = new IntImmediate(((ArrayType)node.array.exprType).bodyType.getSize());
        WordValue tmp2 = new BinaryOperation(BinaryOp.MUL, node.subscript.wordValue, tmp1);
        WordValue tmp3 = new BinaryOperation(BinaryOp.ADD, node.array.wordValue, tmp2);
        node.wordValue = new Load(tmp3, null);
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
        switch (node.op) {
            case INC:
                node.wordValue = new BinaryOperation(BinaryOp.ADD,
                        node.body.wordValue, new IntImmediate(1));
                break;
            case DEC:
                node.wordValue = new BinaryOperation(BinaryOp.SUB,
                        node.body.wordValue, new IntImmediate(1));
                break;

            case POS:
                node.wordValue = node.body.wordValue;
                break;
            case NEG:
                node.wordValue = new UnaryOperation(UnaryOp.NEG, node.body.wordValue, null);
                break;

            case BITWISE_NOT:
            default:
                node.wordValue = new UnaryOperation(UnaryOp.NOT, node.body.wordValue, null);
                break;
        }
    }

    private void processLogicalBinaryExpr(BinaryExpr node) {
        // check lhs
        if (node.op == BinaryExpr.BinaryOp.LOGICAL_AND) {
            node.lhs.ifTrue = new BasicBlock("lhs_true");
            node.lhs.ifFalse = node.ifFalse;
            visit(node.lhs);
            curBB = node.lhs.ifTrue;
        } else {
            node.lhs.ifTrue = node.ifTrue;
            node.lhs.ifFalse = new BasicBlock("lhs_false");
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
        WordValue cmp = new IntComparison(cond, node.lhs.wordValue, node.rhs.wordValue);
        if (node.ifTrue != null) {
            // if the expression is in a condition part
            curBB.end(new Branch(cmp, node.ifTrue, node.ifFalse));
        } else {
            // if the expression is in an assignment
            node.wordValue = cmp;
        }
    }

    private void processStringRelationalExpr(BinaryExpr node) {

    }

    private void processArithmeticExpr(BinaryExpr node) {
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
        node.wordValue = new BinaryOperation(op, node.lhs.wordValue, node.rhs.wordValue);
    }

    private void assign(WordValue addr, Expr rhs) {
        if (rhs.ifTrue != null) {
            // for short-circuit evaluation
            BasicBlock merge = new BasicBlock(null);
            rhs.ifTrue.append(new Store(addr, new IntImmediate(1)));
            rhs.ifTrue.end(new Jump(merge));
            rhs.ifFalse.append(new Store(addr, new IntImmediate(0)));
            rhs.ifFalse.end(new Jump(merge));
            curBB = merge;
        } else {
            curBB.append(new Store(addr, rhs.wordValue));
        }
    }

    private void processAssign(BinaryExpr node) {
        // build rhs
        if (isLogicalExpression(node.rhs)) {
            node.rhs.ifTrue = new BasicBlock(null);
            node.rhs.ifFalse = new BasicBlock(null);
        }
        visit(node.rhs);

        // build the address of lhs
        getAddress = true;
        visit(node.lhs);
        getAddress = false;

        // build assignment
        assign(node.lhs.wordValue, node.rhs);
        node.wordValue = node.rhs.wordValue;
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
                if (node.lhs.exprType.type == Type.Types.INT) processIntRelationalExpr(node);
                else processStringRelationalExpr(node);
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
                processArithmeticExpr(node);
                break;
        }
    }

    @Override
    public void visit(EmptyExpr node) {
        // do nothing
    }

    @Override
    public void visit(FunctionCall node) {

    }

    @Override
    public void visit(NewExpr node) {

    }

    @Override
    public void visit(MemberAccess node) {

    }

    @Override
    public void visit(SelfDecrement node) {

    }

    @Override
    public void visit(SelfIncrement node) {

    }

    @Override
    public void visit(Identifier node) {
        if (getAddress) {
            node.wordValue = curFunction.getVarAddr(node.name);
        } else {
            node.wordValue = new Load(curFunction.getVarAddr(node.name), node.name);
            if (node.ifTrue != null) {
                curBB.end(new Branch(node.wordValue, node.ifTrue, node.ifFalse));
            }
        }
    }

    @Override
    public void visit(BoolConst node) {

    }

    @Override
    public void visit(IntConst node) {
        node.wordValue = new IntImmediate(node.value);
    }

    @Override
    public void visit(StringConst node) {

    }

    @Override
    public void visit(NullLiteral node) {

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
