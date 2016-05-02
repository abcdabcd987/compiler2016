package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.IR.Register;
import com.abcdabcd987.compiler2016.IR.StaticData;
import com.abcdabcd987.compiler2016.IR.VirtualRegister;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class SymbolInfo {
    private Type type;
    private int offset;
    public Register register = null;

    public SymbolInfo(Type type, int offset) {
        this.type = type;
        this.offset = offset;
    }

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }
}
