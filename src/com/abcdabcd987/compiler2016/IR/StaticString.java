package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.CompilerOptions;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class StaticString extends StaticData {
    public String value;

    public StaticString(String value) {
        super("str");
        this.value = value;
    }

    @Override
    public int getRegisterSize() {
        return CompilerOptions.getSizePointer();
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
