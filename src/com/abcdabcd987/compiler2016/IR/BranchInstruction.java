package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.BackEnd.SSATransformer;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by abcdabcd987 on 2016-04-10.
 */
public abstract class BranchInstruction extends IRInstruction {
    public BranchInstruction(BasicBlock BB, IRInstruction prev, IRInstruction next) {
        super(BB, prev, next);
    }

    public BranchInstruction(BasicBlock BB) {
        super(BB);
    }

    /**
     * <pre>
     * change from: curBB -> toBB
     *          to: curBB -> insertedBB -> toBB
     * </pre>
     * used in ssa destruction.
     * @param toBB old jump destination
     * @param insertedBB inserted jump destination
     * @see SSATransformer#removePhiInstruction()
     */
    public abstract void insertSplitBlock(BasicBlock toBB, BasicBlock insertedBB);

    /**
     * utility function for {@link #insertSplitBlock(BasicBlock, BasicBlock)}.
     * replace `old` with `new` from `info`.
     * @param info is either `curBB.getSucc()` or `toBB.getPred()`
     * @param old old jump destination
     * @param now new jump destination
     * @see IRInstruction#curBB
     * @see BasicBlock#getPred()
     */
    protected void updateConnectivity(Set<BasicBlock> info, BasicBlock old, BasicBlock now) {
        if (info == null) return;
        Iterator<BasicBlock> iter = info.iterator();
        while (iter.hasNext())
            if (iter.next() == old) {
                iter.remove();
                break;
            }
        info.add(now);
    }
}
