package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abcdabcd987 on 2016-04-02.
 */
@RunWith(Parameterized.class)
public class SemanticCheckerTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        for (File f : new File("testcase/semantic/passed/").listFiles()) {
            if (f.isFile() && f.getName().endsWith(".mx")) {
                params.add(new Object[] { "testcase/semantic/passed/" + f.getName(), true });
            }
        }
        for (File f : new File("testcase/semantic/compile_error/").listFiles()) {
            if (f.isFile() && f.getName().endsWith(".mx")) {
                params.add(new Object[] { "testcase/semantic/compile_error/" + f.getName(), false });
            }
        }
        return params;
    }

    private String filename;
    private boolean shouldPass;

    public SemanticCheckerTest(String filename, boolean shouldPass) {
        this.filename = filename;
        this.shouldPass = shouldPass;
    }

    @Test
    public void testPass() throws IOException {
        System.out.println(filename);

        try {
            InputStream is = new FileInputStream(filename);
            ANTLRInputStream input = new ANTLRInputStream(is);
            MillLexer lexer = new MillLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MillParser parser = new MillParser(tokens);

            ParseTree tree = parser.program();
            ParseTreeWalker walker = new ParseTreeWalker();
            ASTBuilder astBuilder = new ASTBuilder();
            walker.walk(astBuilder, tree);
            Program program = astBuilder.getProgram();
            ASTPrinter printer = new ASTPrinter(System.out);

            CompilationError ce = new CompilationError();
            GlobalSymbolTable sym = new GlobalSymbolTable();
            StructSymbolScanner structSymbolScanner = new StructSymbolScanner(sym, ce);
            program.accept(structSymbolScanner);
            StructFunctionDeclarator structFunctionDeclarator = new StructFunctionDeclarator(sym, ce);
            program.accept(structFunctionDeclarator);
            SemanticChecker semanticChecker = new SemanticChecker(sym, ce);
            program.accept(semanticChecker);
            program.accept(printer);
            if (!shouldPass) fail("Should not pass.");
        } catch (Exception e) {
            if (shouldPass) throw e;
            else e.printStackTrace(System.err);
        }
    }
}
