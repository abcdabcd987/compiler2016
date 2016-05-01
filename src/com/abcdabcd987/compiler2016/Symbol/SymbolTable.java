package com.abcdabcd987.compiler2016.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class SymbolTable {
    private Map<String, SymbolInfo> map = new LinkedHashMap<>();
    private SymbolTable enclosingScope;
    private int offset = 0;
    private boolean isGlobal = false;

    public SymbolTable(SymbolTable enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public static SymbolTable createGlobalSymbolTable() {
        SymbolTable sym = new SymbolTable(null);
        sym.isGlobal = true;
        return sym;
    }

    public void define(String name, Type type) {
        map.put(name, new SymbolInfo(type, offset));
        offset += type.getRegisterSize();
    }

    public SymbolInfo getInfoCurrent(String name) { return map.get(name); }

    public SymbolInfo getInfo(String name) {
        SymbolInfo t = map.get(name);
        if (t != null) return t;
        if (enclosingScope != null) return enclosingScope.getInfo(name);
        return null;
    }

    public int getSize() {
        return offset;
    }

    public Type getTypeCurrent(String name) {
        SymbolInfo info = getInfoCurrent(name);
        return info != null ? info.getType() : null;
    }

    public Type getType(String name) {
        SymbolInfo info = getInfo(name);
        return info != null ? info.getType() : null;
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
