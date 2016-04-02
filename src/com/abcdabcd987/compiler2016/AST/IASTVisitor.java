package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-28.
 */
public interface IASTVisitor {
    void visit(Program node);

    void visit(StructDecl node);
    void visit(VariableDecl node);
    void visit(FunctionDecl node);

    void visit(ArrayTypeNode node);
    void visit(PrimitiveTypeNode node);

    void visit(BreakStmt node);
    void visit(ContinueStmt node);
    void visit(ReturnStmt node);
    void visit(FunctionTypeNode node);
    void visit(StructTypeNode node);

    void visit(CompoundStmt node);
    void visit(IfStmt node);
    void visit(ForLoop node);
    void visit(WhileLoop node);
    void visit(VariableDeclStmt node);

    void visit(ArrayAccess node);
    void visit(UnaryExpr node);
    void visit(BinaryExpr node);
    void visit(EmptyExpr node);
    void visit(FunctionCall node);
    void visit(NewExpr node);
    void visit(MemberAccess node);
    void visit(SelfDecrement node);
    void visit(SelfIncrement node);

    void visit(Identifier node);
    void visit(BoolConst node);
    void visit(IntConst node);
    void visit(StringConst node);
    void visit(NullLiteral node);

    void visit(Expr node);
    void visit(Stmt node);
    void visit(Decl node);
    void visit(ASTNode node);
    void visit(BinaryExpr.BinaryOp node);
    void visit(UnaryExpr.UnaryOp node);
}
