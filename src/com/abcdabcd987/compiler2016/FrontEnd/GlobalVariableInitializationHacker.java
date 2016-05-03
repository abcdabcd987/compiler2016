package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.Symbol.FunctionType;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import com.abcdabcd987.compiler2016.Symbol.PrimitiveType;
import com.abcdabcd987.compiler2016.Symbol.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class GlobalVariableInitializationHacker implements IASTVisitor {
    private GlobalSymbolTable sym;
    private List<Stmt> initBody = new ArrayList<>();

    public GlobalVariableInitializationHacker(GlobalSymbolTable sym) {
        this.sym = sym;
    }

    @Override
    public void visit(Program node) {
        node.decls.forEach(x -> x.accept(this));

        FunctionCall call = new FunctionCall(new Identifier("main", null), new ArrayList<>(), null, new ArrayList<>());
        ReturnStmt returnStmt = new ReturnStmt(call, null, null);
        initBody.add(returnStmt);

        FunctionDecl.Builder funcBuilder = new FunctionDecl.Builder();
        funcBuilder.setReturnType(new PrimitiveTypeNode(Type.Types.INT));
        funcBuilder.setName("__init");
        funcBuilder.setBody(new CompoundStmt(initBody));

        FunctionDecl decl = funcBuilder.build();
        decl.functionType = new FunctionType(GlobalSymbolTable.intType, "__init");
        node.decls.add(decl);
    }

    @Override
    public void visit(StructDecl node) {

    }

    @Override
    public void visit(VariableDecl node) {
        if (node.init != null) {
            Identifier id = new Identifier(node.name, node.posName);
            id.scope = sym.globals;
            BinaryExpr expr = new BinaryExpr(
                    BinaryExpr.BinaryOp.ASSIGN,
                    id,
                    node.init,
                    node.posType,
                    node.posName,
                    node.posInit);
            initBody.add(expr);
            node.init = null;
        }
    }

    @Override
    public void visit(FunctionDecl node) {

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
