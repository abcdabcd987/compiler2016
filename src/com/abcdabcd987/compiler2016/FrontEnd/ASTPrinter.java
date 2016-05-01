package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;

import java.io.PrintStream;

/**
 * Created by abcdabcd987 on 2016-03-28.
 */
public class ASTPrinter implements IASTVisitor {
    private PrintStream out;
    private StringBuilder sb = new StringBuilder();

    public ASTPrinter(PrintStream out) {
        this.out = out;
    }

    private void indent() {
        sb.append("  ");
    }

    private void dedent() {
        sb.delete(sb.length()-2, sb.length());
    }

    @Override
    public void visit(ArrayAccess node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.array);
        visit(node.subscript);
        dedent();
    }

    @Override
    public void visit(ArrayTypeNode node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.baseType);
        dedent();
    }

    @Override
    public void visit(BinaryExpr node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.op);
        visit(node.lhs);
        visit(node.rhs);
        dedent();
    }

    @Override
    public void visit(BoolConst node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue + ", value:" + node.value);
    }

    @Override
    public void visit(BreakStmt node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(CompoundStmt node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        node.stmts.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(ContinueStmt node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
    }

    @Override
    public void visit(EmptyExpr node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
    }

    @Override
    public void visit(Expr node) {
        if (node == null) return;
        node.accept(this);
    }

    @Override
    public void visit(ForLoop node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
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
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.name);
        node.parameters.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(FunctionDecl node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
        indent();
        visit(node.returnType);
        node.argTypes.stream().forEachOrdered(this::visit);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(Identifier node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue + ", name:" + node.name);
    }

    @Override
    public void visit(IfStmt node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.cond);
        visit(node.then);
        visit(node.otherwise);
        dedent();
    }

    @Override
    public void visit(IntConst node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue + ", value:" + node.value);
    }

    @Override
    public void visit(NewExpr node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.type);
        node.dim.forEach(this::visit);
        dedent();
    }

    @Override
    public void visit(NullLiteral node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
    }

    @Override
    public void visit(Program node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        node.decls.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(MemberAccess node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.record);
        out.println(sb.toString() + "member: " + node.member);
        dedent();
    }

    @Override
    public void visit(StructDecl node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
        indent();
        node.members.stream().forEachOrdered(this::visit);
        dedent();
    }

    @Override
    public void visit(StructTypeNode node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
    }

    @Override
    public void visit(ReturnStmt node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.value);
        dedent();
    }

    @Override
    public void visit(SelfDecrement node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.self);
        dedent();
    }

    @Override
    public void visit(SelfIncrement node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.self);
        dedent();
    }

    @Override
    public void visit(StringConst node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue + ", value:" + node.value);
    }

    @Override
    public void visit(UnaryExpr node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + " exprType:" + node.exprType + ", isLvalue:" + node.isLvalue);
        indent();
        visit(node.op);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(VariableDecl node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.name);
        indent();
        visit(node.type);
        visit(node.init);
        dedent();
    }

    @Override
    public void visit(WhileLoop node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
        indent();
        visit(node.cond);
        visit(node.body);
        dedent();
    }

    @Override
    public void visit(Stmt node) {
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
        out.println(sb.toString() + node.getClass().getSimpleName() + node);
    }

    @Override
    public void visit(UnaryExpr.UnaryOp node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + node);
    }

    @Override
    public void visit(VariableDeclStmt node) {
        if (node == null) return;
        out.print(sb.toString() + node.getClass().getSimpleName() + " -> ");
        if (node.decl != null) visit(node.decl);
        else out.println();
    }

    @Override
    public void visit(PrimitiveTypeNode node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName() + ": " + node.getType().name());
    }

    @Override
    public void visit(FunctionTypeNode node) {
        if (node == null) return;
        out.println(sb.toString() + node.getClass().getSimpleName());
    }
}
