package com.abcdabcd987.compiler2016.IR;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class PhiInstruction {
    private BasicBlock BB;
    public VirtualRegister dest;
    public Map<BasicBlock, VirtualRegister> paths = new HashMap<>();

    public PhiInstruction(BasicBlock BB, VirtualRegister dest) {
        this.BB = BB;
        this.dest = dest;
    }

    public void add(VirtualRegister value, BasicBlock from) {
        paths.put(from, value);
    }

    public VirtualRegister getValue(BasicBlock from) {
        return paths.get(from);
    }

    public BasicBlock getBasicBlock() {
        return BB;
    }
}
