package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.Symbol.FunctionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Function {
    private Map<String, VirtualRegister> varReg = new HashMap<>();
    private String name;
    private BasicBlock startBB;
    private FunctionType type;
    private int retSize;

    public FunctionType getType() {
        return type;
    }

    public Function(FunctionType type) {
        this.retSize = type.returnType.getRegisterSize();
        this.name = type.name;
        this.type = type;
        this.startBB = new BasicBlock(name + "_start");
    }

    public void defineVarReg(String name, VirtualRegister reg) {
        varReg.put(name, reg);
    }

    public IntValue getVarReg(String name) {
        return varReg.get(name);
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
