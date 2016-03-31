package com.abcdabcd987.compiler2016.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class SymbolTable {
    private Map<String, Type> map = new LinkedHashMap<>();

    public void define(String name, Type type) {
        map.put(name, type);
    }

    public Type resolve(String name) {
        return map.get(name);
    }

    public String toStructureString(String indent) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream().forEachOrdered(x ->
                sb.append(indent).append(x.getKey()).append(": ").append(x.getValue()).append("\n")
        );
        return sb.toString();
    }
}
