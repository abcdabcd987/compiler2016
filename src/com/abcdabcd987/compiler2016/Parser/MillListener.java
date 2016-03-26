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
	 * Enter a parse tree produced by {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MillParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MillParser.StatementContext ctx);
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
	 * Enter a parse tree produced by {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(MillParser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(MillParser.BlockItemContext ctx);
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
	 * Enter a parse tree produced by {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement(MillParser.IterationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement(MillParser.IterationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement(MillParser.JumpStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement(MillParser.JumpStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeSpecifier(MillParser.TypeSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeSpecifier(MillParser.TypeSpecifierContext ctx);
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
	 * Enter a parse tree produced by the {@code BitOr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBitOr(MillParser.BitOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitOr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBitOr(MillParser.BitOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(MillParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(MillParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LefRigShift}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLefRigShift(MillParser.LefRigShiftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LefRigShift}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLefRigShift(MillParser.LefRigShiftContext ctx);
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
	 * Enter a parse tree produced by the {@code BoolBitNot}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolBitNot(MillParser.BoolBitNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolBitNot}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolBitNot(MillParser.BoolBitNotContext ctx);
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
	 * Enter a parse tree produced by the {@code LtRt}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLtRt(MillParser.LtRtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LtRt}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLtRt(MillParser.LtRtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqNeq}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEqNeq(MillParser.EqNeqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqNeq}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEqNeq(MillParser.EqNeqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(MillParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(MillParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LeqReq}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLeqReq(MillParser.LeqReqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LeqReq}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLeqReq(MillParser.LeqReqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryPlusMinus}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryPlusMinus(MillParser.UnaryPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryPlusMinus}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryPlusMinus(MillParser.UnaryPlusMinusContext ctx);
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
	 * Enter a parse tree produced by the {@code BitXor}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBitXor(MillParser.BitXorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitXor}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBitXor(MillParser.BitXorContext ctx);
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
	 * Enter a parse tree produced by the {@code BitAnd}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBitAnd(MillParser.BitAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitAnd}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBitAnd(MillParser.BitAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrefixIncDec}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixIncDec(MillParser.PrefixIncDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrefixIncDec}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixIncDec(MillParser.PrefixIncDecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssign(MillParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssign(MillParser.AssignContext ctx);
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
	 * Enter a parse tree produced by the {@code BoolOr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolOr(MillParser.BoolOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolOr}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolOr(MillParser.BoolOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BoolAnd}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolAnd(MillParser.BoolAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolAnd}
	 * labeled alternative in {@link MillParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolAnd(MillParser.BoolAndContext ctx);
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
	 * Enter a parse tree produced by {@link MillParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MillParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MillParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MillParser.ParameterContext ctx);
}