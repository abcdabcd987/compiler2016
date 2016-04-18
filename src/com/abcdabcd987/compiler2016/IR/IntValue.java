package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-07.
 */
public interface IntValue {
    IRNode getIRNode();

    int getSize();

    default IntValue toSize(int sz) {
        if (sz == getSize()) return this;
        return new IntConvert(sz, this);
    }

    default IntValue toPointerSize() {
        return toSize(CompilerOptions.getSizePointer());
    }
}
