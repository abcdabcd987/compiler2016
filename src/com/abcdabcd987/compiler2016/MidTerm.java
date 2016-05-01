package com.abcdabcd987.compiler2016;

import com.abcdabcd987.compiler2016.AST.Program;
import com.abcdabcd987.compiler2016.FrontEnd.*;
import com.abcdabcd987.compiler2016.Parser.MillLexer;
import com.abcdabcd987.compiler2016.Parser.MillParser;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.InputStream;

/**
 * Created by abcdabcd987 on 2016-04-06.
 */
public class MidTerm {
    public static void main(String[] argv) {
        try {
            InputStream is = System.in;
            ANTLRInputStream input = new ANTLRInputStream(is);
            MillLexer lexer = new MillLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MillParser parser = new MillParser(tokens);

            ParseTree tree = parser.program();
            ParseTreeWalker walker = new ParseTreeWalker();
            ASTBuilder astBuilder = new ASTBuilder();
            walker.walk(astBuilder, tree);
            Program program = astBuilder.getProgram();

            CompilationError ce = new CompilationError();
            GlobalSymbolTable sym = new GlobalSymbolTable();
            StructSymbolScanner structSymbolScanner = new StructSymbolScanner(sym, ce);
            StructFunctionDeclarator structFunctionDeclarator = new StructFunctionDeclarator(sym, ce);
            SemanticChecker semanticChecker = new SemanticChecker(sym, ce);

            program.accept(structSymbolScanner);
            program.accept(structFunctionDeclarator);
            program.accept(semanticChecker);
            //program.accept(printer);
        } catch (Exception e) {
            //e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
