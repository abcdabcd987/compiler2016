package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class LoadInstruction extends IRNode implements Int32Value {
    public Int32Value address;
    public String hintName;

    public LoadInstruction(Int32Value address, String hintName) {
        this.address = address;
        this.hintName = hintName;
    }
}
