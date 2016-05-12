package com.abcdabcd987.compiler2016.IR;

import com.abcdabcd987.compiler2016.Symbol.FunctionType;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class Function {
    public List<VirtualRegister> argVarRegList = new ArrayList<>();
    private String name;
    private BasicBlock startBB;
    public BasicBlock exitBB;
    private FunctionType type;
    private int retSize;

    // control flow graph information
    private List<BasicBlock> reversePostOrder = null;
    private List<BasicBlock> reversePreOrder = null;
    private List<BasicBlock> DTPreOrder = null;
    private Set<BasicBlock> visited = null;
    public List<Return> retInstruction = new ArrayList<>();
    public Set<Function> calleeSet = new HashSet<>();
    public Set<Function> recursiveCalleeSet = new HashSet<>();

    // register allocation information
    public Map<VirtualRegister, StackSlot> argStackSlotMap = new HashMap<>();
    public List<StackSlot> stackSlots = new ArrayList<>();
    public Set<PhysicalRegister> usedPhysicalGeneralRegister = new HashSet<>();

    // builtin function hack FIXME!
    public String builtinFunctionHackName = null;

    public FunctionType getType() {
        return type;
    }

    protected Function() {

    }

    public Function(FunctionType type) {
        this.retSize = type.returnType.getRegisterSize();
        this.name = type.name;
        this.type = type;
        this.startBB = new BasicBlock(this, name + ".entry");
    }

    // traverse basic blocks using post order
    private void dfsPostOrder(BasicBlock node) {
        if (visited.contains(node)) return;
        visited.add(node);
        node.getSucc().forEach(this::dfsPostOrder);
        reversePostOrder.add(node);
    }

    // calc reverse post order
    public void calcReversePostOrder() {
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
        reversePreOrder.add(node);
        node.getSucc().forEach(this::dfsPreOrder);
    }
    
    // calc reverse pre order
    public void calcReversePreOrder() {
        reversePreOrder = new ArrayList<>();
        visited = new HashSet<>();
        dfsPreOrder(startBB);
        Collections.reverse(reversePreOrder);
        visited = null;
    }

    public List<BasicBlock> getReversePreOrder() {
        if (reversePreOrder == null) calcReversePreOrder();
        return reversePreOrder;
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

    /**
     * update callee set
     */
    public void updateCalleeSet() {
        calleeSet.clear();
        for (BasicBlock BB : getReversePostOrder()) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext())
                if (inst instanceof Call) {
                    calleeSet.add(((Call) inst).getFunc());
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

    @Override
    public String toString() {
        return name;
    }
}
