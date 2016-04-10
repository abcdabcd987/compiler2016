package com.abcdabcd987.compiler2016.AST;

import com.abcdabcd987.compiler2016.IR.BasicBlock;
import com.abcdabcd987.compiler2016.IR.Int1Value;
import com.abcdabcd987.compiler2016.IR.Int32Value;
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
    public Int1Value int1Value;
    public Int32Value int32Value;
}
