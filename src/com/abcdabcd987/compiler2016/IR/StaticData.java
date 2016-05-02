package com.abcdabcd987.compiler2016.IR;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public abstract class StaticData extends Register {
    private String hintName;

    public StaticData(String hintName) {
        this.hintName = hintName;
    }

    public String getHintName() {
        return hintName;
    }

    public abstract int getLength();
}
