package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class VirtualRegister extends IntValue {
    private String hintName;
    private int ssaId = -1;
    private VirtualRegister oldName = null;

    public VirtualRegister(String hintName) {
        this.hintName = hintName;
    }

    private VirtualRegister(String hintName, int ssaId, VirtualRegister oldName) {
        this.hintName = hintName;
        this.ssaId = ssaId;
        this.oldName = oldName;
    }

    public VirtualRegister newSSARenamedRegister(int id) {
        return new VirtualRegister(hintName, id, this);
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

    public String getHintName() {
        return ssaId == -1 ? hintName : hintName + "." + ssaId;
    }

    @Override
    public String toString() {
        return hintName;
    }
}
