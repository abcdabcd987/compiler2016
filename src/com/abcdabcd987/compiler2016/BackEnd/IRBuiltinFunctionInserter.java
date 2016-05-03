package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Symbol.FunctionType;
import com.abcdabcd987.compiler2016.Symbol.GlobalSymbolTable;

import static com.abcdabcd987.compiler2016.IR.BinaryOperation.BinaryOp.*;
import static com.abcdabcd987.compiler2016.IR.IntComparison.Condition.*;

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
            func.argVarReg.put(name, reg);
            func.argVarRegList.add(reg);
        }
        return func;
    }

    private void doStringConcat() {
        if (ir.builtinStringConcat != null) return;
        Function func = initFunction(GlobalSymbolTable.stringConcatFunc);
        ir.builtinStringConcat = func;
        ir.builtinFunctions.add(func);

        BasicBlock startBB = func.getStartBB();
        BasicBlock copy1BB = new BasicBlock(func, "copy1");
        BasicBlock copy2BB = new BasicBlock(func, "copy2");
        BasicBlock copy1LoopBB = new BasicBlock(func, "copy1_loop");
        BasicBlock copy2LoopBB = new BasicBlock(func, "copy2_loop");
        BasicBlock afterBB = new BasicBlock(func, "after");
        VirtualRegister s1Reg = new VirtualRegister("s1");
        VirtualRegister s2Reg = new VirtualRegister("s2");
        VirtualRegister addrReg = new VirtualRegister("addr");
        VirtualRegister l1Reg = new VirtualRegister("l1");
        VirtualRegister l2Reg = new VirtualRegister("l2");
        VirtualRegister lenReg = new VirtualRegister("len");
        VirtualRegister iReg = new VirtualRegister("i");
        VirtualRegister jReg = new VirtualRegister("j");
        VirtualRegister kReg = new VirtualRegister("k");
        VirtualRegister tReg = new VirtualRegister("t");

        startBB.append(new Move(startBB, s1Reg, func.argVarRegList.get(0)));
        startBB.append(new Move(startBB, s2Reg, func.argVarRegList.get(1)));
        startBB.append(new Load(startBB, l1Reg, wordSize, s1Reg, 0));
        startBB.append(new Load(startBB, l2Reg, wordSize, s2Reg, 0));
        startBB.append(new BinaryOperation(startBB, lenReg, ADD, l1Reg, l2Reg));
        startBB.append(new BinaryOperation(startBB, addrReg, ADD, lenReg, new IntImmediate(1+ wordSize)));
        startBB.append(new HeapAllocate(startBB, addrReg, addrReg));
        startBB.append(new Store(startBB, wordSize, addrReg, 0, lenReg));
        startBB.append(new BinaryOperation(startBB, s1Reg, ADD, s1Reg, new IntImmediate(4)));
        startBB.append(new BinaryOperation(startBB, s2Reg, ADD, s2Reg, new IntImmediate(4)));
        startBB.append(new BinaryOperation(startBB, kReg, ADD, addrReg, new IntImmediate(4)));
        startBB.append(new Move(startBB, iReg, s1Reg));
        startBB.append(new Move(startBB, jReg, s2Reg));
        startBB.append(new BinaryOperation(startBB, l1Reg, ADD, l1Reg, s1Reg));
        startBB.append(new BinaryOperation(startBB, l2Reg, ADD, l2Reg, s2Reg));
        startBB.end(new Jump(startBB, copy1BB));

        copy1BB.append(new IntComparison(copy1BB, tReg, LT, iReg, l1Reg));
        copy1BB.end(new Branch(copy1BB, tReg, copy1LoopBB, copy2BB));

        copy1LoopBB.append(new Load(copy1LoopBB, tReg, 1, iReg, 0));
        copy1LoopBB.append(new Store(copy1LoopBB, 1, kReg, 0, tReg));
        copy1LoopBB.append(new BinaryOperation(copy1LoopBB, iReg, ADD, iReg, new IntImmediate(1)));
        copy1LoopBB.append(new BinaryOperation(copy1LoopBB, kReg, ADD, kReg, new IntImmediate(1)));
        copy1LoopBB.end(new Jump(copy1LoopBB, copy1BB));

        copy2BB.append(new IntComparison(copy2BB, tReg, LT, jReg, l2Reg));
        copy2BB.end(new Branch(copy2BB, tReg, copy2LoopBB, afterBB));

        copy2LoopBB.append(new Load(copy2LoopBB, tReg, 1, jReg, 0));
        copy2LoopBB.append(new Store(copy2LoopBB, 1, kReg, 0, tReg));
        copy2LoopBB.append(new BinaryOperation(copy2LoopBB, jReg, ADD, jReg, new IntImmediate(1)));
        copy2LoopBB.append(new BinaryOperation(copy2LoopBB, kReg, ADD, kReg, new IntImmediate(1)));
        copy2LoopBB.end(new Jump(copy2LoopBB, copy1BB));

        afterBB.append(new Store(afterBB, 1, kReg, 0, new IntImmediate(0)));
        afterBB.end(new Return(afterBB, addrReg));
    }

    private void doPrint() {
        if (ir.builtinPrint != null) return;
        Function func = initFunction(GlobalSymbolTable.printFunc);
        ir.builtinPrint = func;
//        ir.builtinFunctions.add(func);
    }

    private void doPrintln() {
        if (ir.builtinPrintln != null) return;
        Function func = initFunction(GlobalSymbolTable.printlnFunc);
        ir.builtinPrintln = func;
        ir.builtinFunctions.add(func);

        BasicBlock startBB = func.getStartBB();
        BasicBlock copyBB = new BasicBlock(func, "copy");
        BasicBlock loopBB = new BasicBlock(func, "loop");
        BasicBlock afterBB = new BasicBlock(func, "after");
        VirtualRegister addrReg = new VirtualRegister("addr");
        VirtualRegister lenReg = new VirtualRegister("len");
        VirtualRegister iReg = new VirtualRegister("i");
        VirtualRegister jReg = new VirtualRegister("j");
        VirtualRegister tReg = new VirtualRegister("t");
        VirtualRegister sReg = new VirtualRegister("s");

        startBB.append(new Move(startBB, sReg, func.argVarRegList.get(0)));
        startBB.append(new Load(startBB, lenReg, wordSize, sReg, 0));
        startBB.append(new BinaryOperation(startBB, addrReg, ADD, lenReg, new IntImmediate(2)));
        startBB.append(new HeapAllocate(startBB, addrReg, addrReg));
        startBB.append(new BinaryOperation(startBB, jReg, ADD, sReg, new IntImmediate(wordSize)));
        startBB.append(new BinaryOperation(startBB, lenReg, ADD, lenReg, jReg));
        startBB.append(new Move(startBB, iReg, addrReg));
        startBB.end(new Jump(startBB, copyBB));

        copyBB.append(new IntComparison(copyBB, tReg, LT, jReg, lenReg));
        copyBB.end(new Branch(copyBB, tReg, loopBB, afterBB));

        loopBB.append(new Load(loopBB, tReg, 1, jReg, 0));
        loopBB.append(new Store(loopBB, 1, iReg, 0, tReg));
        loopBB.append(new BinaryOperation(loopBB, iReg, ADD, iReg, new IntImmediate(1)));
        loopBB.append(new BinaryOperation(loopBB, jReg, ADD, jReg, new IntImmediate(1)));
        loopBB.end(new Jump(loopBB, copyBB));

        afterBB.append(new Store(afterBB, 1, iReg, 0, new IntImmediate('\n')));
        afterBB.append(new Store(afterBB, 1, iReg, 1, new IntImmediate(0)));
        Call call = new Call(afterBB, null, ir.builtinPrint);
        call.appendArg(addrReg);
        afterBB.append(call);
        afterBB.end(new Return(afterBB, null));
    }

    public void run() {
        doPrint();
        doPrintln();
        doStringConcat();
    }
}
