package com.abcdabcd987.compiler2016.Parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by abcdabcd987 on 2016-03-24.
 */
@RunWith(Parameterized.class)
public class ParserTest {

    @Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        for (File f : new File("testcase/").listFiles()) {
            if (f.isFile() && f.getName().endsWith(".mill")) {
                params.add(new Object[] { "testcase/" + f.getName() });
            }
        }
        return params;
    }

    private String filename;

    public ParserTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void testNoException() throws IOException {
        System.out.println(filename);

        InputStream is = new FileInputStream(filename);
        ANTLRInputStream input = new ANTLRInputStream(is);
        MillLexer lexer = new MillLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MillParser parser = new MillParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        parser.program();
    }
}
