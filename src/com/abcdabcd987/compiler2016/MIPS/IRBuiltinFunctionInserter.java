package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Symbol.FunctionType;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;

import java.util.Arrays;

import static com.abcdabcd987.compiler2016.MIPS.MIPSRegisterSet.*;

/**
 * Created by abcdabcd987 on 2016-05-02.
 */
public class IRBuiltinFunctionInserter {
    private IRRoot ir;
    private final int wordSize = CompilerOptions.getSizeInt();

    public IRBuiltinFunctionInserter(IRRoot ir) {
        this.ir = ir;
    }

    private Function initFunction(FunctionType funcType) {
        Function func = new Function(funcType);
        for (int i = 0; i < funcType.argTypes.size(); ++i) {
            String name = "arg" + i;
            VirtualRegister reg = new VirtualRegister(name);
            func.argVarRegList.add(reg);
        }
        return func;
    }

    private void doStringConcat() {
        if (ir.builtinStringConcat != null) return;
        Function func = initFunction(GlobalSymbolTable.stringConcatFunc);
        ir.builtinStringConcat = func;
        func.builtinFunctionHackName = "stringConcatenate";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1, T2, T3, T4, T5));
        ir.builtinFunctions.add(func);
    }

    private void doPrintString() {
        if (ir.builtinPrintString != null) return;
        Function func = initFunction(GlobalSymbolTable.printStringFunc);
        ir.builtinPrintString = func;
        ir.builtinFunctions.add(func);
    }

    private void doPrintInt() {
        if (ir.builtinPrintInt != null) return;
        Function func = initFunction(GlobalSymbolTable.printIntFunc);
        ir.builtinPrintInt = func;
        ir.builtinFunctions.add(func);
    }

    private void doPrintlnString() {
        if (ir.builtinPrintlnString != null) return;
        Function func = initFunction(GlobalSymbolTable.printlnStringFunc);
        ir.builtinPrintlnString = func;
        ir.builtinFunctions.add(func);
    }


    private void doPrintlnInt() {
        if (ir.builtinPrintlnInt != null) return;
        Function func = initFunction(GlobalSymbolTable.printlnIntFunc);
        ir.builtinPrintlnInt = func;
        ir.builtinFunctions.add(func);
    }

    private void doStringEqual() {
        if (ir.builtinStringEqual != null) return;
        Function func = initFunction(GlobalSymbolTable.stringEqual);
        ir.builtinStringEqual = func;
        func.builtinFunctionHackName = "stringIsEqual";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1));
        ir.builtinFunctions.add(func);
    }

    private void doStringLess() {
        if (ir.builtinStringLess != null) return;
        Function func = initFunction(GlobalSymbolTable.stringLess);
        ir.builtinStringLess = func;
        func.builtinFunctionHackName = "stringLess";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1));
        ir.builtinFunctions.add(func);
    }

    private void doStringSubString() {
        if (ir.builtinStringSubString != null) return;
        Function func = initFunction(GlobalSymbolTable.stringSubString);
        ir.builtinStringSubString = func;
        func.builtinFunctionHackName = "string.substring";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1, T2, T3, T4));
        ir.builtinFunctions.add(func);
    }

    private void doStringParseInt() {
        if (ir.builtinStringParseInt != null) return;
        Function func = initFunction(GlobalSymbolTable.stringParseInt);
        ir.builtinStringParseInt = func;
        func.builtinFunctionHackName = "string.parseInt";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1, T2));
        ir.builtinFunctions.add(func);
    }

    private void doToString() {
        if (ir.builtinToString != null) return;
        Function func = initFunction(GlobalSymbolTable.toStringFunc);
        ir.builtinToString = func;
        func.builtinFunctionHackName = "toString";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, T1, T2, T3, T5, V1));
        ir.builtinFunctions.add(func);
    }

    private void doGetString() {
        if (ir.builtinGetString != null) return;
        Function func = initFunction(GlobalSymbolTable.getStringFunc);
        ir.builtinGetString = func;
        func.builtinFunctionHackName = "getString";
        func.usedPhysicalGeneralRegister.addAll(Arrays.asList(T0, V1));
        ir.builtinFunctions.add(func);
    }

    private void doGetInt() {
        if (ir.builtinGetInt != null) return;
        Function func = initFunction(GlobalSymbolTable.getIntFunc);
        ir.builtinGetInt = func;
        func.builtinFunctionHackName = "getInt";
        ir.builtinFunctions.add(func);
    }

    public void run() {
        doPrintString();
        doPrintInt();
        doPrintlnString();
        doPrintlnInt();
        doToString();
        doGetString();
        doGetInt();
        doStringConcat();
        doStringEqual();
        doStringLess();
        doStringSubString();
        doStringParseInt();
    }
}
