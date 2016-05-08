package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.IR.BasicBlock;
import com.abcdabcd987.compiler2016.IR.IntValue;
import com.abcdabcd987.compiler2016.Symbol.Type;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public abstract class Expr extends Stmt {
    // for semantic check
    public Type exprType;
    public boolean isLvalue = true;

    // for IR: condition check
    public BasicBlock ifTrue;
    public BasicBlock ifFalse;

    // for IR: expr value
    public IntValue intValue;
    public IntValue addressValue;
    public int addressOffset;
}
