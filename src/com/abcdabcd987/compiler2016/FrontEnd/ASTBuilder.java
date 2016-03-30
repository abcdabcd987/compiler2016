package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.Parser.MillBaseListener;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class ASTBuilder extends MillBaseListener {
    private ParseTreeProperty<Object> map = new ParseTreeProperty<>();
    private Program program;

    public Program getProgram() {
        return program;
    }

    // program: programSection* EOF
    @Override
    public void exitProgram(MillParser.ProgramContext ctx) {
        Program.Builder builder = new Program.Builder();
        ctx.programSection().stream().map(map::get).forEachOrdered(builder::add);
        program = builder.build();
        map.put(ctx, program);
    }

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
        Object node = map.get(ctx.variableDeclaration());
        if (node instanceof List) {
            List<VariableDeclStmt> decls = ((List<VariableDecl>)node).stream()
                    .map(x -> new VariableDeclStmt(x)).collect(Collectors.toList());
            map.put(ctx, decls);
        } else {
            map.put(ctx, new VariableDeclStmt((VariableDecl) node));
        }
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
        List<VariableDecl> initWithDecl = (List<VariableDecl>)map.get(ctx.variableDeclaration());
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
        map.put(ctx, new ArrayType((Type)map.get(ctx.typeSpecifier()), null));
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
        ctx.variableInitDeclarator().stream()
                .forEachOrdered(x -> decls.add(new VariableDecl(
                    type,
                    Symbol.get(x.Identifier().getText()),
                    x.expression() != null ? (Expr)map.get(x.expression()) : null)));
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

    // expression: expression op=('++' | '--')
    @Override
    public void exitPostfixIncDec(MillParser.PostfixIncDecContext ctx) {
        Expr expr = (Expr)map.get(ctx.expression());
        if (ctx.op.getType() == MillParser.PlusPlus)
            map.put(ctx, new SelfIncrement(expr));
        else
            map.put(ctx, new SelfDecrement(expr));
    }

    // expression: Identifier '(' parameterList? ')'
    // parameterList: expression (',' expression)*
    @Override
    public void exitFunctionCall(MillParser.FunctionCallContext ctx) {
        FunctionCall.Builder builder = new FunctionCall.Builder();
        builder.setName((Expr)map.get(ctx.expression()));
        if (ctx.parameterList() != null)
            ctx.parameterList().expression().stream()
                    .map(map::get).forEachOrdered(builder::addArg);
        map.put(ctx, builder.build());
    }

    // expression: expression '[' expression ']'
    @Override
    public void exitSubscript(MillParser.SubscriptContext ctx) {
        map.put(ctx, new ArrayAccess(
                (Expr)map.get(ctx.expression(0)),
                (Expr)map.get(ctx.expression(1))
        ));
    }

    // expression: expression '.' Identifier
    @Override
    public void exitMemberAccess(MillParser.MemberAccessContext ctx) {
        map.put(ctx, new RecordAccess(
                (Expr)map.get(ctx.expression()),
                Symbol.get(ctx.Identifier().getText())
        ));
    }

    // expression
    //     :   <assoc=right> op=('++'|'--') expression
    //     |   <assoc=right> op=('+' | '-') expression
    //     |   <assoc=right> op=('!' | '~') expression
    @Override
    public void exitUnaryExpr(MillParser.UnaryExprContext ctx) {
        UnaryExpr.UnaryOp op;
        switch (ctx.op.getType()) {
            case MillParser.PlusPlus: op = UnaryExpr.UnaryOp.INC; break;
            case MillParser.MinusMinus: op = UnaryExpr.UnaryOp.DEC; break;
            case MillParser.Plus: op = UnaryExpr.UnaryOp.POS; break;
            case MillParser.Minus: op = UnaryExpr.UnaryOp.NEG; break;
            case MillParser.Not: op = UnaryExpr.UnaryOp.LOGICAL_NOT; break;
            case MillParser.Tilde: op = UnaryExpr.UnaryOp.BITWISE_NOT; break;
            default: throw new RuntimeException("Unknown unary op.");
        }
        map.put(ctx, new UnaryExpr(
                op,
                (Expr)map.get(ctx.expression())
        ));
    }

    // expression: <assoc=right> 'new' creator
    @Override
    public void exitNew(MillParser.NewContext ctx) {
        map.put(ctx, new NewExpr((Type)map.get(ctx.creator())));
    }

    // expression
    //     :   expression op=('*' | '/' | '%') expression
    //     |   expression op=('+' | '-') expression
    //     |   expression op=('<<'|'>>') expression
    //     |   expression op=('<' | '>') expression
    //     |   expression op=('<='|'>=') expression
    //     |   expression op=('=='|'!=') expression
    //     |   expression op='&' expression
    //     |   expression op='^' expression
    //     |   expression op='|' expression
    //     |   expression op='&&' expression
    //     |   expression op='||' expression
    //     |   <assoc=right> expression op='=' expression
    @Override
    public void exitBinaryExpr(MillParser.BinaryExprContext ctx) {
        BinaryExpr.BinaryOp op;
        switch (ctx.op.getType()) {
            case MillParser.Star: op = BinaryExpr.BinaryOp.MUL; break;
            case MillParser.Div: op = BinaryExpr.BinaryOp.DIV; break;
            case MillParser.Mod: op = BinaryExpr.BinaryOp.MOD; break;
            case MillParser.Plus: op = BinaryExpr.BinaryOp.ADD; break;
            case MillParser.Minus: op = BinaryExpr.BinaryOp.SUB; break;
            case MillParser.LeftShift: op = BinaryExpr.BinaryOp.SHL; break;
            case MillParser.RightShift: op = BinaryExpr.BinaryOp.SHR; break;
            case MillParser.Less: op = BinaryExpr.BinaryOp.LT; break;
            case MillParser.Greater: op = BinaryExpr.BinaryOp.GT; break;
            case MillParser.LessEqual: op = BinaryExpr.BinaryOp.LE; break;
            case MillParser.GreaterEqual: op = BinaryExpr.BinaryOp.GE; break;
            case MillParser.Equal: op = BinaryExpr.BinaryOp.EQ; break;
            case MillParser.NotEqual: op = BinaryExpr.BinaryOp.NE; break;
            case MillParser.And: op = BinaryExpr.BinaryOp.BITWISE_AND; break;
            case MillParser.Caret: op = BinaryExpr.BinaryOp.XOR; break;
            case MillParser.Or: op = BinaryExpr.BinaryOp.BITWISE_OR; break;
            case MillParser.AndAnd: op = BinaryExpr.BinaryOp.LOGICAL_AND; break;
            case MillParser.OrOr: op = BinaryExpr.BinaryOp.LOGICAL_OR; break;
            case MillParser.Assign: op = BinaryExpr.BinaryOp.ASSIGN; break;
            default: throw new RuntimeException("Unknown binary op.");
        }
        map.put(ctx, new BinaryExpr(
                op,
                (Expr)map.get(ctx.expression(0)),
                (Expr)map.get(ctx.expression(1))
        ));
    }

    // expression: Identifier
    @Override
    public void exitIdentifier(MillParser.IdentifierContext ctx) {
        map.put(ctx, new Identifier(Symbol.get(ctx.Identifier().getText())));
    }

    private String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != '\\') {
                sb.append(s.charAt(i));
            } else {
                ++i;
                switch (s.charAt(i)) {
                    case 't': sb.append('\t'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case '\'':sb.append('\''); break;
                    case '"': sb.append('"');  break;
                    case '\\':sb.append('\\'); break;
                    default: sb.append(s.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    // expression: Constant
    @Override
    public void exitLiteral(MillParser.LiteralContext ctx) {
        map.put(ctx, map.get(ctx.constant()));
    }

    // constant
    //     :   type=IntegerConstant
    //     |   type=CharacterConstant
    //     |   type=StringLiteral
    //     |   type=NullLiteral
    //     |   type=BoolConstant
    //     ;
    @Override
    public void exitConstant(MillParser.ConstantContext ctx) {
        String s = ctx.type.getText();
        int type = ctx.type.getType();
        if (type == MillParser.IntegerConstant) {
            map.put(ctx, new IntConst(Integer.valueOf(s)));
        } else if (type == MillParser.NullLiteral) {
            map.put(ctx, new NullLiteral());
        } else if (type == MillParser.BoolConstant) {
            map.put(ctx, new BoolConst(s.equals("true")));
        } else if (type == MillParser.CharacterConstant || type == MillParser.StringLiteral) {
            s = unescape(s);
            if (type == MillParser.CharacterConstant) {
                if (s.length() == 1) map.put(ctx, new IntConst(s.charAt(0)));
                else throw new RuntimeException("Invalid char literal.");
            } else {
                map.put(ctx, new StringConst(s));
            }
        } else {
            throw new RuntimeException("Unknown literal.");
        }
    }

    // expression: '(' expression ')'
    @Override
    public void exitSubExpression(MillParser.SubExpressionContext ctx) {
        map.put(ctx, map.get(ctx.expression()));
    }

    // creator: nonArrayTypeSpecifier ('[' expression ']')*
    @Override
    public void exitCreator(MillParser.CreatorContext ctx) {
        Type type = (Type)map.get(ctx.nonArrayTypeSpecifier());
        ListIterator<MillParser.ExpressionContext> iter =
                ctx.expression().listIterator(ctx.expression().size());
        while (iter.hasPrevious()) {
            MillParser.ExpressionContext x = iter.previous();
            type = new ArrayType(type, (Expr)map.get(x));
        }
        map.put(ctx, type);
    }
}
