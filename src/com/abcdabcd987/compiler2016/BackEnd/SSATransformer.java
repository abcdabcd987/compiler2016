package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-24.
 */
public class SSATransformer {
    private Function func;
    private List<BasicBlock> blocksRPO;
    private Set<VirtualRegister> globals = new HashSet<>();
    private Map<VirtualRegister, Set<BasicBlock>> container = new HashMap<>();
    private Map<VirtualRegister, Integer> counter = new HashMap<>();
    private Map<VirtualRegister, Stack<Integer>> stack = new HashMap<>();

    public SSATransformer(Function func) {
        this.func = func;
    }

    public void executeConstruct() {
        func.calcDominanceTree();
        func.calcDominanceFrontier();
        blocksRPO = func.getReversePostOrder();
        buildGlobalSet();
        insertPhiInstruction();
        renameAll();
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
                    used.stream()
                            .filter(x -> !varKill.contains(x))
                            .forEach(globals::add);
                }
                if (dest != null) {
                    varKill.add(dest);
                    Set<BasicBlock> s = container.get(dest);
                    if (s == null) {
                        s = new HashSet<>();
                        container.put(dest, s);
                    }
                    s.add(block);
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
            if (!container.containsKey(x)) container.put(x, new HashSet<>());
            workList.clear();
            visited.clear();
            workList.addAll(container.get(x));
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
        int idx = counter.getOrDefault(reg, 0) + 1;
        counter.put(reg, idx);
        Stack<Integer> s = stack.get(reg);
        if (s == null) {
            s = new Stack<>();
            stack.put(reg, s);
        }
        s.push(idx);
        return idx;
    }

    private void rename(BasicBlock BB) {
        BB.phi.values().forEach(x -> x.dest = x.dest.newSSARenamedRegister(newId(x.dest)));

        java.util.function.Function<VirtualRegister, Integer> idSuplierForUsed = x -> stack.get(x).peek();
        java.util.function.Function<VirtualRegister, Integer> idSuplierForDefined = this::newId;
        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            i.renameUsedRegister(idSuplierForUsed);
            i.renameDefinedRegister(idSuplierForDefined);
        }

        for (BasicBlock succ : BB.getSucc()) {
            for (Map.Entry<VirtualRegister, PhiInstruction> entry : succ.phi.entrySet()) {
                PhiInstruction phi = entry.getValue();
                VirtualRegister oldName = entry.getKey();
                Stack<Integer> s = stack.get(oldName);
                VirtualRegister newName = s != null ? oldName.newSSARenamedRegister(s.peek()) : null;
                phi.paths.put(BB, newName);
            }
        }

        BB.DTChildren.forEach(this::rename);

        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            VirtualRegister x = i.getDefinedRegister();
            if (x != null) stack.get(x.getOldName()).pop();
        }

        BB.phi.values().forEach(x -> stack.get(x.dest.getOldName()).pop());
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
        func.argVarReg.entrySet().forEach(e -> stack.get(e.getValue().getOldName()).pop());
    }
}
