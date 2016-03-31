package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.Symbol.Type;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public abstract class TypeNode extends ASTNode {
    public abstract Type.Types getType();
}
