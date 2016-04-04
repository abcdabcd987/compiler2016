package com.abcdabcd987.compiler2016.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Created by abcdabcd987 on 2016-04-04.
 */
public class SourcePosition {
    public final int line;
    public final int column;

    public SourcePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public SourcePosition(Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public SourcePosition(ParserRuleContext ctx) {
        this(ctx.start);
    }

    public SourcePosition(TerminalNode terminal) {
        this(terminal.getSymbol());
    }

    @Override
    public String toString() {
        return "Line " + line + " Column " + column;
    }
}
