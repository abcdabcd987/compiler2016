package com.abcdabcd987.compiler2016.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class SymbolTable {
    private Map<String, Type> map = new LinkedHashMap<>();
    private SymbolTable enclosingScope;

    public SymbolTable(SymbolTable enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public void define(String name, Type type) {
        map.put(name, type);
    }

    public Type resolveCurrent(String name) {
        return map.get(name);
    }

    public Type resolve(String name) {
        Type t = map.get(name);
        if (t != null) return t;
        if (enclosingScope != null) return enclosingScope.resolve(name);
        return null;
    }

    public SymbolTable getEnclosingScope() {
        return enclosingScope;
    }

    public String toStructureString(String indent) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream().forEachOrdered(x ->
                sb.append(indent).append(x.getKey()).append(": ").append(x.getValue()).append("\n")
        );
        return sb.toString();
    }
}
