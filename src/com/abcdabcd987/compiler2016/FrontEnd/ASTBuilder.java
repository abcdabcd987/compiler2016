package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.Parser.MillBaseListener;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.Type;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.List;
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
            @SuppressWarnings("unchecked")
            List<VariableDeclStmt> decls = ((List<VariableDecl>)node).stream()
                    .map(VariableDeclStmt::new).collect(Collectors.toList());
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
                ctx.statement(1) == null ? null : (Stmt)map.get(ctx.statement(1)),
                new SourcePosition(ctx.expression())
        ));
    }

    // iterationStatement: 'while' '(' expression ')' statement
    @Override
    public void exitWhile(MillParser.WhileContext ctx) {
        map.put(ctx, new WhileLoop(
                (Expr)map.get(ctx.expression()),
                (Stmt)map.get(ctx.statement()),
                new SourcePosition(ctx.expression())
        ));
    }

    // iterationStatement:
    //    'for' '(' declinit=variableDeclaration
    //              cond=expression? ';'
    //              step=expression? ')'
    //        statement
    //    'for' '(' init=expression? ';'
    //              cont=expression? ';'
    //              step=expression? ')'

    @Override
    public void exitFor(MillParser.ForContext ctx) {
        List<VariableDecl> initWithDecl = (List<VariableDecl>)map.get(ctx.declinit);
        Expr init = (Expr)map.get(ctx.init);
        Expr cond = (Expr)map.get(ctx.cond);
        Expr step = (Expr)map.get(ctx.step);
        Stmt body = (Stmt)map.get(ctx.statement());
        if (ctx.variableDeclaration() == null)
            map.put(ctx, new ForLoop(init, cond, step, body, cond != null ? new SourcePosition(ctx.cond) : null));
        else
            map.put(ctx, new ForLoop(initWithDecl, cond, step, body, cond != null ? new SourcePosition(ctx.cond) : null));
    }

    // jumpStatement: 'continue' ';'
    @Override
    public void exitContinue(MillParser.ContinueContext ctx) {
        map.put(ctx, new ContinueStmt(new SourcePosition(ctx)));
    }

    // jumpStatement: 'break' ';'
    @Override
    public void exitBreak(MillParser.BreakContext ctx) {
        map.put(ctx, new BreakStmt(new SourcePosition(ctx)));
    }

    // jumpStatement: 'return' expression? ';'
    @Override
    public void exitReturn(MillParser.ReturnContext ctx) {
        map.put(ctx, new ReturnStmt(
                ctx.expression() != null ? (Expr)map.get(ctx.expression()) : null,
                new SourcePosition(ctx.start),
                ctx.expression() != null ? new SourcePosition(ctx.expression()) : null
        ));
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
            case MillParser.Int: map.put(ctx, new PrimitiveTypeNode(Type.Types.INT)); break;
            case MillParser.Bool: map.put(ctx, new PrimitiveTypeNode(Type.Types.BOOL)); break;
            case MillParser.String: map.put(ctx, new PrimitiveTypeNode(Type.Types.STRING)); break;
            case MillParser.Void: map.put(ctx, new PrimitiveTypeNode(Type.Types.VOID)); break;
            case MillParser.Identifier:
                map.put(ctx, new StructTypeNode(ctx.Identifier().getText()));
                break;
            default: throw new RuntimeException("Unhandled type in `nonArrayTypeSpecifier`");
        }
    }

    // typeSpecifier: typeSpecifier '[' ']'
    @Override
    public void exitArrayType(MillParser.ArrayTypeContext ctx) {
        map.put(ctx, new ArrayTypeNode((TypeNode)map.get(ctx.typeSpecifier())));
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
        TypeNode type = (TypeNode)map.get(ctx.typeSpecifier());
        ctx.variableInitDeclarator().stream()
                .forEachOrdered(x -> decls.add(new VariableDecl(
                    type,
                    x.Identifier().getText(),
                    x.expression() != null ? (Expr)map.get(x.expression()) : null,
                    new SourcePosition(ctx.typeSpecifier()),
                    new SourcePosition(x.Identifier()),
                    x.expression() != null ? new SourcePosition(x.expression().start) : null)));
        map.put(ctx, decls);
    }

    // classDeclaration: 'class' Identifier '{' memberDeclaration* '}'
    @Override
    public void exitClassDeclaration(MillParser.ClassDeclarationContext ctx) {
        StructDecl.Builder builder = new StructDecl.Builder();
        builder.setName(ctx.Identifier().getText());
        builder.setPosName(new SourcePosition(ctx.Identifier()));
        ctx.memberDeclaration().stream().map(map::get).forEachOrdered(builder::add);
        map.put(ctx, builder.build());
    }

    // memberDeclaration: typeSpecifier Identifier ';'
    @Override
    public void exitMemberDeclaration(MillParser.MemberDeclarationContext ctx) {
        map.put(ctx, new VariableDecl(
                (TypeNode)map.get(ctx.typeSpecifier()),
                ctx.Identifier().getText(),
                null,
                new SourcePosition(ctx.typeSpecifier()),
                new SourcePosition(ctx.Identifier()),
                null
        ));
    }

    // functionDeclaration: typeSpecifier Identifier '(' parameterDeclarationList? ')' blockStatement
    // parameterDeclarationList: parameterDeclaration (',' parameterDeclaration)*
    @Override
    public void exitFunctionDeclaration(MillParser.FunctionDeclarationContext ctx) {
        FunctionDecl.Builder builder = new FunctionDecl.Builder() ;
        builder.setReturnType((TypeNode)map.get(ctx.typeSpecifier()));
        builder.setName(ctx.Identifier().getText());
        builder.setBody((CompoundStmt)map.get(ctx.blockStatement()));
        builder.setPosName(new SourcePosition(ctx.Identifier()));
        builder.setPosReturnType(new SourcePosition(ctx.typeSpecifier()));
        if (ctx.parameterDeclarationList() != null)
            ctx.parameterDeclarationList().parameterDeclaration()
                    .stream().map(map::get).forEachOrdered(builder::addArgType);
        map.put(ctx, builder.build());
    }

    // parameterDeclaration: typeSpecifier Identifier
    @Override
    public void exitParameterDeclaration(MillParser.ParameterDeclarationContext ctx) {
        map.put(ctx, new VariableDecl(
                (TypeNode)map.get(ctx.typeSpecifier()),
                ctx.Identifier().getText(),
                null,
                new SourcePosition(ctx.typeSpecifier().start),
                new SourcePosition(ctx.Identifier().getSymbol()),
                null
        ));
    }

    // expression: expression operator=('++' | '--')
    @Override
    public void exitPostfixIncDec(MillParser.PostfixIncDecContext ctx) {
        Expr expr = (Expr)map.get(ctx.expression());
        if (ctx.op.getType() == MillParser.PlusPlus)
            map.put(ctx, new SelfIncrement(expr, new SourcePosition(ctx.expression())));
        else
            map.put(ctx, new SelfDecrement(expr, new SourcePosition(ctx.expression())));
    }

    // expression: Identifier '(' parameterList? ')'
    // parameterList: expression (',' expression)*
    @Override
    public void exitFunctionCall(MillParser.FunctionCallContext ctx) {
        FunctionCall.Builder builder = new FunctionCall.Builder();
        builder.setName((Expr)map.get(ctx.expression()));
        builder.setPosName(new SourcePosition(ctx.expression()));
        if (ctx.parameterList() != null) {
            ctx.parameterList().expression().stream()
                    .map(map::get).forEachOrdered(builder::addArg);
            ctx.parameterList().expression().stream()
                    .forEachOrdered(x -> builder.addPosArg(new SourcePosition(x)));
        }
        map.put(ctx, builder.build());
    }

    // expression: expression '[' expression ']'
    @Override
    public void exitSubscript(MillParser.SubscriptContext ctx) {
        map.put(ctx, new ArrayAccess(
                (Expr)map.get(ctx.expression(0)),
                (Expr)map.get(ctx.expression(1)),
                new SourcePosition(ctx.expression(0)),
                new SourcePosition(ctx.expression(1))
        ));
    }

    // expression: expression '.' Identifier
    @Override
    public void exitMemberAccess(MillParser.MemberAccessContext ctx) {
        map.put(ctx, new MemberAccess(
                (Expr)map.get(ctx.expression()),
                ctx.Identifier().getText(),
                new SourcePosition(ctx.expression()),
                new SourcePosition(ctx.Identifier())
        ));
    }

    // expression
    //     :   <assoc=right> operator=('++'|'--') expression
    //     |   <assoc=right> operator=('+' | '-') expression
    //     |   <assoc=right> operator=('!' | '~') expression
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
            default: throw new RuntimeException("Unknown unary operator.");
        }
        map.put(ctx, new UnaryExpr(
                op,
                (Expr)map.get(ctx.expression()),
                new SourcePosition(ctx.op),
                new SourcePosition(ctx.expression())
        ));
    }

    // expression: <assoc=right> 'new' creator
    @Override
    public void exitNew(MillParser.NewContext ctx) {
        map.put(ctx, map.get(ctx.creator()));
    }

    // expression
    //     :   expression operator=('*' | '/' | '%') expression
    //     |   expression operator=('+' | '-') expression
    //     |   expression operator=('<<'|'>>') expression
    //     |   expression operator=('<' | '>') expression
    //     |   expression operator=('<='|'>=') expression
    //     |   expression operator=('=='|'!=') expression
    //     |   expression operator='&' expression
    //     |   expression operator='^' expression
    //     |   expression operator='|' expression
    //     |   expression operator='&&' expression
    //     |   expression operator='||' expression
    //     |   <assoc=right> expression operator='=' expression
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
            default: throw new RuntimeException("Unknown binary operator.");
        }
        map.put(ctx, new BinaryExpr(
                op,
                (Expr)map.get(ctx.expression(0)),
                (Expr)map.get(ctx.expression(1)),
                new SourcePosition(ctx.op),
                new SourcePosition(ctx.expression(0)),
                new SourcePosition(ctx.expression(1))
        ));
    }

    // expression: Identifier
    @Override
    public void exitIdentifier(MillParser.IdentifierContext ctx) {
        map.put(ctx, new Identifier(ctx.Identifier().getText(), new SourcePosition(ctx.Identifier())));
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
            //s = unescape(s);
            s = s.substring(1, s.length()-1);
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

    // creator: nonArrayTypeSpecifier ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+
    @Override
    public void exitCreatorError(MillParser.CreatorErrorContext ctx) {
        throw new ParseCancellationException("Array dimension specification in new expression should be left aligned.");
    }

    // creator: nonArrayTypeSpecifier ('[' expression ']')+ ('[' ']')*
    @Override
    public void exitCreatorArray(MillParser.CreatorArrayContext ctx) {
        NewExpr.Builder builder = new NewExpr.Builder();
        builder.setType((TypeNode)map.get(ctx.nonArrayTypeSpecifier()));
        ctx.expression().stream().map(map::get).forEachOrdered(builder::addDimension);
        ctx.expression().stream().forEachOrdered(x -> builder.addPosDimension(new SourcePosition(x)));
        int unspecified = ctx.getTokens(MillParser.LBracket).size() - ctx.expression().size();
        for (int i = 0; i < unspecified; ++i) {
            builder.addDimension(null);
            builder.addPosDimension(null);
        }
        map.put(ctx, builder.build());
    }

    // creator: nonArrayTypeSpecifier
    @Override
    public void exitCreatorNonArray(MillParser.CreatorNonArrayContext ctx) {
        NewExpr.Builder builder = new NewExpr.Builder();
        builder.setType((TypeNode)map.get(ctx.nonArrayTypeSpecifier()));
        map.put(ctx, builder.build());
    }
}
