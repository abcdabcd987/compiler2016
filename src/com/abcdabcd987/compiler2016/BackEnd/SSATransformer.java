package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-24.
 */
public class SSATransformer {
    private static class RegisterInformation {
        /**
         * BBs that contains reg
         */
        Set<BasicBlock> containingBB = new HashSet<>();

        /**
         * SSA id stack
         */
        Stack<Integer> stack = new Stack<>();

        /**
         * SSA id allocator
         */
        int counter = 0;
    }

    /**
     * used in ssa destruction
     * target <- source
     */
    private static class ParallelCopy {
        VirtualRegister target;
        VirtualRegister source;

        public ParallelCopy(VirtualRegister target, VirtualRegister source) {
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
    private Map<BasicBlock, BlockInformation> blockInfo = new HashMap<>();
    private VirtualRegister tmpReg = new VirtualRegister("breaker");

    public SSATransformer(Function func) {
        this.func = func;
    }

    public void construct() {
        func.calcDominanceTree();
        func.calcDominanceFrontier();
        blocksRPO = func.getReversePostOrder();
        buildGlobalSet();
        insertPhiInstruction();
        renameAll();
    }

    public void destruct() {
        removePhiInstruction();
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
                varKill.addAll(func.argVarReg.values());
            }
            for (IRInstruction i = block.getHead(); i != null; i = i.getNext()) {
                Set<VirtualRegister> used = i.getUsedRegister();
                VirtualRegister dest = i.getDefinedRegister();
                if (used != null) {
                    for (VirtualRegister reg : used)
                        if (!varKill.contains(reg)) {
                            globals.add(reg);
                            if (!regInfo.containsKey(reg)) regInfo.put(reg, new RegisterInformation());
                        }
                }
                if (dest != null) {
                    varKill.add(dest);
                    RegisterInformation info = regInfo.get(dest);
                    if (info == null) {
                        info = new RegisterInformation();
                        regInfo.put(dest, info);
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
        BB.phi.values().forEach(x -> x.dest = x.dest.newSSARenamedRegister(newId(x.dest)));

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
                VirtualRegister newName = !s.empty() ? oldName.newSSARenamedRegister(s.peek()) : null;
                phi.paths.put(BB, newName);
            }
        }

        BB.DTChildren.forEach(this::rename);

        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            VirtualRegister x = i.getDefinedRegister();
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
        func.argVarReg.entrySet().forEach(e -> func.argVarReg.put(e.getKey(), e.getValue().newSSARenamedRegister(newId(e.getValue()))));

        // rename all basic blocks
        rename(func.getStartBB());

        // rename function arguments
        func.argVarReg.entrySet().forEach(e -> regInfo.get(e.getValue().getOldName()).stack.pop());
    }

    /**
     * remove phi
     * see SSA Book: Algorithm 3.5: Critical Edge Splitting Algorithm for making non-conventional SSA form conventional.
     */
    private void removePhiInstruction() {
        blocksRPO.forEach(x -> blockInfo.put(x, new BlockInformation()));

        Map<BasicBlock, List<ParallelCopy>> mapPC = new HashMap<>(); // predecessor => parallel copies
        for (BasicBlock BB : blocksRPO) {
            mapPC.clear();

            for (BasicBlock in : BB.getPred()) {
                Set<BasicBlock> inSucc = in.getSucc();
                BasicBlock toInsert = in;
                if (inSucc.size() > 1) {
                    toInsert = new BasicBlock("CEP");
                    blockInfo.put(toInsert, new BlockInformation());
                    ((BranchInstruction) BB.getLast()).insertSplitedBlock(BB, toInsert);
                }
                mapPC.put(in, blockInfo.get(toInsert).pc);
            }

            for (PhiInstruction phi : BB.phi.values()) {
                for (Map.Entry<BasicBlock, VirtualRegister> entry : phi.paths.entrySet()) {
                    BasicBlock fromBB = entry.getKey();
                    VirtualRegister srcReg = entry.getValue();
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

            pred.put(tmpReg, null);
            for (ParallelCopy i : PC) {
                location.put(i.target, null);
                pred.put(i.source, null);
            }

            for (ParallelCopy i : PC) {
                location.put(i.source, i.source);
                pred.put(i.target, i.source);
                todo.add(i.target);
            }

            for (ParallelCopy i : PC)
                if (location.get(i.target) == null)
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

            PC.clear();
        }
    }
}
