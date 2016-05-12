package com.abcdabcd987.compiler2016.IR;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class VirtualRegister extends Register {
    private String hintName;
    private int ssaId = -1;
    private VirtualRegister oldName = null;
    private Map<Integer, VirtualRegister> newNames = null;
    public PhysicalRegister forcedPhysicalRegister = null;

    public VirtualRegister(String hintName) {
        this.hintName = hintName;
    }

    private VirtualRegister(String hintName, int ssaId, VirtualRegister oldName) {
        this.hintName = hintName;
        this.ssaId = ssaId;
        this.oldName = oldName;
    }

    public VirtualRegister getSSARenamedRegister(int id) {
        assert ssaId == -1;
        if (newNames == null) newNames = new HashMap<>();
        VirtualRegister newName = newNames.get(id);
        if (newName == null) {
            newName = new VirtualRegister(hintName, id, this);
            newNames.put(id, newName);
        }
        return newName;
    }

    public int getSSAId() {
        return ssaId;
    }

    public VirtualRegister getOldName() {
        return oldName;
    }

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public VirtualRegister copy() {
        return new VirtualRegister(hintName);
    }

    public String getHintName() {
        return hintName;
    }

    @Override
    public String toString() {
        return ssaId == -1 ? hintName : hintName + "." + ssaId;
    }
}
