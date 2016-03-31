package com.abcdabcd987.compiler2016.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class FunctionType extends Type {
    public VariableType returnType;
    public String name;
    public List<VariableType> argTypes = new ArrayList<>();

    public FunctionType(VariableType returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<FunctionType>");
        sb.append("name: ").append(name);
        sb.append(", retType: ").append(returnType);
        sb.append(", argTypes: [");
        argTypes.stream().forEachOrdered(x -> sb.append(x).append(", "));
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toStructureString(String indent) {
        return indent + toString() + "\n";
    }
}
