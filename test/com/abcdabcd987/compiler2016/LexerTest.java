package com.abcdabcd987.compiler2016;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Created by abcdabcd987 on 2016-03-02.
 */
public class LexerTest {

    private static void writeStandardAnswer(String path) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path + ".ans"))) {
            Lexer lexer = new Lexer(path + ".in");
            Token token;
            while ((token = lexer.next()) != null) out.write(token + "\n");
        }
    }

    private static boolean testLexer(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        Lexer lexer = new Lexer(path + ".in");
        Token token;
        while ((token = lexer.next()) != null) sb.append(token).append("\n");

        StringBuilder std = new StringBuilder();
        try (BufferedReader stdStream = new BufferedReader(new FileReader(path + ".ans"))) {
            String line;
            while ((line = stdStream.readLine()) != null) std.append(line);
        }

        return sb.equals(std);
    }

    @Test
    public void testAll() throws Exception {
        assertTrue(testLexer("test/com/abcdabcd987/compiler2016/lexer_1"));
    }

    public static void main(String[] args) throws IOException {
        //writeStandardAnswer("test/com/abcdabcd987/compiler2016/lexer_1");
    }
}