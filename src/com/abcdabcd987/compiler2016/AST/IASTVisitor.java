package com.abcdabcd987.compiler2016.AST;

/**
 * Created by abcdabcd987 on 2016-03-28.
 */
public interface IASTVisitor {
    void visit(ArrayAccess node);
    void visit(ArrayType node);
    void visit(BinaryExpr node);
    void visit(BoolConst node);
    void visit(BreakStmt node);
    void visit(CompoundStmt node);
    void visit(ContinueStmt node);
    void visit(EmptyExpr node);
    void visit(Expr node);
    void visit(ForLoop node);
    void visit(FunctionCall node);
    void visit(FunctionDecl node);
    void visit(Identifier node);
    void visit(IfStmt node);
    void visit(InitValue node);
    void visit(IntConst node);
    void visit(NewExpr node);
    void visit(NullLiteral node);
    void visit(Program node);
    void visit(RecordAccess node);
    void visit(RecordDecl node);
    void visit(StructType node);
    void visit(ReturnStmt node);
    void visit(SelfDecrement node);
    void visit(SelfIncrement node);
    void visit(StringConst node);
    void visit(UnaryExpr node);
    void visit(VariableDecl node);
    void visit(WhileLoop node);
    void visit(Type node);
    void visit(Stmt node);
    void visit(Decl node);
    void visit(ASTNode node);
    void visit(BinaryExpr.BinaryOp node);
    void visit(UnaryExpr.UnaryOp node);
    void visit(VariableDeclStmt node);
    void visit(PrimitiveType node);
    void visit(FunctionType node);
}
