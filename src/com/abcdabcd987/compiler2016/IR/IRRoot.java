package com.abcdabcd987.compiler2016.IR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
public class IRRoot extends IRNode {
    public List<Function> functions = new ArrayList<>();

    @Override
    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
