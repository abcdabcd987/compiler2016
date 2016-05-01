package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abcdabcd987 on 2016-03-28.
 */
@RunWith(Parameterized.class)
public class ASTBuilderTest {
    @Parameterized.Parameters
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

    public ASTBuilderTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void testAST() throws IOException {
        System.out.println(filename);

        InputStream is = new FileInputStream(filename);
        ANTLRInputStream input = new ANTLRInputStream(is);
        MillLexer lexer = new MillLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MillParser parser = new MillParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        ASTBuilder astBuilder = new ASTBuilder();
        walker.walk(astBuilder, tree);
        ASTPrinter astPrinter = new ASTPrinter(System.out);
        Program program = astBuilder.getProgram();
        program.accept(astPrinter);
    }
}
