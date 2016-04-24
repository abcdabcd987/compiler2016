package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.Symbol.*;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class StructFunctionDeclarator implements IASTVisitor {
    private GlobalSymbolTable symbolTable;
    private CompilationError ce;

    public StructFunctionDeclarator(GlobalSymbolTable symbolTable, CompilationError ce) {
        this.symbolTable = symbolTable;
        this.ce = ce;
    }

    @Override
    public void visit(Program node) {
        node.decls.stream().forEachOrdered(x -> x.accept(this));
    }

    @Override
    public void visit(StructDecl node) {
        StructType s = (StructType) symbolTable.resolveType(node.name);
        node.members.stream().forEachOrdered(x -> {
            if (x.init != null) {
                ce.add(x.posInit, "Initializer is not allowed in struct declaration.");
                return;
            }
            if (s.members.getTypeCurrent(x.name) != null) {
                ce.add(x.posName, node.name + " has already been declared.");
                return;
            }
            VariableType type = symbolTable.resolveVariableTypeFromAST(x.type);
            if (type == null) {
                ce.add(x.posType, "Cannot resolve type.");
                return;
            }
            s.members.define(x.name, type);
        });
    }

    @Override
    public void visit(VariableDecl node) {

    }

    @Override
    public void visit(FunctionDecl node) {
        if (symbolTable.globals.getType(node.name) != null) {
            ce.add(node.posName, node.name + " has already been declared.");
            return;
        }
        VariableType returnType = symbolTable.resolveVariableTypeFromAST(node.returnType);
        if (returnType == null) {
            ce.add(node.posReturnType, "Cannot resolve type");
            return;
        }
        if (node.name.equals("main") && (returnType.type != Type.Types.INT || node.argTypes.size() != 0)) {
            ce.add(node.posName, "`main` function is only allowed to have `int main();`.");
            return;
        }
        FunctionType func = new FunctionType(returnType, node.name);
        boolean success = true;
        for (VariableDecl x : node.argTypes) {
            if (x.init != null) {
                ce.add(x.posInit, "Initializer is not allowed in parameter declaration.");
                return;
            }
            VariableType type = symbolTable.resolveVariableTypeFromAST(x.type);
            if (type == null) {
                ce.add(x.posType, "Cannot resolve type.");
                success = false;
                break;
            }
            func.argTypes.add(type);
            func.argNames.add(x.name);
        }
        if (success) {
            node.functionType = func;
            symbolTable.globals.define(node.name, func);
        }
    }

    @Override
    public void visit(ArrayTypeNode node) {

    }

    @Override
    public void visit(PrimitiveTypeNode node) {

    }

    @Override
    public void visit(BreakStmt node) {

    }

    @Override
    public void visit(ContinueStmt node) {

    }

    @Override
    public void visit(ReturnStmt node) {

    }

    @Override
    public void visit(FunctionTypeNode node) {

    }

    @Override
    public void visit(StructTypeNode node) {

    }

    @Override
    public void visit(CompoundStmt node) {

    }

    @Override
    public void visit(IfStmt node) {

    }

    @Override
    public void visit(ForLoop node) {

    }

    @Override
    public void visit(WhileLoop node) {

    }

    @Override
    public void visit(VariableDeclStmt node) {

    }

    @Override
    public void visit(ArrayAccess node) {

    }

    @Override
    public void visit(UnaryExpr node) {

    }

    @Override
    public void visit(BinaryExpr node) {

    }

    @Override
    public void visit(EmptyExpr node) {

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

    }

    @Override
    public void visit(BoolConst node) {

    }

    @Override
    public void visit(IntConst node) {

    }

    @Override
    public void visit(StringConst node) {

    }

    @Override
    public void visit(NullLiteral node) {

    }

    @Override
    public void visit(Expr node) {

    }

    @Override
    public void visit(Stmt node) {

    }

    @Override
    public void visit(Decl node) {

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
