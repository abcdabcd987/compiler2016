package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.BackEnd.SSATransformer;

import java.util.*;
import java.util.function.Function;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class Branch extends BranchInstruction {
    private IntValue cond;
    private BasicBlock then;
    private BasicBlock otherwise;

    public Branch(BasicBlock BB, IntValue cond, BasicBlock then, BasicBlock otherwise) {
        super(BB);
        this.cond = cond;
        this.then = then;
        this.otherwise = otherwise;
        reloadUsedRegisterCollection();
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public VirtualRegister getDefinedRegister() {
        return null;
    }

    @Override
    protected void reloadUsedRegisterCollection() {
        usedRegister.clear();
        if (cond instanceof Register) usedRegister.add((Register) cond);
        usedIntValue.clear();
        usedIntValue.add(cond);
    }

    @Override
    public void setDefinedRegister(Register newReg) {
        assert false;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (cond instanceof Register) cond = regMap.get(cond);
        reloadUsedRegisterCollection();
    }

    @Override
    public void renameDefinedRegister(Function<VirtualRegister, Integer> idSupplier) {

    }

    @Override
    public void renameUsedRegister(Function<VirtualRegister, Integer> idSupplier) {
        if (cond instanceof VirtualRegister)
            cond = ((VirtualRegister) cond).getSSARenamedRegister(idSupplier.apply((VirtualRegister) cond));
        reloadUsedRegisterCollection();
    }

    @Override
    public void replaceIntValueUse(IntValue oldValue, IntValue newValue) {
        if (cond == oldValue) cond = newValue;
        reloadUsedRegisterCollection();
    }

    @Override
    public Collection<BasicBlock> getUsedBasicBlock() {
        return Arrays.asList(then, otherwise);
    }

    @Override
    public Branch copyAndRename(Map<Object, Object> renameMap) {
        return new Branch(
                (BasicBlock) renameMap.getOrDefault(curBB, curBB),
                (IntValue) renameMap.getOrDefault(cond, cond),
                (BasicBlock) renameMap.getOrDefault(then, then),
                (BasicBlock) renameMap.getOrDefault(otherwise, otherwise)
        );
    }

    public IntValue getCond() {
        return cond;
    }

    public BasicBlock getThen() {
        return then;
    }

    public BasicBlock getElse() {
        return otherwise;
    }


    /**
     * <pre>
     * change from: curBB -> toBB
     *          to: curBB -> insertedBB -> toBB
     * </pre>
     * used in ssa destruction.
     * @param toBB old jump destination
     * @return inserted split block
     * @see SSATransformer#removePhiInstruction()
     */
    public BasicBlock insertSplitBlock(BasicBlock toBB) {
        assert (then == toBB ? 1 : 0) + (otherwise == toBB ? 1 : 0) == 1;
        BasicBlock target = then == toBB ? then : otherwise;
        BasicBlock insertedBB = new BasicBlock(curBB.getParent(), "CEP");
        insertedBB.end(new Jump(insertedBB, target));

        if (then == toBB) then = insertedBB;
        else otherwise = insertedBB;

        curBB.getSucc().remove(toBB);
        curBB.getSucc().add(insertedBB);
        toBB.getPred().remove(curBB);
        toBB.getPred().add(insertedBB);
        insertedBB.getPred().add(curBB);
        insertedBB.getSucc().add(toBB);
        return insertedBB;
    }
}
