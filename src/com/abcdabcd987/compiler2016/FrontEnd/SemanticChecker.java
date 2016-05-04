package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.Symbol.*;

import java.util.Stack;

import static com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable.*;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class SemanticChecker implements IASTVisitor {
    private GlobalSymbolTable globalSymbolTable;
    private SymbolTable currentScope;
    private CompilationError ce;

    private Stack<ASTNode> loopASTStack = new Stack<>();
    private ASTNode currentFunction;
    private FunctionType currentFunctionType;

    public SemanticChecker(GlobalSymbolTable globalSymbolTable, CompilationError ce) {
        this.globalSymbolTable = globalSymbolTable;
        this.ce = ce;
        this.currentScope = globalSymbolTable.globals;
    }

    @Override
    public void visit(Program node) {
        node.scope = globalSymbolTable.globals;
        node.decls.stream().forEachOrdered(x -> x.accept(this));
    }

    @Override
    public void visit(StructDecl node) {
        node.scope = globalSymbolTable.globals;
    }

    @Override
    public void visit(VariableDecl node) {
        node.scope = currentScope;
        if (currentScope.getTypeCurrent(node.name) != null) {
            ce.add(node.posName, node.name + " has already been declared.");
            return;
        }
        Type type = globalSymbolTable.resolveVariableTypeFromAST(node.type);
        if (type == null) {
            ce.add(node.posType, "Cannot resolve type.");
            return;
        }
        if (type.type == Type.Types.VOID) {
            ce.add(node.posType, "Variable declaration does not allow `void` type.");
            return;
        }

        if (node.init != null) {
            visit(node.init);
            if (!type.isSameType(node.init.exprType)) {
                ce.add(node.posInit, "The initializer must match the type of the declaration.");
                return;
            }
        }

        currentScope.define(node.name, type);
    }

    @Override
    public void visit(FunctionDecl node) {
        node.scope = currentScope;
        currentFunction = node.body;
        currentFunctionType = (FunctionType) globalSymbolTable.globals.getType(node.name);
        currentScope = new SymbolTable(globalSymbolTable.globals);
        node.argTypes.stream().forEachOrdered(x -> {
            currentScope.define(x.name, globalSymbolTable.resolveVariableTypeFromAST(x.type));
            x.scope = currentScope;
        });

        visit(node.body);

        currentFunction = null;
        currentFunctionType = null;
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void visit(ArrayTypeNode node) {

    }

    @Override
    public void visit(PrimitiveTypeNode node) {

    }

    @Override
    public void visit(BreakStmt node) {
        node.scope = currentScope;
        if (loopASTStack.empty()) {
            ce.add(node.pos, "Break statement should only exist inside a loop.");
            return;
        }
    }

    @Override
    public void visit(ContinueStmt node) {
        node.scope = currentScope;
        if (loopASTStack.empty()) {
            ce.add(node.pos, "Continue statement should only exist inside a loop.");
            return;
        }
    }

    @Override
    public void visit(ReturnStmt node) {
        node.scope = currentScope;
        if (currentFunction == null) {
            ce.add(node.pos, "Return statement should only exist inside a function.");
            return;
        }
        Type retType;
        if (node.value != null) {
            visit(node.value);
            retType = node.value.exprType;
        } else {
            retType = voidType;
        }
        if (!currentFunctionType.returnType.isSameType(retType)) {
            ce.add(node.posValue, "Expect `" + currentFunctionType.returnType + "` return type, but got `" + retType + "`");
            return;
        }
    }

    @Override
    public void visit(FunctionTypeNode node) {

    }

    @Override
    public void visit(StructTypeNode node) {

    }

    @Override
    public void visit(CompoundStmt node) {
        node.scope = currentScope;
        node.stmts.stream().forEachOrdered(x -> x.accept(this));
    }

    @Override
    public void visit(IfStmt node) {
        node.scope = currentScope;
        visit(node.cond);
        Type condType = node.cond.exprType;
        if (!condType.isSameType(boolType)) {
            ce.add(node.posCond, "Condition expression in a if-statement should be `bool`, but got `" + condType + "`.");
        }
        currentScope = new SymbolTable(currentScope);
        visit(node.then);
        currentScope = currentScope.getEnclosingScope();
        if (node.otherwise != null) {
            currentScope = new SymbolTable(currentScope);
            visit(node.otherwise);
            currentScope = currentScope.getEnclosingScope();
        }
    }

    @Override
    public void visit(ForLoop node) {
        node.scope = currentScope;
        currentScope = new SymbolTable(currentScope);

        if (node.init != null) {
            visit(node.init);
        } else if (node.initWithDecl != null) {
            node.initWithDecl.stream().forEachOrdered(x -> x.accept(this));
        }

        if (node.cond != null) {
            visit(node.cond);
            if (!node.cond.exprType.isSameType(boolType)) {
                ce.add(node.posCond, "Condition expression in a for-loop should be bool, but got `" + node.cond.exprType + "`.");
            }
        }

        if (node.step != null) {
            visit(node.step);
        }

        loopASTStack.push(node);
        visit(node.body);
        loopASTStack.pop();

        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void visit(WhileLoop node) {
        node.scope = currentScope;
        currentScope = new SymbolTable(currentScope);

        visit(node.cond);
        if (!node.cond.exprType.isSameType(boolType)) {
            ce.add(node.posCond, "Condition expression in a while-loop should be bool, but got `" + node.cond.exprType + "`.");
        }
        loopASTStack.push(node);
        visit(node.body);
        loopASTStack.pop();

        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void visit(VariableDeclStmt node) {
        node.scope = currentScope;
        visit(node.decl);
    }

    // Expression Visitor should set:
    //     node.exprType
    //     node.isLvalue

    @Override
    public void visit(ArrayAccess node) {
        node.scope = currentScope;
        visit(node.array);
        visit(node.subscript);
        if (node.array.exprType.type != Type.Types.ARRAY) {
            ce.add(node.posArray, "Array access occurs at non-array expression.");
            return;
        }
        if (node.subscript.exprType.type != Type.Types.INT) {
            ce.add(node.posSubscript, "Array subscript expression should be `int`, but got `" + node.subscript.exprType + "`.");
            return;
        }
        node.exprType = ((ArrayType)node.array.exprType).bodyType;
        node.isLvalue = true;
    }

    @Override
    public void visit(UnaryExpr node) {
        node.scope = currentScope;
        visit(node.body);
        switch (node.op) {
            case INC:
            case DEC:
                if (!node.body.isLvalue) {
                    ce.add(node.posOp, "Self increment/decrement can only be operated on lvalues.");
                    return;
                }
                if (node.body.exprType.type != Type.Types.INT) {
                    ce.add(node.posOp, "Self increment/decrement can only be operated on `int`.");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = true;
                return;

            case POS:
            case NEG:
            case BITWISE_NOT:
                if (node.body.exprType.type != Type.Types.INT) {
                    ce.add(node.posOp, "Unary +/-/~ can only be operated on `int`.");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = false;
                return;

            case LOGICAL_NOT:
                if (node.body.exprType.type != Type.Types.BOOL) {
                    ce.add(node.posOp, "Logical not can only be operated on `bool`.");
                    return;
                }
                node.exprType = boolType;
                node.isLvalue = false;
                return;
        }
    }

    @Override
    public void visit(BinaryExpr node) {
        node.scope = currentScope;
        visit(node.lhs);
        visit(node.rhs);
        if (!node.lhs.exprType.isSameType(node.rhs.exprType)) {
            ce.add(node.posRhs, "The left and right operands of a binary expression should be of the same value.");
            return;
        }
        Type.Types operandType = node.lhs.exprType.type;
        switch (node.op) {
            case ASSIGN:
                if (!node.lhs.isLvalue) {
                    ce.add(node.posLhs, "The left operand of assignment expression should be a lvalue.");
                    return;
                }
                node.exprType = node.rhs.exprType;
                node.isLvalue = true;
                return;

            case LOGICAL_OR:
            case LOGICAL_AND:
                if (operandType != Type.Types.BOOL) {
                    ce.add(node.posLhs, "The operands of logical or/and should be a `bool`");
                    return;
                }
                node.exprType = boolType;
                node.isLvalue = false;
                return;

            case EQ:
            case NE:
                node.exprType = boolType;
                node.isLvalue = false;
                return;

            case LT:
            case GT:
            case LE:
            case GE:
                if (operandType != Type.Types.INT && operandType != Type.Types.STRING) {
                    ce.add(node.posLhs, "The operands of relational operation should be a `int` or a `string`");
                    return;
                }
                node.exprType = boolType;
                node.isLvalue = false;
                return;

            case BITWISE_OR:
            case BITWISE_AND:
            case XOR:
            case SHL:
            case SHR:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (operandType != Type.Types.INT) {
                    ce.add(node.posLhs, "The operands of binary arithmetic operation should be a `int`");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = false;
                return;

            case ADD:
                if (operandType != Type.Types.INT && operandType != Type.Types.STRING) {
                    ce.add(node.posLhs, "The operands of binary addition operation should be a `int` or a `string`");
                    return;
                }
                node.exprType = operandType == Type.Types.INT ? intType : stringType;
                node.isLvalue = false;
        }
    }

    @Override
    public void visit(EmptyExpr node) {
        node.scope = currentScope;
        node.exprType = voidType;
        node.isLvalue = false;
    }

    @Override
    public void visit(FunctionCall node) {
        node.scope = currentScope;

        visit(node.name);
        FunctionType functionType = (FunctionType)node.name.exprType;
        if (functionType.type != Type.Types.FUNCTION) {
            ce.add(node.posName, "Function call on non-function type.");
            return;
        }
        boolean hasThis = GlobalSymbolTable.arrayBuiltinMethods.containsValue(functionType) || GlobalSymbolTable.stringBuiltinMethods.containsValue(functionType);
        int offset = hasThis ? 1 : 0;
        if (functionType.argTypes.size() != node.parameters.size() + offset) {
            ce.add(node.posArgs.get(node.posArgs.size()-1), "Function call expect " + functionType.argTypes.size() + "arguments, but got " + node.parameters.size() + ".");
            return;
        }
        for (int i = offset; i < functionType.argTypes.size(); ++i) {
            visit(node.parameters.get(i - offset));
            VariableType targetType = functionType.argTypes.get(i);
            Type sourceType = node.parameters.get(i - offset).exprType;
            if (!targetType.isSameType(sourceType)) {
                ce.add(node.posArgs.get(i - offset), "The " + (i+1-offset) + "# argument of function call expect " + targetType + ", but got " + sourceType + ".");
                return;
            }
        }
        if (hasThis)
            node.argThis = ((MemberAccess)node.name).record;

        node.isLvalue = false;
        node.exprType = functionType.returnType;
    }

    @Override
    public void visit(NewExpr node) {
        node.scope = currentScope;
        VariableType type = globalSymbolTable.resolveVariableTypeFromAST(node.type);
        for (int i = 0; i < node.dim.size(); ++i) {
            Expr x = node.dim.get(i);
            if (x == null) break;
            visit(x);
            if (x.exprType.type != Type.Types.INT) {
                ce.add(node.posDim.get(i), "Dimension expression in a new-expression should be `int`.");
                return;
            }
        }
        for (int i = 0; i < node.dim.size(); ++i)
            type = new ArrayType(type);
        node.exprType = type;
        node.isLvalue = false;
    }

    @Override
    public void visit(MemberAccess node) {
        node.scope = currentScope;
        visit(node.record);
        Type.Types recordType = node.record.exprType.type;
        if (recordType == Type.Types.STRUCT) {
            StructType s = (StructType) node.record.exprType;
            VariableType t = (VariableType) s.members.getTypeCurrent(node.member);
            if (t == null) {
                ce.add(node.posMember, "Struct " + s + " has no member named " + node.member + ".");
                return;
            }
            node.isLvalue = true;
            node.exprType = t;
        } else if (recordType == Type.Types.STRING) {
            FunctionType t = GlobalSymbolTable.stringBuiltinMethods.get(node.member);
            if (t == null) {
                ce.add(node.posMember, "No builtin string method named " + node.member + ".");
                return;
            }
            node.isLvalue = false;
            node.exprType = t;
        } else if (recordType == Type.Types.ARRAY) {
            FunctionType t = GlobalSymbolTable.arrayBuiltinMethods.get(node.member);
            if (t == null) {
                ce.add(node.posMember, "No builtin array method named " + node.member + ".");
                return;
            }
            node.isLvalue = false;
            node.exprType = t;
        } else {
            ce.add(node.posRecord, "Member access should only occur at struct type or string.");
            return;
        }
    }

    @Override
    public void visit(SelfDecrement node) {
        node.scope = currentScope;
        visit(node.self);
        if (!node.isLvalue) {
            ce.add(node.posSelf, "Self decrement should only operate on a lvalue.");
            return;
        }
        if (node.self.exprType.type != Type.Types.INT) {
            ce.add(node.posSelf, "Self decrement should only operate on a `int`.");
            return;
        }
        node.exprType = intType;
        node.isLvalue = false;
    }

    @Override
    public void visit(SelfIncrement node) {
        node.scope = currentScope;
        visit(node.self);
        if (!node.isLvalue) {
            ce.add(node.posSelf, "Self increment should only operate on a lvalue.");
            return;
        }
        if (node.self.exprType.type != Type.Types.INT) {
            ce.add(node.posSelf, "Self increment should only operate on a `int`.");
            return;
        }
        node.exprType = intType;
        node.isLvalue = false;
    }

    @Override
    public void visit(Identifier node) {
        node.scope = currentScope;
        SymbolInfo info = currentScope.getInfo(node.name);
        if (info == null) {
            ce.add(node.pos, "Cannot resolve symbol `" + node.name + "`.");
            return;
        }
        Type t = info.getType();
        node.symbolInfo = info;
        node.exprType = t;
        node.isLvalue = t.type != Type.Types.FUNCTION;
    }

    @Override
    public void visit(BoolConst node) {
        node.scope = currentScope;
        node.exprType = boolType;
        node.isLvalue = false;
    }

    @Override
    public void visit(IntConst node) {
        node.scope = currentScope;
        node.exprType = intType;
        node.isLvalue = false;
    }

    @Override
    public void visit(StringConst node) {
        node.scope = currentScope;
        node.exprType = stringType;
        node.isLvalue = false;
    }

    @Override
    public void visit(NullLiteral node) {
        node.scope = currentScope;
        node.exprType = nullType;
        node.isLvalue = false;
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
        node.accept(this);
    }

    @Override
    public void visit(BinaryExpr.BinaryOp node) {

    }

    @Override
    public void visit(UnaryExpr.UnaryOp node) {

    }
}
