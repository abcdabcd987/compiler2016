package com.abcdabcd987.compiler2016.Parser;

import com.abcdabcd987.compiler2016.AST.*;

/**
 * Created by abcdabcd987 on 2016-03-28.
 */
public class ASTPrintVisitor implements IASTVisitor {
    private StringBuilder sb = new StringBuilder();

    private void indent() {
        sb.append("  ");
    }

    private void dedent() {
        sb.delete(sb.length()-2, sb.length());
    }

    @Override
    public void visit(ArrayAccess node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.array);
        visit(node.subscript);
        dedent();
    }

    @Override
    public void visit(ArrayType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.baseType);
        visit(node.size);
        dedent();
    }

    @Override
    public void visit(BinaryExpr node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.op);
        visit(node.lhs);
        visit(node.rhs);
        dedent();
    }

    @Override
    public void visit(BoolConst node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + " value:" + node.value);
    }

    @Override
    public void visit(BoolType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(BreakStmt node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(CompoundStmt node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        node.decls.stream().forEachOrdered(this::visit);
        node.stmts.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(ContinueStmt node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(EmptyExpr node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(Expr node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(ForLoop node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        if (node.initWithDecl != null) node.initWithDecl.stream().forEachOrdered(this::visit);
        else visit(node.init);
        visit(node.cond);
        visit(node.step);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(FunctionCall node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.name);
        node.parameters.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(FunctionDecl node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.returnType);
        visit(node.name);
        node.argTypes.stream().forEachOrdered(this::visit);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(Identifier node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
    }

    @Override
    public void visit(IfStmt node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.cond);
        visit(node.then);
        visit(node.otherwise);
        dedent();
    }

    @Override
    public void visit(InitValue node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.expr);
        dedent();
    }

    @Override
    public void visit(IntConst node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.value);
    }

    @Override
    public void visit(IntType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(NewExpr node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.type);
        dedent();
    }

    @Override
    public void visit(NullLiteral node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(Program node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        node.recordDecl.stream().forEachOrdered(this::visit);
        node.variableDecl.stream().forEachOrdered(this::visit);
        node.functionDecl.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(RecordAccess node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.member);
        visit(node.record);
        dedent();
    }

    @Override
    public void visit(RecordDecl node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.name);
        node.members.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(RecordType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
    }

    @Override
    public void visit(ReturnStmt node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.value);
        dedent();
    }

    @Override
    public void visit(SelfDecrement node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.self);
        dedent();
    }

    @Override
    public void visit(SelfIncrement node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.self);
        dedent();
    }

    @Override
    public void visit(StringConst node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.value);
    }

    @Override
    public void visit(StringType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(Symbol node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node);
    }

    @Override
    public void visit(UnaryExpr node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.op);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(VariableDecl node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.type);
        visit(node.name);
        visit(node.init);
        dedent();
    }

    @Override
    public void visit(VoidType node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(WhileLoop node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.cond);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(Type node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(Stmt node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(BasicType node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(Decl node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(ASTNode node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(BinaryExpr.BinaryOp node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + node);
    }

    @Override
    public void visit(UnaryExpr.UnaryOp node) {
        if (node == null) return;
        System.out.println(sb.toString() + node.getClass().getSimpleName() + node);
    }

}
