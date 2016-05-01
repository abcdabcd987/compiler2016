package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.IR.PhysicalRegister;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class MIPSRegister extends PhysicalRegister {
    private final int id;
    private final String name;
    private final boolean isGeneral;
    private final boolean isCallerSave;
    private final boolean isCalleeSave;

    public MIPSRegister(int id, String name, boolean isGeneral, boolean isCallerSave, boolean isCalleeSave) {
        this.id = id;
        this.name = name;
        this.isGeneral = isGeneral;
        this.isCallerSave = isCallerSave;
        this.isCalleeSave = isCalleeSave;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isGeneral() {
        return isGeneral;
    }

    @Override
    public boolean isCallerSave() {
        return isCallerSave;
    }

    @Override
    public boolean isCalleeSave() {
        return isCalleeSave;
    }
}
