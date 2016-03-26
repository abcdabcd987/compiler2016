package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class RecordType extends BasicType {
    public final Symbol name;

    public RecordType(Symbol name) {
        this.name = name;
    }
}
