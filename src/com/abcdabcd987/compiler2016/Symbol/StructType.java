package com.abcdabcd987.compiler2016.Symbol;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class StructType extends VariableType {
    public String name;
    public SymbolTable members;

    public StructType(String name) {
        this.name = name;
        this.members = new SymbolTable();
    }

    @Override
    public String toString() {
        return "<StructType>" + name;
    }

    @Override
    public String toStructureString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(toString()).append(" {\n");
        sb.append(members.toStructureString(indent + "  "));
        sb.append(indent).append("}\n");
        return sb.toString();
    }
}
