package com.abcdabcd987.compiler2016.IR;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Function extends IRNode {
    private Map<String, IntValue> varAddr = new HashMap<>();
    private String name;
    private BasicBlock startBB;
    private int retSize;

    public Function(int retSize, String name) {
        this.retSize = retSize;
        this.name = name;
        this.startBB = new BasicBlock(name + "_start");
    }

    public void defineVarAddr(String name, IntValue addr) {
        varAddr.put(name, addr);
    }

    public IntValue getVarAddr(String name) {
        return varAddr.get(name);
    }

    public String getName() {
        return name;
    }

    public BasicBlock getStartBB() {
        return startBB;
    }

    public int getRetSize() {
        return retSize;
    }

    public void setStartBB(BasicBlock startBB) {
        this.startBB = startBB;
    }

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
