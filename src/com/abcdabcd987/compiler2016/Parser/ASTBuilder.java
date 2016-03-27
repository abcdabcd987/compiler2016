package com.abcdabcd987.compiler2016.Parser;

import com.abcdabcd987.compiler2016.AST.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ASTBuilder extends MillBaseListener {
    public ParseTreeProperty<Object> map = new ParseTreeProperty<>();

    // programSection: classDeclaration
    @Override
    public void exitClass(MillParser.ClassContext ctx) {
        map.put(ctx, map.get(ctx.classDeclaration()));
    }

    // programSection: functionDeclaration
    @Override
    public void exitFunc(MillParser.FuncContext ctx) {
        map.put(ctx, map.get(ctx.functionDeclaration()));
    }

    // programSection: variableDeclaration
    @Override
    public void exitVar(MillParser.VarContext ctx) {
        map.put(ctx, map.get(ctx.variableDeclaration()));
    }

    // statement: blockStatement
    @Override
    public void exitBlock(MillParser.BlockContext ctx) {
        map.put(ctx, map.get(ctx.blockStatement()));
    }

    // statement: expressionStatement
    @Override
    public void exitExpr(MillParser.ExprContext ctx) {
        map.put(ctx, map.get(ctx.expressionStatement()));
    }

    // statement: selectionStatement
    @Override
    public void exitSelect(MillParser.SelectContext ctx) {
        map.put(ctx, map.get(ctx.selectionStatement()));
    }

    // statement: iterationStatement
    @Override
    public void exitIter(MillParser.IterContext ctx) {
        map.put(ctx, map.get(ctx.iterationStatement()));
    }

    // statement: jumpStatement
    @Override
    public void exitJump(MillParser.JumpContext ctx) {
        map.put(ctx, map.get(ctx.jumpStatement()));
    }

    // blockStatement: '{' blockItem* '}'
    @Override
    public void exitBlockStatement(MillParser.BlockStatementContext ctx) {
        CompoundStmt.Builder builder = new CompoundStmt.Builder();
        ctx.blockItem().stream().map(map::get).forEachOrdered(builder::add);
        map.put(ctx, builder.build());
    }

    // blockItem: variableDeclaration
    @Override
    public void exitVardecl(MillParser.VardeclContext ctx) {
        map.put(ctx, map.get(ctx.variableDeclaration()));
    }

    // blockItem: statement
    @Override
    public void exitStmt(MillParser.StmtContext ctx) {
        map.put(ctx, map.get(ctx.statement()));
    }

    // expressionStatement: expression? ';'
    @Override
    public void exitExpressionStatement(MillParser.ExpressionStatementContext ctx) {
        map.put(ctx, ctx.expression() != null ? map.get(ctx.expression()) : new EmptyExpr());
    }

    // selectionStatement: 'if' '(' expression ')' statement ('else' statement)?
    @Override
    public void exitSelectionStatement(MillParser.SelectionStatementContext ctx) {
        map.put(ctx, new IfStmt(
                (Expr)map.get(ctx.expression()),
                (Stmt)map.get(ctx.statement(0)),
                ctx.statement(1) == null ? null : (Stmt)map.get(ctx.statement(1))));
    }

    // iterationStatement: 'while' '(' expression ')' statement
    @Override
    public void exitWhile(MillParser.WhileContext ctx) {
        map.put(ctx, new WhileLoop(
                (Expr)map.get(ctx.expression()),
                (Stmt)map.get(ctx.statement())));
    }

    // iterationStatement: 'for' '(' (variableDeclaration | expression)? ';'
    //                               expression? ';'
    //                               expression? ')'
    //                     statement
    @Override
    public void exitFor(MillParser.ForContext ctx) {
        VariableDecl initWithDecl = (VariableDecl)map.get(ctx.variableDeclaration());
        Expr init = (Expr)map.get(ctx.expression(0));
        Expr cond = (Expr)map.get(ctx.expression(1));
        Expr step = (Expr)map.get(ctx.expression(2));
        Stmt body = (Stmt)map.get(ctx.statement());
        if (init != null) map.put(ctx, new ForLoop(init, cond, step, body));
        else map.put(ctx, new ForLoop(initWithDecl, cond, step, body));
    }

    // jumpStatement: 'continue' ';'
    @Override
    public void exitContinue(MillParser.ContinueContext ctx) {
        map.put(ctx, new ContinueStmt());
    }

    // jumpStatement: 'break' ';'
    @Override
    public void exitBreak(MillParser.BreakContext ctx) {
        map.put(ctx, new BreakStmt());
    }

    // jumpStatement: 'return' expression? ';'
    @Override
    public void exitReturn(MillParser.ReturnContext ctx) {
        map.put(ctx, new ReturnStmt(
                ctx.expression() != null ? (Expr)map.get(ctx.expression()) : null));
    }

    // nonArrayTypeSpecifier
    //     :   type='int'
    //     |   type='bool'
    //     |   type='string'
    //     |   type='void'
    //     |   type=Identifier
    //     ;
    @Override
    public void exitNonArrayTypeSpecifier(MillParser.NonArrayTypeSpecifierContext ctx) {
        switch (ctx.type.getType()) {
            case MillParser.Int: map.put(ctx, new IntType()); break;
            case MillParser.Bool: map.put(ctx, new BoolType()); break;
            case MillParser.String: map.put(ctx, new StringType()); break;
            case MillParser.Void: map.put(ctx, new VoidType()); break;
            case MillParser.Identifier:
                map.put(ctx, new RecordType(Symbol.get(ctx.Identifier().getText())));
                break;
            default: throw new RuntimeException("Unhandled type in `nonArrayTypeSpecifier`");
        }
    }

    // typeSpecifier: typeSpecifier '[' ']'
    @Override
    public void exitArrayType(MillParser.ArrayTypeContext ctx) {
        map.put(ctx, new ArrayType((Type)map.get(ctx.typeSpecifier())));
    }

    // typeSpecifier: nonArrayTypeSpecifier
    @Override
    public void exitNonArrayType(MillParser.NonArrayTypeContext ctx) {
        map.put(ctx, map.get(ctx.nonArrayTypeSpecifier()));
    }

    // variableDeclaration: typeSpecifier variableInitDeclarator (',' variableInitDeclarator)* ';'
    // variableInitDeclarator: Identifier ('=' expression)?
    @Override
    public void exitVariableDeclaration(MillParser.VariableDeclarationContext ctx) {
        List<VariableDecl> decls = new ArrayList<>();
        Type type = (Type)map.get(ctx.typeSpecifier());
        ctx.variableInitDeclarator().stream().forEachOrdered(x -> {
            decls.add(new VariableDecl(
                    type,
                    Symbol.get(x.Identifier().getText()),
                    x.expression() != null ? (Expr)map.get(x.expression()) : null));
        });
        map.put(ctx, decls);
    }

    // classDeclaration: 'class' Identifier '{' memberDeclaration* '}'
    @Override
    public void exitClassDeclaration(MillParser.ClassDeclarationContext ctx) {
        RecordDecl.Builder builder = new RecordDecl.Builder();
        builder.setName(Symbol.get(ctx.Identifier().getText()));
        ctx.memberDeclaration().stream().map(map::get).forEachOrdered(builder::add);
        map.put(ctx, builder.build());
    }

    // memberDeclaration: typeSpecifier Identifier ';'
    @Override
    public void exitMemberDeclaration(MillParser.MemberDeclarationContext ctx) {
        map.put(ctx, new VariableDecl(
                (Type)map.get(ctx.typeSpecifier()),
                Symbol.get(ctx.Identifier().getText()),
                null
        ));
    }

    // functionDeclaration: typeSpecifier Identifier '(' parameterDeclarationList? ')' blockStatement
    // parameterDeclarationList: parameterDeclaration (',' parameterDeclaration)*
    @Override
    public void exitFunctionDeclaration(MillParser.FunctionDeclarationContext ctx) {
        FunctionDecl.Builder builder = new FunctionDecl.Builder() ;
        builder.setReturnType((Type)map.get(ctx.typeSpecifier()));
        builder.setName(Symbol.get(ctx.Identifier().getText()));
        builder.setBody((CompoundStmt)map.get(ctx.blockStatement()));
        if (ctx.parameterDeclarationList() != null)
            ctx.parameterDeclarationList().parameterDeclaration()
                    .stream().map(map::get).forEachOrdered(builder::addArgType);
        map.put(ctx, builder.build());
    }

    // parameterDeclaration: typeSpecifier Identifier
    @Override
    public void exitParameterDeclaration(MillParser.ParameterDeclarationContext ctx) {
        map.put(ctx, new VariableDecl(
                (Type)map.get(ctx.typeSpecifier()),
                Symbol.get(ctx.Identifier().getText()),
                null
        ));
    }
}
