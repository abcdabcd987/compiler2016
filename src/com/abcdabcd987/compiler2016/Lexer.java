package com.abcdabcd987.compiler2016;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by abcdabcd987 on 2016-03-02.
 */
public class Lexer {
    private BufferedReader inputStream;
    private int lastChar = 0;
    private int lineNumber = 1;
    private int columnNumber = 0;
    private boolean finished = false;
    private StringBuilder sb = new StringBuilder();

    Lexer(String sourcePath) throws IOException {
        inputStream = new BufferedReader(new FileReader(sourcePath));
        read();
    }

    private int read() throws IOException {
        lastChar = inputStream.read();
        if (lastChar == '\n') {
            ++lineNumber;
            columnNumber = 1;
        } else {
            ++columnNumber;
        }
        return lastChar;
    }

    private void skipWhitespace() throws IOException {
        while (lastChar != -1 && Character.isWhitespace(lastChar))
            read();
    }

    private int toDigit(int ch) {
        return ch - '0';
    }

    private boolean isLetter(int ch) {
        return Character.isLetter(ch) || (ch == '_') || (ch == '$');
    }

    private int readChar() throws IOException {
        read();
        if (lastChar == '\\') {
            read();
            if (     lastChar == 'n')  return '\n';
            else if (lastChar == '\\') return '\\';
            else if (lastChar == '\'') return '\'';
            else if (lastChar == '\"') return '\"';
            else {
                //TODO: other escape characters & hex/oct representation
                throw new Error("Unsupported escape characters.");
            }
        }
        return lastChar;
    }

    public Token next() {
        //TODO: ignore comment
        try {
            if (finished) return null;
            Token token = new Token();
            if (lastChar == -1) {
                inputStream.close();
                finished = true;
                token.type = TokenType.EOF;
                return token;
            }
            int ch;

            skipWhitespace();

            if (isLetter(lastChar)) { // identifier or keyword
                sb.setLength(0);
                while (isLetter(lastChar) || Character.isDigit(lastChar)) {
                    sb.appendCodePoint(lastChar);
                    read();
                }
                token.strval = sb.toString();
                if (KEYWORDS.contains(token.strval))
                    token.type = TokenType.KEYWORD;
                else
                    token.type = TokenType.IDENTIFIER;
                return token;

            } else if (Character.isDigit(lastChar)) { // int or double
                //TODO: number in hex or oct
                int ival = 0;
                double dval = 0;
                while (Character.isDigit(lastChar) || lastChar == '.') {
                    ival = ival * 10 + toDigit(lastChar);
                    dval = dval * 10 + toDigit(lastChar);
                    read();
                }
                if (lastChar == '.') {
                    read();
                    for (double s = .1; Character.isDigit(lastChar); s /= 10) {
                        dval += toDigit(lastChar) * s;
                        read();
                    }
                    token.type = TokenType.DOUBLE;
                    token.dblval = dval;
                } else {
                    token.type = TokenType.INTEGER;
                    token.intval = ival;
                }
                return token;

            } else if (lastChar == '\'') { // char
                ch = readChar();
                read();
                if (lastChar != '\'') {
                    throw new Error("char format error.");
                }
                read();
                token.type = TokenType.CHAR;
                token.strval = String.valueOf((char)ch);
                return token;

            } else if (lastChar == '\"') { // string
                sb.setLength(0);
                while ((ch = readChar()) != '\"') sb.appendCodePoint(ch);
                read();
                token.type = TokenType.STRING;
                token.strval = sb.toString();
                return token;

            } else if (lastChar == -1) { // end-of-file
                token.type = TokenType.EOF;
                return token;
            }

            // symbol
            // REMARK: This is dirty!
            token.type = TokenType.SYMBOL;
            sb.setLength(0);
            sb.appendCodePoint(lastChar);
            ch = lastChar;
            read();
            if (lastChar == '=' && ASSIGN_PREFIXS.contains(ch)) sb.appendCodePoint('=');
            else if (ch == '|' && lastChar == '|') sb.appendCodePoint('|');
            else if (ch == '&' && lastChar == '&') sb.appendCodePoint('&');
            else if (ch == '+' && lastChar == '+') sb.appendCodePoint('+');
            else if (ch == '-' && lastChar == '-') sb.appendCodePoint('-');
            else if (ch == '-' && lastChar == '>') sb.appendCodePoint('>');
            else if (ch == '>' && lastChar == '>') sb.appendCodePoint('>');
            else if (ch == '<' && lastChar == '<') sb.appendCodePoint('<');
            boolean readNext = sb.length() == 2;
            if ((ch == '<' && lastChar == '<') || (ch == '>' && lastChar == '>')) {
                read();
                if (lastChar == '=') sb.appendCodePoint('=');
                else readNext = false;
            }
            if (readNext) read();
            token.strval = sb.toString();
            return token;

        } catch (IOException e) {
            return null;
        } catch (Error e) {
            System.err.printf("Lexer: At Line %d Column %d: %s\n", lineNumber, columnNumber, e.getMessage());
            System.exit(1);
            return null; // to trick the java compiler
        }
    }

    private static final Set<String> KEYWORDS = new HashSet<String>(){{
        add("void");
        add("char");
        add("int");
        add("struct");
        add("union");
        add("if");
        add("else");
        add("while");
        add("for");
        add("continue");
        add("break");
        add("return");
        add("sizeof");
    }};

    private static final Set<Integer> ASSIGN_PREFIXS = new HashSet<Integer>(){{
        add((int)'*');
        add((int)'/');
        add((int)'%');
        add((int)'+');
        add((int)'-');
        add((int)'<');
        add((int)'>');
        add((int)'&');
        add((int)'^');
        add((int)'|');
        add((int)'=');
        add((int)'!');
    }};
}
