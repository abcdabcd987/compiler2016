package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-24.
 */
public class SSATransformer {
    private Function func;
    private List<BasicBlock> blocks;
    private Set<VirtualRegister> globals = Collections.newSetFromMap(new IdentityHashMap<>());
    private Set<VirtualRegister> varKill = Collections.newSetFromMap(new IdentityHashMap<>());
    private Map<VirtualRegister, Set<BasicBlock>> container = new IdentityHashMap<>();
    private Map<VirtualRegister, Integer> counter = new IdentityHashMap<>();
    private Map<VirtualRegister, Stack<Integer>> stack = new IdentityHashMap<>();

    public SSATransformer(Function func) {
        this.func = func;
        blocks = func.getReversePostOrder();
    }

    public void executeConstruct() {
        func.calcDominanceTree();
        func.calcDominanceFrontier();
        buildGlobalSet();
        insertPhiInstruction();
        renameAll();
    }

    /**
     * build the global name set
     * see Engineer a Compiler: Figure 9-9
     */
    private void buildGlobalSet() {
        for (BasicBlock block : blocks) {
            varKill.clear();
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
                        s = Collections.newSetFromMap(new IdentityHashMap<>());
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
        for (VirtualRegister x : globals) {
            Set<BasicBlock> workList = Collections.newSetFromMap(new IdentityHashMap<>());
            workList.addAll(container.get(x));
            for (BasicBlock b : workList)
                for (BasicBlock d : b.DF) {
                    PhiInstruction phi = d.phi.get(x);
                    if (phi == null) {
                        phi = new PhiInstruction(d, x);
                        d.phi.put(null, phi);
                    }
                    if (!phi.paths.containsKey(b)) {
                        phi.add(x, b);
                    }
                }
        }
    }

    private int newId(VirtualRegister reg) {
        int idx = counter.getOrDefault(reg, 0) + 1;
        counter.put(reg, idx);
        stack.get(reg).push(idx);
        return idx;
    }

    private void rename(BasicBlock BB) {
        BB.phi.values().forEach(x -> x.dest = x.dest.newSSARenamedRegister(newId(x.dest)));

        java.util.function.Function<VirtualRegister, Integer> idSuplierForUsed = x -> stack.get(x).peek();
        java.util.function.Function<VirtualRegister, Integer> idSuplierForDefined = this::newId;
        Map<VirtualRegister, VirtualRegister> latestDefinedName = new IdentityHashMap<>();
        for (IRInstruction i = BB.getHead(); i != null; i = i.getNext()) {
            i.renameUsedRegister(idSuplierForUsed);
            i.renameDefinedRegister(idSuplierForDefined);
            VirtualRegister defined = i.getDefinedRegister();
            if (defined != null) {
                latestDefinedName.put(defined.getOldName(), defined);
            }
        }

        for (BasicBlock succ : BB.getSucc()) {
            for (Map.Entry<VirtualRegister, PhiInstruction> entry : succ.phi.entrySet()) {
                VirtualRegister oldName = entry.getKey();
                PhiInstruction phi = entry.getValue();
                if (phi.paths.containsKey(BB))
                    phi.paths.put(BB, latestDefinedName.get(oldName));
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
        globals.forEach(x -> stack.put(x, new Stack<>()));
        rename(func.getStartBB());
    }
}
