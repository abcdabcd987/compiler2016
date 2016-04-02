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
        this.type = Types.FUNCTION;
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

    @Override
    public boolean isSameType(Type rhs) {
        /*if (!(rhs instanceof FunctionType)) return false;
        FunctionType t = (FunctionType) rhs;
        if (!returnType.isSameType(t.returnType)) return false;
        if (argTypes.size() != t.argTypes.size()) return false;
        for (int i = 0; i < argTypes.size(); ++i)
            if (!argTypes.get(i).isSameType(t.argTypes.get(i)))
                return false;
        return true;*/
        return this == rhs;
    }
}
