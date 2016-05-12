package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.BackEnd.FunctionInliner;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public abstract class IntValue {
    public abstract void accept(IIRVisitor visitor);

    /**
     * used in {@link FunctionInliner}
     */
    public abstract IntValue copy();
}
