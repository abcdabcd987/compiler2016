package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public interface IIRVisitor {
    void visit(IRRoot node);
    void visit(BasicBlock node);
    void visit(Function node);

    void visit(BinaryOperation node);
    void visit(UnaryOperation node);
    void visit(IntComparison node);
    void visit(IntImmediate node);
    void visit(Call node);
    void visit(SystemCall node);
    void visit(PhiInstruction node);

    void visit(Branch node);
    void visit(Return node);
    void visit(Jump node);

    void visit(VirtualRegister node);
    void visit(PhysicalRegister node);
    void visit(StackSlot node);
    void visit(HeapAllocate node);
    void visit(Load node);
    void visit(Store node);
    void visit(Move node);

    void visit(StaticSpace node);
    void visit(StaticString node);
}
