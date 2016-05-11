package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-24.
 */
public class SSATransformer {
    private static class RegisterInformation {
        /** BBs that contains reg */
        Set<BasicBlock> containingBB = new HashSet<>();

        /** SSA id stack */
        Stack<Integer> stack = new Stack<>();

        /** SSA id allocator */
        int counter = 0;
    }

    private static class IntValueInformation {
        /** Instruction that uses this int value */
        Set<IRInstruction> usedBy = new HashSet<>();

        /** Instruction that uses this int value */
        Set<PhiInstruction> usedByPhiSet = new HashSet<>();

        /** Instruction that defines this int value */
        IRInstruction definedBy = null;
    }

    /**
     * used in ssa destruction
     * target <- source
     */
    private static class ParallelCopy {
        VirtualRegister target;
        IntValue source;

        ParallelCopy(VirtualRegister target, IntValue source) {
            this.target = target;
            this.source = source;
        }
    }

    private static class BlockInformation {
        /**
         * parallel copy instructions.
         */
        List<ParallelCopy> pc = new ArrayList<>();
    }

    private Function func;
    private List<BasicBlock> blocksRPO;
    private Set<VirtualRegister> globals = new HashSet<>();
    private Map<VirtualRegister, RegisterInformation> regInfo = new HashMap<>();
    private Map<IntValue, IntValueInformation> ivInfo = new HashMap<>();
    private Map<BasicBlock, BlockInformation> blockInfo = new HashMap<>();

    public SSATransformer(Function func) {
        this.func = func;
    }

    public void construct() {
        func.calcDominanceTree();
        func.calcDominanceFrontier();
        func.calcReversePostOrder();
        blocksRPO = func.getReversePostOrder();
        buildGlobalSet();
        insertPhiInstruction();
        renameAll();
    }

    public void destroy() {
        removePhiInstruction();
        func.calcReversePostOrder();
        blocksRPO = func.getReversePostOrder();
        sequentializeParallelCopy();
    }

    /**
     * build the global name set
     * see Engineer a Compiler: Figure 9-9
     */
    private void buildGlobalSet() {
        Set<VirtualRegister> varKill = new HashSet<>();
        for (BasicBlock block : blocksRPO) {
            varKill.clear();
            if (block == func.getStartBB()) {
                for (VirtualRegister arg : func.argVarRegList) {
                    varKill.add(arg);
                    if (!regInfo.containsKey(arg)) regInfo.put(arg, new RegisterInformation());
                }
            }
            for (IRInstruction i = block.getHead(); i != null; i = i.getNext()) {
                Collection<Register> used = i.getUsedRegister();
                Register dest = i.getDefinedRegister();
                for (Register reg : used)
                    if (reg instanceof VirtualRegister)
                        if (!varKill.contains(reg)) {
                            globals.add((VirtualRegister) reg);
                            if (!regInfo.containsKey(reg)) regInfo.put((VirtualRegister) reg, new RegisterInformation());
                        }
                if (dest instanceof VirtualRegister) {
                    varKill.add((VirtualRegister) dest);
                    RegisterInformation info = regInfo.get(dest);
                    if (info == null) {
                        info = new RegisterInformation();
                        regInfo.put((VirtualRegister) dest, info);
                    }
                    info.containingBB.add(block);
                }
            }
        }
    }

    /**
     * insert phi instruction
     * see Engineer a Compiler: Figure 9-9
     */
    private void insertPhiInstruction() {
        Queue<BasicBlock> workList = new LinkedList<>();
        Set<BasicBlock> visited = new HashSet<>();
        for (VirtualRegister x : globals) {
            workList.clear();
            visited.clear();
            workList.addAll(regInfo.get(x).containingBB);
            while (!workList.isEmpty()) {
                BasicBlock b = workList.remove();
                if (visited.contains(b)) continue;
                visited.add(b);
                for (BasicBlock d : b.DF)
                    if (!d.phi.containsKey(x)) {
                        d.phi.put(x, new PhiInstruction(d, x));
                        workList.add(d);
                    }
            }
        }
    }

    private int newId(VirtualRegister reg) {
        RegisterInformation info = regInfo.get(reg);
        int idx = info.counter++;
        info.stack.push(idx);
        return idx;
    }

    private void rename(BasicBlock BB) {
        BB.phi.values().forEach(x -> x.dest = x.dest.getSSARenamedRegister(newId(x.dest)));

        java.util.function.Function<VirtualRegister, Integer> idSuplierForUsed = x -> regInfo.get(x).stack.peek();
        java.util.function.Function<VirtualRegister, Integer> idSuplierForDefined = this::newId;
        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            i.renameUsedRegister(idSuplierForUsed);
            i.renameDefinedRegister(idSuplierForDefined);
        }

        for (BasicBlock succ : BB.getSucc()) {
            for (Map.Entry<VirtualRegister, PhiInstruction> entry : succ.phi.entrySet()) {
                PhiInstruction phi = entry.getValue();
                VirtualRegister oldName = entry.getKey();
                Stack<Integer> s = regInfo.get(oldName).stack;
                VirtualRegister newName = !s.empty() ? oldName.getSSARenamedRegister(s.peek()) : null;
                phi.paths.put(BB, newName);
            }
        }

        BB.DTChildren.forEach(this::rename);

        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            VirtualRegister x = (VirtualRegister)i.getDefinedRegister();
            if (x != null) regInfo.get(x.getOldName()).stack.pop();
        }

        BB.phi.values().forEach(x -> regInfo.get(x.dest.getOldName()).stack.pop());
    }

    /**
     * rename phase
     * see Engineer a Compiler: Figure 9-12
     */
    private void renameAll() {
        // rename function arguments
        for (int i = 0; i < func.argVarRegList.size(); ++i) {
            VirtualRegister old = func.argVarRegList.get(i);
            VirtualRegister now = old.getSSARenamedRegister(newId(old));
            func.argVarRegList.set(i, now);
        }

        // rename all basic blocks
        rename(func.getStartBB());

        // rename function arguments
        func.argVarRegList.forEach(x -> regInfo.get(x.getOldName()).stack.pop());
    }

    /**
     * remove phi
     * see SSA Book: Algorithm 3.5: Critical Edge Splitting Algorithm for making non-conventional SSA form conventional.
     */
    private void removePhiInstruction() {
        blocksRPO.forEach(x -> blockInfo.put(x, new BlockInformation()));

        Map<BasicBlock, List<ParallelCopy>> mapPC = new HashMap<>(); // predecessor => parallel copies
        Set<BasicBlock> predSet = new HashSet<>();
        for (BasicBlock BB : blocksRPO) {
            mapPC.clear();

            predSet.clear();
            predSet.addAll(BB.getPred());
            for (BasicBlock in : predSet) {
                BasicBlock toInsert = in;
                if (in.getSucc().size() > 1) {
                    assert in.getLast() instanceof Branch;
                    toInsert = ((Branch) in.getLast()).insertSplitBlock(BB);
                    blockInfo.put(toInsert, new BlockInformation());
                }
                mapPC.put(in, blockInfo.get(toInsert).pc);
            }

            for (PhiInstruction phi : BB.phi.values()) {
                for (Map.Entry<BasicBlock, IntValue> entry : phi.paths.entrySet()) {
                    BasicBlock fromBB = entry.getKey();
                    IntValue srcReg = entry.getValue();
                    VirtualRegister tarReg = phi.dest;
                    if (tarReg != srcReg && srcReg != null) // parallel self copy and copy of undef value is meaningless
                        mapPC.get(fromBB).add(new ParallelCopy(tarReg, srcReg));
                }
            }

            BB.phi.clear();
        }
    }

    /**
     * <p>
     * replacement of parallel copies with sequences of sequential copy operations.
     * see SSA Book: Algorithm 22.6: Parallel copy sequentialization algorithm.
     * </p>
     *
     * <p>
     * also see: Benoit Boissinot, Alain Darte, Fabrice Rastello, Benoît Dupont de Dinechin, Christophe Guillon.
     * <em>Revisiting Out-of-SSA Translation for Correctness, Code Quality, and Efficiency.</em>
     * [Research Report] 2008, pp.14. &lt;inria-00349925v1&gt;
     * </p>
     */
    private void sequentializeParallelCopy() {
        Queue<VirtualRegister> ready = new LinkedList<>();
        Queue<VirtualRegister> todo = new LinkedList<>();
        Map<VirtualRegister, VirtualRegister> pred = new HashMap<>();
        Map<VirtualRegister, VirtualRegister> location = new HashMap<>();
        for (BasicBlock BB : blocksRPO) {
            ready.clear();
            todo.clear();
            pred.clear();
            location.clear();
            List<ParallelCopy> PC = blockInfo.get(BB).pc;
            VirtualRegister tmpReg = new VirtualRegister("breaker");

            pred.put(tmpReg, null);
            for (ParallelCopy i : PC) {
                if (i.source instanceof VirtualRegister) {
                    location.put(i.target, null);
                    pred.put((VirtualRegister) i.source, null);
                }
            }

            for (ParallelCopy i : PC) {
                if (i.source instanceof VirtualRegister) {
                    VirtualRegister src = (VirtualRegister) i.source;
                    location.put(src, src);
                    pred.put(i.target, src);
                    todo.add(i.target);
                }
            }

            for (ParallelCopy i : PC)
                if (i.source instanceof VirtualRegister && location.get(i.target) == null)
                    ready.add(i.target);

            while (!todo.isEmpty()) {
                while (!ready.isEmpty()) {
                    VirtualRegister b = ready.remove();
                    VirtualRegister a = pred.get(b);
                    VirtualRegister c = location.get(a);
                    BB.appendBeforeJump(new Move(BB, b, c));
                    location.put(a, b);
                    if (a == c && pred.get(a) != null) ready.add(a);
                }

                VirtualRegister b = todo.remove();
                if (b == location.get(pred.get(b))) {
                    BB.appendBeforeJump(new Move(BB, tmpReg, b));
                    location.put(b, tmpReg);
                    ready.add(b);
                }
            }

            for (ParallelCopy i : PC)
                if (i.source instanceof IntImmediate)
                    BB.appendBeforeJump(new Move(BB, i.target, i.source));

            PC.clear();
        }
    }

    private IntValueInformation getIntValueInfo(IntValue reg) {
        IntValueInformation info = ivInfo.get(reg);
        if (info == null) {
            info = new IntValueInformation();
            ivInfo.put(reg, info);
        }
        return info;
    }

    private void buildDefUse() {
        for (BasicBlock BB : func.getReversePostOrder()) {
            for (PhiInstruction phi : BB.phi.values()) {
                for (IntValue used : phi.paths.values()) {
                    getIntValueInfo(used).usedBy.add(phi);
                    getIntValueInfo(used).usedByPhiSet.add(phi);
                }
                getIntValueInfo(phi.dest).definedBy = phi;
            }
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                for (IntValue used : inst.getUsedIntValue())
                    getIntValueInfo(used).usedBy.add(inst);
                if (inst.getDefinedRegister() != null)
                    getIntValueInfo(inst.getDefinedRegister()).definedBy = inst;
            }
        }
    }

    /**
     * Return true if inst has side effect other than assigning to its destination.
     * Should only be used by {@link #naivelyEliminateDeadCode()}
     */
    private boolean hasSideEffect(IRInstruction inst) {
        return inst instanceof HeapAllocate || inst instanceof Call;
    }

    /**
     * Naive Dead Code Elimination
     * See the Tiger Book: Algorithm 19.12
     */
    public void naivelyEliminateDeadCode() {
        buildDefUse();
        Set<VirtualRegister> workList = new HashSet<>();
        for (IntValue intValue : ivInfo.keySet()) {
            if (intValue instanceof VirtualRegister)
                workList.add((VirtualRegister) intValue);
        }
        while (!workList.isEmpty()) {
            Iterator<VirtualRegister> iter = workList.iterator();
            VirtualRegister reg = iter.next();
            iter.remove();
            IntValueInformation info = ivInfo.get(reg);
            Set<IRInstruction> useList = info.usedBy;
            if (useList.isEmpty()) {
                IRInstruction def = info.definedBy;
                if (def != null && !hasSideEffect(def)) {
                    def.remove();
                    for (PhiInstruction phi : info.usedByPhiSet)
                        for (Map.Entry<BasicBlock, IntValue> e : phi.paths.entrySet())
                            if (e.getValue() == reg)
                                phi.paths.put(e.getKey(), null);
                    for (Register used : def.getUsedRegister())
                        if (used instanceof VirtualRegister) {
                            VirtualRegister x = (VirtualRegister) used;
                            ivInfo.get(x).usedBy.remove(def);
                            if (def instanceof PhiInstruction) ivInfo.get(x).usedByPhiSet.remove(def);
                            workList.add(x);
                        }
                }
            }
        }
    }

    private void replaceIntValueUse(Set<IRInstruction> workList, IRInstruction inst, IntValue oldValue, IntValue newValue) {
        Set<IRInstruction> oldUser = ivInfo.get(oldValue).usedBy;
        Set<IRInstruction> newUser = ivInfo.get(newValue).usedBy;
        for (IRInstruction user : oldUser)
            if (user != inst) {
                user.replaceIntValueUse(oldValue, newValue);
                workList.add(user);
                newUser.add(user);
            }
        newUser.remove(inst);
        oldUser.clear();
        inst.remove();
    }

    /**
     * Simple Constant Propagation
     * See the Tiger Book: Chapter 19.3.2
     */
    public void simpleConstantPropagate() {
        ivInfo.clear();
        buildDefUse();
        Set<IRInstruction> workList = new HashSet<>();
        for (BasicBlock BB : func.getReversePostOrder()) {
            BB.phi.values().forEach(workList::add);
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext())
                workList.add(inst);
        }
        while (!workList.isEmpty()) {
            Iterator<IRInstruction> iter = workList.iterator();
            IRInstruction inst = iter.next();
            iter.remove();
            if (inst instanceof Move) {
                Move move = (Move) inst;
                if (move.getSource() instanceof IntImmediate || move.getSource() instanceof VirtualRegister)
                    replaceIntValueUse(workList, inst, move.getDest(), move.getSource());
            } else if (inst instanceof PhiInstruction) {
                PhiInstruction phi = (PhiInstruction) inst;
                IntValue val = phi.paths.values().iterator().next();
                if (phi.paths.size() == 1) {
                    replaceIntValueUse(workList, inst, phi.dest, val);
                } else if (val instanceof IntImmediate) {
                    int c = ((IntImmediate) val).getValue();
                    boolean same = phi.paths.values().stream().allMatch(x -> x instanceof IntImmediate && ((IntImmediate) x).getValue() == c);
                    if (same) {
                        replaceIntValueUse(workList, inst, phi.dest, val);
                    }
                }
            }
        }
    }
}
