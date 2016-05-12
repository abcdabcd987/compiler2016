package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.BackEnd.SSATransformer;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-10.
 */
public abstract class BranchInstruction extends IRInstruction {
    public BranchInstruction(BasicBlock BB, IRInstruction prev, IRInstruction next) {
        super(BB, prev, next);
    }

    protected BranchInstruction(BasicBlock BB) {
        super(BB);
    }

    public abstract Collection<BasicBlock> getUsedBasicBlock();

    @Override
    public abstract BranchInstruction copyAndRename(Map<Object, Object> renameMap);
}
