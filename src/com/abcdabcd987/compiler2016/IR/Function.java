package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.Symbol.FunctionType;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Function {
    private Map<String, VirtualRegister> varReg = new HashMap<>();
    public Map<String, VirtualRegister> argVarReg = new HashMap<>();
    private String name;
    private BasicBlock startBB;
    private FunctionType type;
    private int retSize;

    // control flow graph information
    private List<BasicBlock> reversePostOrder = null;
    private List<BasicBlock> preOrder = null;
    private List<BasicBlock> DTPreOrder = null;
    private Set<BasicBlock> visited = null;

    public FunctionType getType() {
        return type;
    }

    public Function(FunctionType type) {
        this.retSize = type.returnType.getRegisterSize();
        this.name = type.name;
        this.type = type;
        this.startBB = new BasicBlock(name + ".entry");
    }

    public void defineVarReg(String name, VirtualRegister reg, boolean isArg) {
        if (isArg) argVarReg.put(name, reg);
        else varReg.put(name, reg);
    }

    public VirtualRegister getVarReg(String name) {
        VirtualRegister reg = varReg.get(name);
        if (reg == null) reg = argVarReg.get(name);
        return reg;
    }

    // traverse basic blocks using post order
    private void dfsPostOrder(BasicBlock node) {
        if (visited.contains(node)) return;
        visited.add(node);
        node.getSucc().forEach(this::dfsPostOrder);
        reversePostOrder.add(node);
    }

    // calc reverse post order
    private void calcReversePostOrder() {
        reversePostOrder = new ArrayList<>();
        visited = new HashSet<>();
        dfsPostOrder(startBB);
        for (int i = 0; i < reversePostOrder.size(); ++i)
            reversePostOrder.get(i).postOrderNumber = i;
        Collections.reverse(reversePostOrder);
        visited = null;
    }

    public List<BasicBlock> getReversePostOrder() {
        if (reversePostOrder == null) calcReversePostOrder();
        return reversePostOrder;
    }

    // traverse basic blocks using pre order
    private void dfsPreOrder(BasicBlock node) {
        if (visited.contains(node)) return;
        visited.add(node);
        preOrder.add(node);
        node.getSucc().forEach(this::dfsPreOrder);
    }
    
    // calc pre order
    private void calcPreOrder() {
        preOrder = new ArrayList<>();
        visited = new HashSet<>();
        dfsPreOrder(startBB);
        visited = null;
    }

    public List<BasicBlock> getPreOrder() {
        if (preOrder == null) calcPreOrder();
        return preOrder;
    }

    // traverse dominance tree using pre order
    private void dfsDTPreOrder(BasicBlock node) {
        if (visited.contains(node)) return;
        visited.add(node);
        DTPreOrder.add(node);
        node.DTChildren.forEach(this::dfsDTPreOrder);
    }

    // calc pre order
    private void calcDTPreOrder() {
        DTPreOrder = new ArrayList<>();
        visited = new HashSet<>();
        dfsDTPreOrder(startBB);
        visited = null;
    }

    public List<BasicBlock> getDTPreOrder() {
        if (DTPreOrder == null) calcDTPreOrder();
        return DTPreOrder;
    }

    /**
     * calc dominance tree
     * see K.D. Cooper, T. J. Harvey and K.Kennedy. A simple, fast dominance algorithm.
     */
    public void calcDominanceTree() {
        List<BasicBlock> RPO = getReversePostOrder();
        List<BasicBlock> blocks = RPO.subList(1, RPO.size()); // startBB not included
        blocks.forEach(x -> x.IDom = null);
        startBB.IDom = startBB;

        // calc IDom
        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock block : blocks) {
                BasicBlock newIDom = null;
                for (BasicBlock pred : block.getPred())
                    if (pred.IDom != null) {
                        newIDom = pred;
                        break;
                    }
                assert newIDom != null;
                for (BasicBlock pred : block.getPred())
                    if (pred != newIDom && pred.IDom != null)
                        newIDom = intersect(pred, newIDom);
                if (block.IDom != newIDom) {
                    block.IDom = newIDom;
                    changed = true;
                }
            }
        }

        // calc successors
        RPO.forEach(x -> x.DTChildren = new HashSet<>());
        blocks.forEach(x -> x.IDom.DTChildren.add(x));
    }

    private BasicBlock intersect(BasicBlock b1, BasicBlock b2) {
        BasicBlock finger1 = b1;
        BasicBlock finger2 = b2;
        while (finger1 != finger2) {
            while (finger1.postOrderNumber < finger2.postOrderNumber) finger1 = finger1.IDom;
            while (finger1.postOrderNumber > finger2.postOrderNumber) finger2 = finger2.IDom;
        }
        return finger1;
    }

    /**
     * calc dominance frontier
     * see K.D. Cooper, T. J. Harvey and K.Kennedy. A simple, fast dominance algorithm.
     */
    public void calcDominanceFrontier() {
        List<BasicBlock> blocks = getReversePostOrder();
        blocks.forEach(x -> x.DF = new HashSet<>());
        for (BasicBlock block : blocks) {
            if (block.getPred().size() < 2) continue;
            for (BasicBlock pred : block.getPred()) {
                BasicBlock runner = pred;
                while (runner != block.IDom) {
                    runner.DF.add(block);
                    runner = runner.IDom;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public BasicBlock getStartBB() {
        return startBB;
    }

    public int getRetSize() {
        return retSize;
    }

    public void setStartBB(BasicBlock startBB) {
        this.startBB = startBB;
    }

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
