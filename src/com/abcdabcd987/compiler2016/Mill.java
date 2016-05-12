package com.abcdabcd987.compiler2016;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.BackEnd.*;
import com.abcdabcd987.compiler2016.FrontEnd.*;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.MIPS.*;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    private void printIR() {
        ir.accept(new IRPrinter(out));
    }

    private void processSSA() {
        List<SSATransformer> ssaTransformers = ir.functions.values().stream().map(SSATransformer::new).collect(Collectors.toList());
        ssaTransformers.forEach(SSATransformer::construct);
        if (CompilerOptions.ifPrintSSAIR) printIR();
        if (CompilerOptions.eliminateDeadCode) ssaTransformers.forEach(SSATransformer::naivelyEliminateDeadCode);
        if (CompilerOptions.simpleConstantPropagate) ssaTransformers.forEach(SSATransformer::simpleConstantPropagate);
        ssaTransformers.forEach(SSATransformer::destroy);
    }

    private void allocateRegister() throws Exception {
        RegisterAllocator allocator;
        Collection<PhysicalRegister> regs = MIPSRegisterSet.general;
        switch (CompilerOptions.registerAllocator) {
            case "local": allocator = new LocalBottomUpAllocator(ir, regs); break;
            case "no"   : allocator = new StupidAllocator(ir, regs); break;
            case "color": allocator = new GraphColoringAllocator(ir, regs, MIPSRegisterSet.T0, MIPSRegisterSet.T1); break;
            default: throw new Exception("unknown register allocator");
        }
        allocator.run();
    }

    public void run() throws Exception {
        buildAST();
        if (CompilerOptions.ifPrintAST) printAST();
        buildIR();
        if (CompilerOptions.ifPrintRawIR) printIR();
        if (CompilerOptions.enableInline) new FunctionInliner(ir).run();
        if (CompilerOptions.ifPrintIRAfterInline) printIR();
        new GlobalVariableResolver(ir).run();
        if (CompilerOptions.enableSSA) processSSA();
        new RegisterInformationInjector(ir).run();
        allocateRegister();
        new TargetIRTransformer(ir).run();
        ir.accept(new MIPSPrinter(out));
    }

    private static void printHelpAndExit(boolean illegal) {
        if (illegal) System.out.println("Error! Unknown argument sequence.");
        System.out.println("Mill - Mx* language implementation made with love by abcdabcd987");
        System.out.println("Usage: mill [options] [input]");
        System.out.println("Options:");
        System.out.println("  -help              Print this help message");
        System.out.println("  -o <file>          Write output to <file>");
        System.out.println("  -reg-alloc <val>   Set register allocator to <val>");
        System.out.println("                     Available register allocators:");
        System.out.println("                       no:    Don't allocate at all. (CISC-like)");
        System.out.println("                       local: Local bottom-up allocator");
        System.out.println("                       color: Global allocation by interference graph coloring");
        System.out.println("  -print-ast         Print the abstract semantic tree");
        System.out.println("  -print-ir          Print the intermediate representation");
        System.out.println("  -print-ssa-ir      Print the intermediate representation after SSA transforms");
        System.out.println("  -no-inline         Disable function inlining");
        System.out.println("  -no-ssa            Disable single static assignment analysis and transforms");
        System.out.println("  -no-naive-dce      Disable naive dead code elimination");
        System.out.println("  -no-scp            Disable simple constant propagate");
        System.exit(illegal ? 1 : 0);
    }

    public static void main(String[] argv) throws Exception {
        // check options
        String inFile = null;
        String outFile = null;
        for (int i = 0; i < argv.length; ++i) {
            String arg = argv[i];
            switch (arg) {
                case "-no-ssa":
                    CompilerOptions.enableSSA = false;
                    break;

                case "-print-ast":
                    CompilerOptions.ifPrintAST = true;
                    break;

                case "-print-ir":
                    CompilerOptions.ifPrintRawIR = true;
                    break;

                case "-print-ssa-ir":
                    CompilerOptions.ifPrintSSAIR = true;
                    break;

                case "-reg-alloc":
                    if (i+1 >= argv.length) printHelpAndExit(true);
                    CompilerOptions.registerAllocator = argv[++i];
                    break;

                case "-o":
                    if (i+1 >= argv.length) printHelpAndExit(true);
                    outFile = argv[++i];
                    break;

                case "-no-inline":
                    CompilerOptions.enableInline = false;
                    break;

                case "-no-naive-dce":
                    CompilerOptions.eliminateDeadCode = false;
                    break;

                case "-no-scp":
                    CompilerOptions.simpleConstantPropagate = false;
                    break;

                case "-help":
                case "--help":
                case "-?":
                case "/?":
                    printHelpAndExit(false);
                    break;

                default:
                    if (inFile != null) printHelpAndExit(true);
                    inFile = arg;
            }
        }

        // run compiler
        InputStream in = inFile == null ? System.in : new FileInputStream(inFile);
        PrintStream out = outFile == null ? System.out : new PrintStream(new FileOutputStream(outFile));
        new Mill(in, out).run();
    }
}
