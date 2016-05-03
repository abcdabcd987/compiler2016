package com.abcdabcd987.compiler2016;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.BackEnd.*;
import com.abcdabcd987.compiler2016.FrontEnd.*;
import com.abcdabcd987.compiler2016.IR.Function;
import com.abcdabcd987.compiler2016.IR.IRRoot;
import com.abcdabcd987.compiler2016.IR.PhysicalRegister;
import com.abcdabcd987.compiler2016.MIPS.MIPSPrinter;
import com.abcdabcd987.compiler2016.MIPS.MIPSRegisterSet;
import com.abcdabcd987.compiler2016.MIPS.RegisterInformationInjector;
import com.abcdabcd987.compiler2016.MIPS.TargetIRTransformer;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by abcdabcd987 on 2016-05-01.
 */
public class Mill {
    private InputStream in;
    private PrintStream out;
    private Program ast;
    private IRRoot ir;

    public Mill(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    private void buildAST() throws IOException {
        MillParser parser = new MillParser(new CommonTokenStream(new MillLexer(new ANTLRInputStream(in))));
        parser.setErrorHandler(new BailErrorStrategy());
        ASTBuilder astBuilder = new ASTBuilder();
        new ParseTreeWalker().walk(astBuilder, parser.program());
        ast = astBuilder.getProgram();
        in = null;
    }

    private void printAST() {
        ast.accept(new ASTPrinter(out));
    }

    private void buildIR() {
        CompilationError ce = new CompilationError();
        GlobalSymbolTable sym = new GlobalSymbolTable();
        IRBuilder irBuilder = new IRBuilder(sym);

        ast.accept(new StructSymbolScanner(sym, ce));
        ast.accept(new GlobalVariableInitializationHacker(sym));
        ast.accept(new StructFunctionDeclarator(sym, ce));
        ast.accept(new SemanticChecker(sym, ce));

        ir = irBuilder.getIRRoot();
        new IRBuiltinFunctionInserter(ir).run();

        ast.accept(irBuilder);
        ast = null;

        ir.builtinFunctions.forEach(x -> ir.functions.put(x.getName(), x));
    }

    private void printIR() {
        ir.accept(new IRPrinter(out));
    }

    private void constructSSA() {
        for (Function func : ir.functions.values()) {
            SSATransformer ssaTransformer = new SSATransformer(func);
            ssaTransformer.construct();
        }
    }

    private void destructSSA() {
        for (Function func : ir.functions.values()) {
            SSATransformer ssaTransformer = new SSATransformer(func);
            ssaTransformer.destroy();
        }
    }

    private void allocateRegister() throws Exception {
        for (Function func : ir.functions.values()) {
            switch (CompilerOptions.registerAllocator) {
                case "local": new LocalBottomUpAllocator(MIPSRegisterSet.general, func).run(); break;
                case "no": new StupidAllocator(MIPSRegisterSet.general, func).run(); break;
                default: throw new Exception("unknown register allocator");
            }
        }
    }

    public void run() throws Exception {
        buildAST();
        if (CompilerOptions.ifPrintAST) printAST();
        buildIR();
        new GlobalVariableResolver(ir).run();
        if (CompilerOptions.ifPrintRawIR) printIR();
        if (CompilerOptions.enableSSA) {
            constructSSA();
            if (CompilerOptions.ifPrintSSAIR) printIR();
            destructSSA();
        }
        new RegisterInformationInjector(ir).run();
        allocateRegister();
        new TargetIRTransformer(ir).run();
        ir.accept(new MIPSPrinter(out));
    }
}
