package com.abcdabcd987.compiler2016.AST;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class Symbol {
    private static Map<String, Symbol> pool = new HashMap<>();
    private String name;

    private Symbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Symbol get(String s) {
        s = s.intern();
        Symbol sym = pool.get(s);
        if (sym == null) {
            sym = new Symbol(s);
            pool.put(s, sym);
        }
        return sym;
    }
}
