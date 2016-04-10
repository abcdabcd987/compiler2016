package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.*;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Symbol.Type;

import java.util.IdentityHashMap;

/**
 * Created by abcdabcd987 on 2016-04-08.
 */
public class IRBuilder implements IASTVisitor {
    private BasicBlock curBB;
    private BasicBlock curLoopStepBB;
    private BasicBlock curLoopAfterBB;

    @Override
    public void visit(Program node) {
        node.decls.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(StructDecl node) {

    }

    @Override
    public void visit(VariableDecl node) {

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
        curBB.end(new Jump(curLoopAfterBB));
    }

    @Override
    public void visit(ContinueStmt node) {
        curBB.end(new Jump(curLoopStepBB));
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
        BasicBlock BBTrue = new BasicBlock("if_true");
        BasicBlock BBFalse = new BasicBlock("if_false");
        BasicBlock BBMerge = new BasicBlock("if_merge");

        // branch instruction should be added by logical expression
        node.cond.ifTrue = BBTrue;
        node.cond.ifFalse = BBFalse;
        visit(node.cond);

        // generate then
        curBB = BBTrue;
        visit(node.then);
        BBTrue.append(new Jump(BBMerge));

        // generate else
        if (node.otherwise != null) {
            curBB = BBFalse;
            visit(node.otherwise);
        }
        BBFalse.append(new Jump(BBMerge));

        // merge
        curBB = BBMerge;
    }

    @Override
    public void visit(ForLoop node) {
        BasicBlock BBCond = new BasicBlock("for_cond");
        BasicBlock BBLoop = new BasicBlock("for_loop");
        BasicBlock BBStep = new BasicBlock("for_step");
        BasicBlock BBAfter = new BasicBlock("for_after");

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBStep;
        curLoopAfterBB = BBAfter;

        // generate init
        if (node.init != null) visit(node.init);
        else node.initWithDecl.forEach(x -> x.accept(this));

        // jump to condition check
        curBB.end(new Jump(BBCond));

        // generate condition check
        curBB = BBCond;
        node.cond.ifTrue = BBStep;
        node.cond.ifFalse = BBAfter;
        visit(node.cond);

        // generate loop
        curBB = BBLoop;
        visit(node.body);

        // generate step
        if (node.step != null) {
            curBB = BBStep;
            visit(node.step);
        }

        // exit loop
        curLoopStepBB = oldLoopCondBB;
        curLoopAfterBB = oldLoopAfterBB;
        curBB = BBAfter;
    }

    @Override
    public void visit(WhileLoop node) {
        BasicBlock BBCond = new BasicBlock("while_cond");
        BasicBlock BBLoop = new BasicBlock("while_loop");
        BasicBlock BBAfter = new BasicBlock("while_after");

        // save old loop BB
        BasicBlock oldLoopCondBB = curLoopStepBB;
        BasicBlock oldLoopAfterBB = curLoopAfterBB;
        curLoopStepBB = BBCond;
        curLoopAfterBB = BBAfter;

        // jump to condition check
        curBB.end(new Jump(BBCond));

        // generate condition check
        curBB = BBCond;
        node.cond.ifTrue = BBLoop;
        node.cond.ifFalse = BBAfter;
        visit(node.cond);

        // generate loop
        curBB = BBLoop;
        visit(node.body);

        // exit loop
        curLoopStepBB = oldLoopCondBB;
        curLoopAfterBB = oldLoopAfterBB;
        curBB = BBAfter;
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

    private void processLogicalBinaryExpr(BinaryExpr node) {
        // check lhs
        if (node.op == BinaryExpr.BinaryOp.LOGICAL_AND) {
            node.lhs.ifTrue = new BasicBlock("lhs_true");
            node.lhs.ifFalse = node.ifFalse;
            visit(node.lhs);
            curBB = node.lhs.ifTrue;
        } else {
            node.lhs.ifTrue = node.ifTrue;
            node.lhs.ifFalse = new BasicBlock("lhs_false");
            visit(node.lhs);
            curBB = node.lhs.ifFalse;
        }

        // check rhs
        node.rhs.ifTrue = node.ifTrue;
        node.rhs.ifFalse = node.ifFalse;
        visit(node.rhs);
    }

    private void processIntRelationalExpr(BinaryExpr node) {
        visit(node.lhs);
        visit(node.rhs);

        IntComparison.Condition cond = null;
        switch (node.op) {
            case EQ: cond = IntComparison.Condition.EQ; break;
            case NE: cond = IntComparison.Condition.NE; break;
            case LT: cond = IntComparison.Condition.LT; break;
            case GT: cond = IntComparison.Condition.GT; break;
            case LE: cond = IntComparison.Condition.LE; break;
            case GE: cond = IntComparison.Condition.GE; break;
        }

        // generate cmp and br instructions
        Int1Value cmp = new IntComparison(cond, node.lhs.int32Value, node.rhs.int32Value);
        curBB.end(new Branch(cmp, node.ifTrue, node.ifFalse));
    }

    private void processStringRelationalExpr(BinaryExpr node) {

    }

    @Override
    public void visit(BinaryExpr node) {
        switch (node.op) {
            case ASSIGN:
                break;

            case LOGICAL_OR:
            case LOGICAL_AND:
                processLogicalBinaryExpr(node);
                break;

            case BITWISE_OR:
                break;
            case BITWISE_AND:
                break;
            case XOR:
                break;

            case EQ:
            case NE:
            case LT:
            case GT:
            case LE:
            case GE:
                if (node.exprType.type == Type.Types.INT) processIntRelationalExpr(node);
                else processStringRelationalExpr(node);
                break;

            case SHL:
                break;
            case SHR:
                break;
            case ADD:
                break;
            case SUB:
                break;
            case MUL:
                break;
            case DIV:
                break;
            case MOD:
                break;
        }
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
