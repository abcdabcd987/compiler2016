package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-05-12.
 */
public class FunctionInliner {
    private static class FunctionInfo {
        int numInst = 0;
        int numUsed = 0;
        boolean skip = false;
    }

    private IRRoot ir;
    private Map<Function, FunctionInfo> funcInfo = new HashMap<>();

    public FunctionInliner(IRRoot ir) {
        this.ir = ir;
    }

    private void copyIntValue(Map<Object, Object> renameMap, IntValue value) {
        if (renameMap.containsKey(value)) return;
        renameMap.put(value, value.copy());
    }

    /**
     * @return return moved next ir instruction
     */
    private IRInstruction doInline(Call call) {
        Function func = call.getBasicBlock().getParent();
        Function callee = call.getFunc();
        Map<Object, Object> renameMap = new HashMap<>();
        func.calcReversePostOrder();
        List<BasicBlock> RPO = callee.getReversePostOrder();

        // create entry and exit block
        BasicBlock oldExitBB = callee.exitBB;
        BasicBlock newExitBB = new BasicBlock(func, oldExitBB.getHintName());
        renameMap.put(oldExitBB, newExitBB);
        renameMap.put(callee.getStartBB(), call.getBasicBlock());
        if (func.exitBB == call.getBasicBlock()) func.exitBB = newExitBB;

        // move instructions after `call` to `newExitBB`
        Map<Object, Object> moveMap = Collections.singletonMap(call.getBasicBlock(), newExitBB);
        for (IRInstruction inst = call.getNext(); inst != null; inst = inst.getNext()) {
            if (inst instanceof BranchInstruction)
                newExitBB.end(((BranchInstruction) inst).copyAndRename(moveMap));
            else
                newExitBB.append(inst.copyAndRename(moveMap));
            inst.remove();
        }
        IRInstruction newExitBBHead = newExitBB.getHead();

        // setup args
        for (int i = 0; i < call.getArgs().size(); ++i) {
            VirtualRegister oldReg = callee.argVarRegList.get(i);
            VirtualRegister newReg = oldReg.copy();
            call.prepend(new Move(call.getBasicBlock(), newReg, call.getArgs().get(i)));
            renameMap.put(oldReg, newReg);
        }

        // remove function call
        call.remove();

        // copy blocks
        RPO.stream().filter(x -> !renameMap.containsKey(x)).forEach(x -> renameMap.put(x, new BasicBlock(func, x.getHintName())));

        // copy instructions
        for (BasicBlock oldBB : RPO) {
            BasicBlock newBB = (BasicBlock) renameMap.get(oldBB);
            for (IRInstruction inst = oldBB.getHead(); inst != null; inst = inst.getNext()) {
                inst.getUsedIntValue().forEach(x -> copyIntValue(renameMap, x));
                if (inst.getDefinedRegister() != null)
                    copyIntValue(renameMap, inst.getDefinedRegister());
                if (newBB != newExitBB) {
                    if (inst instanceof BranchInstruction) {
                        if (!(inst instanceof Return))
                            newBB.end(((BranchInstruction) inst).copyAndRename(renameMap));
                    } else {
                        newBB.append(inst.copyAndRename(renameMap));
                    }
                } else {
                    if (!(inst instanceof Return))
                        newExitBBHead.prepend(inst.copyAndRename(renameMap));
                }
            }
        }

        // check call.getBasicBlock()
        if (!call.getBasicBlock().isEnded())
            call.getBasicBlock().end(new Jump(call.getBasicBlock(), newExitBB));

        // copy return value
        Return retInst = callee.retInstruction.get(0);
        if (retInst.getRet() != null) {
            newExitBBHead.prepend(new Move(newExitBB, call.getDest(), (IntValue) renameMap.get(retInst.getRet())));
        }

        return newExitBB.getHead();
    }

    private void countInstructionNumber() {
        ir.functions.values().forEach(x -> funcInfo.put(x, new FunctionInfo()));
        for (Function func : ir.functions.values()) {
            FunctionInfo info = funcInfo.get(func);
            for (BasicBlock BB : func.getReversePostOrder())
                for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                    ++info.numInst;
                    if (inst instanceof Call) {
                        FunctionInfo calleeInfo = funcInfo.get(((Call) inst).getFunc());
                        if (calleeInfo != null) ++calleeInfo.numUsed;
                    }
                }
        }
    }

    public void run() {
        final int MAX_CALLEE_INST = 1<<8;
        final int MAX_CALLER_INST = 1<<14;

        countInstructionNumber();
        funcInfo.forEach((func, info) -> info.skip = func.recursiveCalleeSet.contains(func));

        boolean changed = true;
        List<BasicBlock> RPO = new ArrayList<>();
        List<String> toDeleteFunction = new ArrayList<>();
        while (changed) {
            changed = false;
            toDeleteFunction.clear();
            for (Function func : ir.functions.values()) {
                FunctionInfo info = funcInfo.get(func);
                func.calcReversePostOrder();
                RPO.clear();
                RPO.addAll(func.getReversePostOrder());
                for (BasicBlock BB : RPO) {
                    for (IRInstruction inst = BB.getHead(), next; inst != null; inst = next) {
                        next = inst.getNext();
                        if (!(inst instanceof Call)) continue; // skip non-call
                        Call call = (Call) inst;
                        FunctionInfo calleeInfo = funcInfo.get(call.getFunc());
                        if (calleeInfo == null) continue;      // skip builtin function
                        if (calleeInfo.skip) continue;         // skip self recursion

                        if (calleeInfo.numInst < MAX_CALLEE_INST && calleeInfo.numInst + info.numInst < MAX_CALLER_INST) {
                            next = doInline(call);
                            changed = true;
                            info.numInst += calleeInfo.numInst;
                            if (--calleeInfo.numUsed == 0) {
                                toDeleteFunction.add(call.getFunc().getName());
                            }
                        }
                    }
                }
                func.calcReversePostOrder();
            }
            toDeleteFunction.forEach(ir.functions::remove);
        }

        ir.functions.values().forEach(Function::updateCalleeSet);
        ir.calcRecursiveCalleeSet();
    }
}
