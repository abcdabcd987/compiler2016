// Generated from Mill.g4 by ANTLR 4.5.2

package com.abcdabcd987.compiler2016.Parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MillParser}.
 */
public interface MillListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MillParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MillParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MillParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code class}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void enterClass(MillParser.ClassContext ctx);
	/**
	 * Exit a parse tree produced by the {@code class}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void exitClass(MillParser.ClassContext ctx);
	/**
	 * Enter a parse tree produced by the {@code func}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void enterFunc(MillParser.FuncContext ctx);
	/**
	 * Exit a parse tree produced by the {@code func}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void exitFunc(MillParser.FuncContext ctx);
	/**
	 * Enter a parse tree produced by the {@code var}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void enterVar(MillParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code var}
	 * labeled alternative in {@link MillParser#programSection}.
	 * @param ctx the parse tree
	 */
	void exitVar(MillParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MillParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MillParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpr(MillParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpr(MillParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code select}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSelect(MillParser.SelectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code select}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSelect(MillParser.SelectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iter}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIter(MillParser.IterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iter}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIter(MillParser.IterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jump}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterJump(MillParser.JumpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jump}
	 * labeled alternative in {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitJump(MillParser.JumpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(MillParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(MillParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code vardecl}
	 * labeled alternative in {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterVardecl(MillParser.VardeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code vardecl}
	 * labeled alternative in {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitVardecl(MillParser.VardeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmt}
	 * labeled alternative in {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterStmt(MillParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmt}
	 * labeled alternative in {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitStmt(MillParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(MillParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(MillParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement(MillParser.SelectionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement(MillParser.SelectionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while}
	 * labeled alternative in {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhile(MillParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while}
	 * labeled alternative in {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhile(MillParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for}
	 * labeled alternative in {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterFor(MillParser.ForContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for}
	 * labeled alternative in {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitFor(MillParser.ForContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continue}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinue(MillParser.ContinueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continue}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinue(MillParser.ContinueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code break}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreak(MillParser.BreakContext ctx);
	/**
	 * Exit a parse tree produced by the {@code break}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreak(MillParser.BreakContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturn(MillParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return}
	 * labeled alternative in {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturn(MillParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#nonArrayTypeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayTypeSpecifier(MillParser.NonArrayTypeSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#nonArrayTypeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayTypeSpecifier(MillParser.NonArrayTypeSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(MillParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(MillParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MillParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MillParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MillParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MillParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#variableInitDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitDeclarator(MillParser.VariableInitDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#variableInitDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitDeclarator(MillParser.VariableInitDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(MillParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(MillParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(MillParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(MillParser.MemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(MillParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(MillParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#parameterDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclarationList(MillParser.ParameterDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#parameterDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclarationList(MillParser.ParameterDeclarationListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclaration(MillParser.ParameterDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclaration(MillParser.ParameterDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code New}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNew(MillParser.NewContext ctx);
	/**
	 * Exit a parse tree produced by the {@code New}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNew(MillParser.NewContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Identifier}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(MillParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Identifier}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(MillParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberAccess}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccess(MillParser.MemberAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberAccess}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccess(MillParser.MemberAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Literal}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(MillParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Literal}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(MillParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MillParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MillParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Subscript}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubscript(MillParser.SubscriptContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Subscript}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubscript(MillParser.SubscriptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(MillParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(MillParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostfixIncDec}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixIncDec(MillParser.PostfixIncDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostfixIncDec}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixIncDec(MillParser.PostfixIncDecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(MillParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(MillParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubExpression}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubExpression(MillParser.SubExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubExpression}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubExpression(MillParser.SubExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creatorError}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreatorError(MillParser.CreatorErrorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creatorError}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreatorError(MillParser.CreatorErrorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creatorArray}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreatorArray(MillParser.CreatorArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creatorArray}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreatorArray(MillParser.CreatorArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code creatorNonArray}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreatorNonArray(MillParser.CreatorNonArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code creatorNonArray}
	 * labeled alternative in {@link MillParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreatorNonArray(MillParser.CreatorNonArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MillParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MillParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(MillParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(MillParser.ConstantContext ctx);
}