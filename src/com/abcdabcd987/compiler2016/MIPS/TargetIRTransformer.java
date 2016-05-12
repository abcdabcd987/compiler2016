package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Symbol.Type;

import java.util.*;

import static com.abcdabcd987.compiler2016.MIPS.MIPSRegisterSet.*;

/**
 * Created by abcdabcd987 on 2016-05-01.
 */
public class TargetIRTransformer {
    private static class FunctionInfo {
        int beginArg;
        int beginSavedReg;
        int beginRA;
        int beginLocal;
        int beginTempReg;
        int frameSize;
        List<PhysicalRegister> usedCallerSaveRegister = new ArrayList<>();
        List<PhysicalRegister> usedCalleeSaveRegister = new ArrayList<>();
        Map<StackSlot, Integer> stackSlotOffset = new HashMap<>();
        Set<PhysicalRegister> recursiveUsedRegister = new HashSet<>();
    }

    private IRRoot irRoot;
    private final int sizeWord = CompilerOptions.getSizeInt();
    private Map<Function, FunctionInfo> funcInfo = new HashMap<>();

    public TargetIRTransformer(IRRoot irRoot) {
        this.irRoot = irRoot;
    }

    /**
     * <pre>
     *     | previous frame |
     *     |________________|    <-  $sp + frameSize           (high)
     *     | $t? backup     |                                    ^
     *     | ...            |                                    |
     *     | $t0 backup     |    <- $sp + beginTempReg           |
     *     |----------------|                                    |
     *     | local data m-1 |                                    |
     *     | ...            |                                    |
     *     | local data 0   |    <- $sp + beginLocal             |
     *     |----------------|                                    |
     *     | return address |    <- $sp + beginRA                |
     *     |----------------|                                    |
     *     | $s? backup     |                                    |
     *     | ...            |                                    |
     *     | $s0 backup     |    <- $sp + beginSavedReg          |
     *     |----------------|                                    |
     *     | arg n-1        |                                    |
     *     | ...            |                                    |
     *     | arg 0          |    <- $sp + beginArg             (low)
     *     |----------------|
     * </pre>
     */
    private void calcFrame(Function func) {
        FunctionInfo info = funcInfo.get(func);
        for (PhysicalRegister pr : func.usedPhysicalGeneralRegister) {
            if (pr.isCallerSave()) info.usedCallerSaveRegister.add(pr);
            if (pr.isCalleeSave()) info.usedCalleeSaveRegister.add(pr);
        }

        info.beginArg = 0;
        info.beginSavedReg = info.beginArg      + func.argVarRegList.size()          * sizeWord;
        info.beginRA       = info.beginSavedReg + info.usedCalleeSaveRegister.size() * sizeWord;
        info.beginLocal    = info.beginRA       +                                      sizeWord;
        info.beginTempReg  = info.beginLocal    + func.stackSlots.size()             * sizeWord;
        info.frameSize     = info.beginTempReg  + info.usedCallerSaveRegister.size() * sizeWord;

        for (int i = 0; i < func.stackSlots.size(); ++i) {
            StackSlot slot = func.stackSlots.get(i);
            info.stackSlotOffset.put(slot, info.beginLocal + i* sizeWord);
        }

        for (int i = 0; i < func.argVarRegList.size(); ++i) {
            VirtualRegister arg = func.argVarRegList.get(i);
            StackSlot slot = func.argStackSlotMap.get(arg);
            info.stackSlotOffset.put(slot, info.beginArg + i * sizeWord);
        }
    }

    private void modifyEntry(Function func) {
        FunctionInfo info = funcInfo.get(func);
        BasicBlock entryBB = func.getStartBB();
        IRInstruction firstInst = func.getStartBB().getHead();

        // extend frame
        firstInst.prepend(new BinaryOperation(entryBB, SP, BinaryOperation.BinaryOp.SUB, SP, new IntImmediate(info.frameSize)));

        // save $ra
        firstInst.prepend(new Store(entryBB, RA, sizeWord, SP, info.beginRA));

        // save $s?
        for (int i = 0; i < info.usedCalleeSaveRegister.size(); ++i)
            firstInst.prepend(new Store(entryBB, info.usedCalleeSaveRegister.get(i), sizeWord, SP, info.beginSavedReg + i * sizeWord));
    }

    private void modifyReturn(Function func) {
        // move to $v0 on return instruction
        if (func.getType().returnType.type != Type.Types.VOID) {
            for (Return ret : func.retInstruction) {
                ret.prepend(new Move(ret.getBasicBlock(), V0, ret.getRet()));
            }
        }
    }

    private void modifyExit(Function func) {
        FunctionInfo info = funcInfo.get(func);
        BasicBlock exitBB = func.exitBB;
        IRInstruction lastInst = exitBB.getLast();

        // restore $s?
        for (int i = 0; i < info.usedCalleeSaveRegister.size(); ++i)
            lastInst.prepend(new Load(exitBB, info.usedCalleeSaveRegister.get(i), sizeWord, SP, info.beginSavedReg + i * sizeWord));

        // restore $ra
        lastInst.prepend(new Load(exitBB, RA, sizeWord, SP, info.beginRA));

        // shrink frame
        lastInst.prepend(new BinaryOperation(exitBB, SP, BinaryOperation.BinaryOp.ADD, SP, new IntImmediate(info.frameSize)));
    }

    private void modifyStackSlot(Function func, FunctionInfo info, BasicBlock BB, IRInstruction inst) {
        // stack slot -> $sp offset
        if (inst instanceof Load) {
            Load load = (Load) inst;
            if (load.address instanceof StackSlot) {
                load.offset = info.stackSlotOffset.get(load.address);
                load.address = SP;
            }
        } else if (inst instanceof Store) {
            Store store = (Store) inst;
            if (store.address instanceof StackSlot) {
                StackSlot slot = (StackSlot) store.address;
                assert slot.getParent() == func;
                store.offset = info.stackSlotOffset.get(slot);
                store.address = SP;
            }
        }
    }

    private void modifyCall(Function func, FunctionInfo info, BasicBlock BB, IRInstruction inst) {
        if (!(inst instanceof Call)) return;
        Call call = (Call) inst;
        Function callee = call.getFunc();
        List<IntValue> args = call.getArgs();
        FunctionInfo calleeInfo = funcInfo.get(callee);

        // save $t? register
        for (int i = 0; i < info.usedCallerSaveRegister.size(); ++i) {
            PhysicalRegister reg = info.usedCallerSaveRegister.get(i);
            if (calleeInfo.recursiveUsedRegister.contains(reg))
                inst.prepend(new Store(BB, reg, sizeWord, SP, info.beginTempReg + i * sizeWord));
        }

        // save $a? register
        if (func.argVarRegList.size() > 0) inst.prepend(new Store(BB, A0, sizeWord, SP, info.beginArg));
        if (func.argVarRegList.size() > 1) inst.prepend(new Store(BB, A1, sizeWord, SP, info.beginArg + sizeWord));
        if (func.argVarRegList.size() > 2) inst.prepend(new Store(BB, A2, sizeWord, SP, info.beginArg + 2*sizeWord));
        if (func.argVarRegList.size() > 3) inst.prepend(new Store(BB, A3, sizeWord, SP, info.beginArg + 3*sizeWord));

        // copy argument
        if (callee.builtinFunctionHackName == null) {
            // normal function call
            for (int i = 0; i < args.size(); ++i) {
                IntValue value = args.get(i);
                if (value instanceof IntImmediate) {
                    inst.prepend(new Move(BB, A0, args.get(i)));
                    value = A0;
                } else if (value instanceof StackSlot) {
                    StackSlot slot = (StackSlot) value;
                    assert slot.getParent() == func;
                    inst.prepend(new Load(BB, A0, sizeWord, SP, info.stackSlotOffset.get(slot)));
                    value = A0;
                }
                inst.prepend(new Store(BB, value, sizeWord, SP, - calleeInfo.frameSize + calleeInfo.beginArg + i*sizeWord));
            }
            if (args.size() > 0) inst.prepend(new Load(BB, A0, sizeWord, SP, - calleeInfo.frameSize + calleeInfo.beginArg));
            if (args.size() > 1) inst.prepend(new Load(BB, A1, sizeWord, SP, - calleeInfo.frameSize + calleeInfo.beginArg + sizeWord));
            if (args.size() > 2) inst.prepend(new Load(BB, A2, sizeWord, SP, - calleeInfo.frameSize + calleeInfo.beginArg + 2*sizeWord));
            if (args.size() > 3) inst.prepend(new Load(BB, A3, sizeWord, SP, - calleeInfo.frameSize + calleeInfo.beginArg + 3*sizeWord));
        } else {
            // builtin function call hack
            for (int i = 0; i < args.size(); ++i) {
                MIPSRegister target = null;
                if      (i == 0) target = A0;
                else if (i == 1) target = A1;
                else if (i == 2) target = A2;
                else if (i == 3) target = A3;
                assert target != null;

                IntValue value = args.get(i);
                if (value instanceof StackSlot) {
                    StackSlot slot = (StackSlot) value;
                    assert slot.getParent() == func;
                    inst.prepend(new Load(BB, target, sizeWord, SP, info.stackSlotOffset.get(slot)));
                } else {
                    inst.prepend(new Move(BB, target, value));
                }
            }
        }

        // move result
        if (call.getDest() != null) inst.append(new Move(BB, call.getDest(), V0));

        // restore $a? register
        if (func.argVarRegList.size() > 0) inst.append(new Load(BB, A0, sizeWord, SP, info.beginArg));
        if (func.argVarRegList.size() > 1) inst.append(new Load(BB, A1, sizeWord, SP, info.beginArg + sizeWord));
        if (func.argVarRegList.size() > 2) inst.append(new Load(BB, A2, sizeWord, SP, info.beginArg + 2*sizeWord));
        if (func.argVarRegList.size() > 3) inst.append(new Load(BB, A3, sizeWord, SP, info.beginArg + 3*sizeWord));

        // restore $t? register
        for (int i = 0; i < info.usedCallerSaveRegister.size(); ++i) {
            PhysicalRegister reg = info.usedCallerSaveRegister.get(i);
            if (calleeInfo.recursiveUsedRegister.contains(reg))
                inst.append(new Load(BB, reg, sizeWord, SP, info.beginTempReg + i * sizeWord));
        }
    }

    private void removeSelfMove(IRInstruction inst) {
        if (!(inst instanceof Move)) return;
        Move move = (Move) inst;
        if (move.getSource() == move.getDest()) inst.remove();
    }

    private void calcRecursiveRegisterUse() {
        for (Function func : irRoot.builtinFunctions) {
            FunctionInfo info = new FunctionInfo();
            funcInfo.put(func, info);
        }
        for (Map.Entry<Function, FunctionInfo> entry : funcInfo.entrySet()) {
            Function func = entry.getKey();
            FunctionInfo info = entry.getValue();
            info.recursiveUsedRegister.addAll(func.usedPhysicalGeneralRegister);
            func.recursiveCalleeSet.forEach(x -> info.recursiveUsedRegister.addAll(x.usedPhysicalGeneralRegister));
        }
    }

    public void run() {
        for (Function func : irRoot.functions.values()) {
            FunctionInfo info = new FunctionInfo();
            funcInfo.put(func, info);
            calcFrame(func);
        }

        calcRecursiveRegisterUse();

        for (Function func : irRoot.functions.values()) {
            FunctionInfo info = funcInfo.get(func);
            modifyEntry(func);
            modifyReturn(func);
            modifyExit(func);

            for (BasicBlock BB : func.getReversePostOrder()) {
                for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                    modifyCall(func, info, BB, inst);
                    modifyStackSlot(func, info, BB, inst);
                    removeSelfMove(inst);
                }
            }
        }
    }
}
