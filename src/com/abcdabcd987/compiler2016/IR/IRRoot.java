package com.abcdabcd987.compiler2016.IR;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
public class IRRoot {
    public Map<String, Function> functions = new LinkedHashMap<>();

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
