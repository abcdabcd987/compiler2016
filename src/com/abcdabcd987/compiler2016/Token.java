package com.abcdabcd987.compiler2016;

/**
 * Created by abcdabcd987 on 2016-03-02.
 */
public class Token {
    public TokenType type;
    public int intval;
    public double dblval;
    public String strval;

    @Override
    public String toString() {
        switch (type) {
            case IDENTIFIER:
            case KEYWORD:
            case SYMBOL:
            case CHAR:
            case STRING:
                return "<" + type + ", " + strval + ">";
            case INTEGER:
                return "<" + type + ", " + intval + ">";
            case DOUBLE:
                return "<" + type + ", " + dblval + ">";
            default: //case EOF:
                return "<" + type + ">";
        }
    }
}
