package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.Symbol.FunctionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Function extends IRNode {
    private Map<String, IntValue> varAddr = new HashMap<>();
    private String name;
    private BasicBlock startBB;
    private FunctionType type;
    private int retSize;

    public FunctionType getType() {
        return type;
    }

    public Function(FunctionType type) {
        this.retSize = type.returnType.getAllocateSize();
        this.name = type.name;
        this.type = type;
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
