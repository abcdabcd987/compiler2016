package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.FrontEnd.*;
import com.abcdabcd987.compiler2016.IR.IRRoot;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.fail;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
@RunWith(Parameterized.class)
public class LLVMIRPrinterTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        for (File f : new File("testcase/ir/").listFiles()) {
            if (f.isFile() && f.getName().endsWith(".mx")) {
                params.add(new Object[] { "testcase/ir/" + f.getName() });
            }
        }
        return params;
    }

    private String filename;

    public LLVMIRPrinterTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void testPass() throws IOException, InterruptedException {
        System.out.println(filename);

        Process lli = new ProcessBuilder()
                .command("lli")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        PrintStream out = new PrintStream(lli.getOutputStream());

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
        Program ast = astBuilder.getProgram();
        ASTPrintVisitor printer = new ASTPrintVisitor();

        CompilationError ce = new CompilationError();
        GlobalSymbolTable sym = new GlobalSymbolTable();
        StructSymbolScanner structSymbolScanner = new StructSymbolScanner(sym, ce);
        StructFunctionDeclarator structFunctionDeclarator = new StructFunctionDeclarator(sym, ce);
        SemanticChecker semanticChecker = new SemanticChecker(sym, ce);
        IRBuilder irBuilder = new IRBuilder();
        LLVMIRPrinter llvmirPrinter = new LLVMIRPrinter(out);
        LLVMIRPrinter llvmirPrinterStdout = new LLVMIRPrinter(System.out);

        ast.accept(structSymbolScanner);
        ast.accept(structFunctionDeclarator);
        ast.accept(semanticChecker);
        ast.accept(irBuilder);

        IRRoot ir = irBuilder.getIRRoot();

        ir.accept(llvmirPrinter);
        ir.accept(llvmirPrinterStdout);

        out.flush();
        out.close();
        int exitcode = lli.waitFor();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = br.readLine();
        } while (!line.startsWith("// assert: "));
        String assertion = line.replace("// assert: ", "").trim();
        if (assertion.equals("exitcode")) {
            int expected = Integer.valueOf(br.readLine().replace("// ", "").trim());
            if (exitcode != expected)
                throw new RuntimeException("exitcode = " + exitcode + ", expected: " + expected);
        } else if (assertion.equals("exception")) {
            if (exitcode == 0)
                throw new RuntimeException("exitcode = 0, expected an exception.");
        } else {
            throw new RuntimeException("unknown assertion.");
        }
        br.close();
    }
}
