package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.FrontEnd.*;
import com.abcdabcd987.compiler2016.IR.Function;
import com.abcdabcd987.compiler2016.IR.IRRoot;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import com.abcdabcd987.compiler2016.Utility.TeeOutputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
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

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
@RunWith(Parameterized.class)
public class SSADestructorTest {
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

    public SSADestructorTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void testPass() throws IOException, InterruptedException {
        System.out.println(filename);

        ByteArrayOutputStream irTextOut = new ByteArrayOutputStream();
        OutputStream tee = new TeeOutputStream(System.out, irTextOut);
        PrintStream out = new PrintStream(tee);

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

        CompilationError ce = new CompilationError();
        GlobalSymbolTable sym = new GlobalSymbolTable();
        StructSymbolScanner structSymbolScanner = new StructSymbolScanner(sym, ce);
        StructFunctionDeclarator structFunctionDeclarator = new StructFunctionDeclarator(sym, ce);
        SemanticChecker semanticChecker = new SemanticChecker(sym, ce);
        GlobalVariableInitializationHacker hacker = new GlobalVariableInitializationHacker(sym);
        IRBuilder irBuilder = new IRBuilder(sym);
        IRPrinter irPrinter = new IRPrinter(out);

        ast.accept(structSymbolScanner);
        ast.accept(structFunctionDeclarator);
        ast.accept(semanticChecker);
        ast.accept(hacker);
        ast.accept(irBuilder);

        IRRoot ir = irBuilder.getIRRoot();
        for (Function func : ir.functions.values()) {
            SSATransformer ssaTransformer = new SSATransformer(func);
            ssaTransformer.construct();
            ssaTransformer.destroy();
        }

        ir.accept(irPrinter);

        out.flush();
        irTextOut.close();

        byte[] irText = irTextOut.toByteArray();
        ByteInputStream vmIn = new ByteInputStream(irText, irText.length);
        LLIRInterpreter vm = new LLIRInterpreter(vmIn, false);
        vm.run();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = br.readLine();
        } while (!line.startsWith("/*! assert:"));
        String assertion = line.replace("/*! assert:", "").trim();
        if (assertion.equals("exitcode")) {
            int expected = Integer.valueOf(br.readLine().trim());
            if (vm.getExitcode() != expected)
                throw new RuntimeException("exitcode = " + vm.getExitcode() + ", expected: " + expected);
        } else if (assertion.equals("exception")) {
            if (!vm.exitException())
                throw new RuntimeException("exit successfully, expected an exception.");
        } else {
            throw new RuntimeException("unknown assertion.");
        }
        br.close();
    }
}
