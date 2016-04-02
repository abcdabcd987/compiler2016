package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.SymbolTable;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public abstract class ASTNode {
    public abstract void accept(IASTVisitor visitor);
    public SymbolTable scope;
}
