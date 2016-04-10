package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public class IntImmediate extends IRNode implements Int32Value {
    public int value;

    public IntImmediate(int value) {
        this.value = value;
    }
}
