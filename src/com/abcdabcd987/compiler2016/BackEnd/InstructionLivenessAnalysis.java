package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instruction level liveness analysis for virtual register and stack slot.
 * Used by graph coloring register allocation.
 * See: http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
 * Use reverse pre order (although the best one is the reverse post order on the reverse CFG)
 * Created by abcdabcd987 on 2016-05-05.
 */
public class InstructionLivenessAnalysis {
    private IRRoot ir;

    public InstructionLivenessAnalysis(IRRoot ir) {
        this.ir = ir;
    }

    private void initBlock(BasicBlock BB) {
        for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext())
            if (inst.liveOut == null) {
                inst.liveOut = new HashSet<>();
                inst.liveIn = new HashSet<>();
            } else {
                inst.liveOut.clear();
                inst.liveIn.clear();
            }
    }

    private void processFunction(Function func) {
        func.calcReversePreOrder();
        List<BasicBlock> RPO = func.getReversePreOrder();
        RPO.forEach(this::initBlock);

        Set<VirtualRegister> out = new HashSet<>();
        Set<VirtualRegister> in = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock BB : RPO) {
                for (IRInstruction inst = BB.getLast(); inst != null; inst = inst.getPrev()) {
                    in.clear();
                    out.clear();
                    if (inst instanceof Branch) {
                        out.addAll(((Branch) inst).getThen().getHead().liveIn);
                        out.addAll(((Branch) inst).getElse().getHead().liveIn);
                    } else if (inst instanceof Jump) {
                        out.addAll(((Jump) inst).getTarget().getHead().liveIn);
                    } else if (!(inst instanceof Return)) {
                        assert inst.getNext() != null; // inst is not a branch, thus inst.getNext() not null
                        out.addAll(inst.getNext().liveIn);
                    }
                    in.addAll(out);
                    Register defined = inst.getDefinedRegister();
                    if (defined instanceof VirtualRegister)
                        in.remove(defined);
                    inst.getUsedRegister().stream()
                            .filter(x -> x instanceof VirtualRegister)
                            .forEach(x -> in.add((VirtualRegister) x));
                    if (!inst.liveOut.equals(out) || !inst.liveIn.equals(in)) {
                        changed = true;
                        inst.liveOut.clear();
                        inst.liveIn.clear();
                        inst.liveOut.addAll(out);
                        inst.liveIn.addAll(in);
                    }
                }
            }
        }
    }

    public void run() {
        ir.functions.values().forEach(this::processFunction);
    }
}
