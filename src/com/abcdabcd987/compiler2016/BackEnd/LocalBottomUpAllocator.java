package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Local bottom up register allocator.
 * See Engineer a Compiler: Figure 13-1.
 * This is used to test code generation.
 *
 * Created by abcdabcd987 on 2016-04-30.
 */
public class LocalBottomUpAllocator extends RegisterAllocator {
    private static class PhysicalRegisterInfo {
        final PhysicalRegister physicalRegister;
        VirtualRegister virtualRegister;
        int next;
        boolean free;

        void clear() {
            virtualRegister = null;
            next = Integer.MAX_VALUE;
            free = true;
        }

        PhysicalRegisterInfo(PhysicalRegister physicalRegister) {
            this.physicalRegister = physicalRegister;
        }
    }

    private static class VirtualRegisterInfo {
        final StackSlot stackSlot;
        PhysicalRegisterInfo currentPhysicalRegister;
        Queue<Integer> nextId = new LinkedList<>();

        VirtualRegisterInfo(StackSlot stackSlot) {
            this.stackSlot = stackSlot;
        }
    }

    private static class InstructionInfo {
        int number;
    }

    private Function func;
    private Map<VirtualRegister, VirtualRegisterInfo> vrInfo = new HashMap<>();
    private Map<IRInstruction, InstructionInfo> instInfo = new HashMap<>();
    private Map<Register, Register> regRenameMap = new HashMap<>();
    private List<PhysicalRegisterInfo> regs = new ArrayList<>();
    private Stack<PhysicalRegisterInfo> regStack = new Stack<>();
    private BasicBlock curBB = null;
    private IRInstruction curInst = null;

    public LocalBottomUpAllocator(Collection<PhysicalRegister> regs, Function func) {
        this.func = func;
        func.usedPhysicalGeneralRegister = new HashSet<>();
        regs.forEach(x -> this.regs.add(new PhysicalRegisterInfo(x)));
    }

    private void numberInstruction() {
        instInfo.clear();
        int cnt = 0;
        for (IRInstruction inst = curBB.getHead(); inst != null; inst = inst.getNext()) {
            InstructionInfo info = instInfo.get(inst);
            info.number = cnt;
            cnt += 2;
        }
    }

    private VirtualRegisterInfo ensureVirtualRegisterInfo(VirtualRegister vr) {
        VirtualRegisterInfo info = vrInfo.get(vr);
        if (info == null) {
            info = new VirtualRegisterInfo(new StackSlot(vr.getHintName()));
            vrInfo.put(vr, info);
        }
        return info;
    }

    private void calcVirtualRegisterNextAccess() {
        for (IRInstruction inst = curBB.getHead(); inst != null; inst = inst.getNext()) {
            int number = instInfo.get(inst).number;
            Register defined = inst.getDefinedRegister();
            Collection<Register> used = inst.getUsedRegister();
            if (defined instanceof VirtualRegister)
                ensureVirtualRegisterInfo((VirtualRegister) defined).nextId.add(number);
            for (Register reg : used)
                if (reg instanceof VirtualRegister)
                    ensureVirtualRegisterInfo((VirtualRegister) reg).nextId.add(number);
        }
    }

    private void allocate(VirtualRegister vr) {
        PhysicalRegisterInfo i;
        if (regStack.isEmpty()) {
            i = null;
            for (PhysicalRegisterInfo reg : regs) if (i == null || reg.next > i.next) i = reg;
            assert i != null;
            VirtualRegisterInfo vri = vrInfo.get(i.virtualRegister);
            vri.currentPhysicalRegister = null;
            Store store = new Store(curBB, CompilerOptions.getSizeInt(), vri.stackSlot, 0, i.physicalRegister);
            curInst.prepend(store);
        } else {
            i = regStack.pop();
            func.usedPhysicalGeneralRegister.add(i.physicalRegister);
        }
        i.virtualRegister = vr;
        i.next = -1;
        i.free = false;
        vrInfo.get(vr).currentPhysicalRegister = i;
    }

    private void ensure(VirtualRegister vr) {
        VirtualRegisterInfo info = vrInfo.get(vr);
        if (info.currentPhysicalRegister == null) {
            allocate(vr);
            Load load = new Load(curBB, info.currentPhysicalRegister.physicalRegister, CompilerOptions.getSizeInt(), info.stackSlot, 0);
            curInst.prepend(load);
        }
    }

    private void free(VirtualRegisterInfo info) {
        info.currentPhysicalRegister.clear();
        regStack.push(info.currentPhysicalRegister);
        info.currentPhysicalRegister = null;
    }

    private void clearAll() {
        for (VirtualRegisterInfo info : vrInfo.values()) {
            if (info.currentPhysicalRegister != null) {
                Store store = new Store(curBB, CompilerOptions.getSizeInt(), info.stackSlot, 0, info.currentPhysicalRegister.physicalRegister);
                curBB.getLast().prepend(store);
                free(info);
            }
            info.nextId.clear();
        }
    }

    private void processBB() {
        for (curInst = curBB.getHead(); curInst != null; curInst = curInst.getNext()) {
            int number = instInfo.get(curInst).number;

            if (!(curInst instanceof Call)) {
                Collection<Register> used = curInst.getUsedRegister();
                used.stream().filter(x -> x instanceof VirtualRegister).forEach(x -> ensure((VirtualRegister) x));
                regRenameMap.clear();
                for (Register reg : used)
                    if (reg instanceof VirtualRegister) {
                        VirtualRegisterInfo info = vrInfo.get(reg);
                        Queue<Integer> nextQueue = info.nextId;
                        while (!nextQueue.isEmpty() && nextQueue.peek() <= number) nextQueue.remove();
                        regRenameMap.put(reg, info.currentPhysicalRegister.physicalRegister);
                        if (nextQueue.isEmpty())
                            free(info);
                        else
                            info.currentPhysicalRegister.next = nextQueue.peek() - number;
                    }
                curInst.setUsedRegister(regRenameMap);
            } else {
                // call instruction don't deserve register allocation
                List<IntValue> args = ((Call) curInst).getArgs();
                for (int i = 0; i < args.size(); ++i)
                    if (args.get(i) instanceof VirtualRegister)
                        args.set(i, vrInfo.get(args.get(i)).stackSlot);
            }

            Register defined = curInst.getDefinedRegister();
            if (defined instanceof VirtualRegister) {
                VirtualRegisterInfo info = vrInfo.get(defined);
                Queue<Integer> nextQueue = info.nextId;
                while (!nextQueue.isEmpty() && nextQueue.peek() <= number) nextQueue.remove();
                allocate((VirtualRegister) defined);
                curInst.setDefinedRegister(info.currentPhysicalRegister.physicalRegister);
                info.currentPhysicalRegister.next = nextQueue.isEmpty() ? Integer.MAX_VALUE : nextQueue.peek() - number;
            }
        }
    }

    public void run() {
        for (BasicBlock BB : func.getReversePostOrder()) {
            curBB = BB;
            numberInstruction();
            calcVirtualRegisterNextAccess();
            processBB();
            clearAll();
        }
    }
}
