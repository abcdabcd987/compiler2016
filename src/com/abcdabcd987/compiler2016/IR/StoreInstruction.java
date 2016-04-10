package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class StoreInstruction extends IRNode {
    public Int32Value address;
    public Int32Value value;

    public StoreInstruction(Int32Value address, Int32Value value) {
        this.address = address;
        this.value = value;
    }
}
