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
        if (currentScope.resolveCurrent(node.name) != null) {
            ce.add(node.name + " has already been declared.");
            return;
        }
        Type type = globalSymbolTable.resolveVariableTypeFromAST(node.type);
        if (type == null) {
            ce.add("Cannot resolve type.");
            return;
        }

        if (node.init != null) {
            visit(node.init);
            if (!type.isSameType(node.init.exprType)) {
                ce.add("The initializer must match the type of the declaration.");
                return;
            }
        }

        currentScope.define(node.name, type);
    }

    @Override
    public void visit(FunctionDecl node) {
        node.scope = currentScope;
        currentFunction = node.body;
        currentFunctionType = (FunctionType) globalSymbolTable.globals.resolve(node.name);
        currentScope = new SymbolTable(globalSymbolTable.globals);
        node.argTypes.stream().forEachOrdered(x ->
                currentScope.define(x.name, globalSymbolTable.resolveVariableTypeFromAST(x.type)));

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
            ce.add("Break statement should only exist inside a loop.");
            return;
        }
    }

    @Override
    public void visit(ContinueStmt node) {
        node.scope = currentScope;
        if (loopASTStack.empty()) {
            ce.add("Continue statement should only exist inside a loop.");
            return;
        }
    }

    @Override
    public void visit(ReturnStmt node) {
        node.scope = currentScope;
        if (currentFunction == null) {
            ce.add("Return statement should only exist inside a function.");
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
            ce.add("Expect `" + currentFunctionType.returnType + "` return type, but got `" + retType + "`");
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
            ce.add("Condition expression in a if-statement should be `bool`, but got `" + condType + "`.");
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
                ce.add("Condition expression in a for-loop should be bool, but got `" + node.cond.exprType + "`.");
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
            ce.add("Condition expression in a while-loop should be bool, but got `" + node.cond.exprType + "`.");
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
            ce.add("Array access occurs at non-array expression.");
            return;
        }
        if (node.subscript.exprType.type != Type.Types.INT) {
            ce.add("Array subscript expression should be `int`, but got `" + node.subscript.exprType + "`.");
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
                    ce.add("Self increment/decrement can only be operated on lvalues.");
                    return;
                }
                if (node.body.exprType.type != Type.Types.INT) {
                    ce.add("Self increment/decrement can only be operated on `int`.");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = true;
                return;

            case POS:
            case NEG:
            case BITWISE_NOT:
                if (node.body.exprType.type != Type.Types.INT) {
                    ce.add("Unary +/-/~ can only be operated on `int`.");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = false;
                return;

            case LOGICAL_NOT:
                if (node.body.exprType.type != Type.Types.BOOL) {
                    ce.add("Logical not can only be operated on `bool`.");
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
        switch (node.op) {
            case ASSIGN:
                if (!node.lhs.isLvalue) {
                    ce.add("The left operand of assignment expression should be a lvalue.");
                    return;
                }
                if (!node.lhs.exprType.isSameType(node.rhs.exprType)) {
                    ce.add("The right operand of assignment expression has a different type of the right one.");
                    return;
                }
                node.exprType = node.rhs.exprType;
                node.isLvalue = true;
                return;

            case LOGICAL_OR:
            case LOGICAL_AND:
                if (node.lhs.exprType.type != Type.Types.BOOL) {
                    ce.add("The left operand of logical or/and should be a `bool`");
                    return;
                }
                if (node.rhs.exprType.type != Type.Types.BOOL) {
                    ce.add("The right operand of logical or/and should be a `bool`");
                    return;
                }
                node.exprType = boolType;
                node.isLvalue = false;
                return;

            case EQ:
            case NE:
                if (!node.lhs.exprType.isSameType(node.rhs.exprType)) {
                    ce.add("Operands of equality test should be of the same type.");
                    return;
                }
                node.exprType = boolType;
                node.isLvalue = false;
                return;

            case LT:
            case GT:
            case LE:
            case GE:
                if (node.lhs.exprType.type != Type.Types.INT) {
                    ce.add("The left operand of relational operation should be a `bool`");
                    return;
                }
                if (node.rhs.exprType.type != Type.Types.INT) {
                    ce.add("The right operand of relational operation should be a `bool`");
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
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (node.lhs.exprType.type != Type.Types.INT) {
                    ce.add("The left operand of binary arithmetic operation should be a `int`");
                    return;
                }
                if (node.rhs.exprType.type != Type.Types.INT) {
                    ce.add("The right operand of binary arithmetic operation should be a `int`");
                    return;
                }
                node.exprType = intType;
                node.isLvalue = false;
                return;
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
            ce.add("Function call on non-function type.");
            return;
        }
        if (functionType.argTypes.size() != node.parameters.size()) {
            ce.add("Function call expect " + functionType.argTypes.size() + "arguments, but got " + node.parameters.size() + ".");
            return;
        }
        for (int i = 0; i < functionType.argTypes.size(); ++i) {
            visit(node.parameters.get(i));
            VariableType targetType = functionType.argTypes.get(i);
            Type sourceType = node.parameters.get(i).exprType;
            if (!targetType.isSameType(sourceType)) {
                ce.add("The " + (i+1) + "# argument of function call expect " + targetType + ", but got " + sourceType + ".");
                return;
            }
        }

        node.isLvalue = false;
        node.exprType = functionType.returnType;
    }

    @Override
    public void visit(NewExpr node) {
        node.scope = currentScope;
        VariableType type = globalSymbolTable.resolveVariableTypeFromAST(node.type);
        node.dim.stream().forEachOrdered(x -> {
            visit(x);
            if (x.exprType.type != Type.Types.INT) {
                ce.add("Dimension expression in a new-expression should be `int`.");
                return;
            }
        });
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
            VariableType t = (VariableType) s.members.resolveCurrent(node.member);
            if (t == null) {
                ce.add("Struct " + s + " has no member named " + node.member + ".");
                return;
            }
            node.isLvalue = true;
            node.exprType = t;
        } else if (recordType == Type.Types.STRING) {
            FunctionType t = GlobalSymbolTable.stringBuiltinMethods.get(node.member);
            if (t == null) {
                ce.add("No builtin string method named " + node.member + ".");
                return;
            }
            node.isLvalue = false;
            node.exprType = t;
        } else {
            ce.add("Member access should only occur at struct type or string.");
        }
    }

    @Override
    public void visit(SelfDecrement node) {
        node.scope = currentScope;
        visit(node.self);
        if (!node.isLvalue) {
            ce.add("Self decrement should only operate on a lvalue.");
            return;
        }
        if (node.self.exprType.type != Type.Types.INT) {
            ce.add("Self decrement should only operate on a `int`.");
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
            ce.add("Self increment should only operate on a lvalue.");
            return;
        }
        if (node.self.exprType.type != Type.Types.INT) {
            ce.add("Self increment should only operate on a `int`.");
            return;
        }
        node.exprType = intType;
        node.isLvalue = false;
    }

    @Override
    public void visit(Identifier node) {
        node.scope = currentScope;
        Type t = currentScope.resolve(node.name);
        if (t == null) {
            ce.add("Cannot resolve symbol `" + node.name + "`.");
            return;
        }
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
