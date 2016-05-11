package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class IRPrinter implements IIRVisitor {
    private PrintStream out;
    private Map<BasicBlock, Boolean> BBVisited = new HashMap<>();
    private Map<BasicBlock, String> labelMap = new HashMap<>();
    private Map<StaticData, String> dataMap = new HashMap<>();
    private Map<VirtualRegister, String> regMap;
    private Map<String, Integer> counterReg;
    private Map<String, Integer> counterBB = new HashMap<>();
    private Map<String, Integer> counterData = new HashMap<>();
    private boolean definingStatic = true;

    public IRPrinter(PrintStream out) {
        this.out = out;
    }

    private String newId(String name, Map<String, Integer> counter) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        if (cnt == 1) return name;
        return name + "_" + cnt;
    }

    private String regId(VirtualRegister reg) {
        String id = regMap.get(reg);
        if (id == null) {
            if (reg.getOldName() != null) {
                id = regId(reg.getOldName()) + "." + reg.getSSAId();
            } else {
                id = newId(reg.getHintName() == null ? "t" : reg.getHintName(), counterReg);
            }
            regMap.put(reg, id);
        }
        return id;
    }

    private String labelId(BasicBlock BB) {
        String id = labelMap.get(BB);
        if (id == null) {
            id = newId(BB.getHintName(), counterBB);
            labelMap.put(BB, id);
        }
        return id;
    }

    private String dataId(StaticData data) {
        String id = dataMap.get(data);
        if (id == null) {
            id = newId(data.getHintName(), counterBB);
            dataMap.put(data, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        node.dataList.forEach(x -> x.accept(this));
        node.stringPool.values().forEach(this::visit);
        if (!node.dataList.isEmpty() || !node.stringPool.isEmpty()) out.println();
        definingStatic = false;
        node.functions.values().forEach(this::visit);
    }

    @Override
    public void visit(BasicBlock node) {
        if (BBVisited.containsKey(node)) return;
        BBVisited.put(node, true);
        out.println("%" + labelId(node) + ":");
        node.phi.values().forEach(this::visit);
        for (IRInstruction i = node.getHead(); i != null; i = i.getNext()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Function node) {
        regMap = new IdentityHashMap<>();
        counterReg = new HashMap<>();
        out.printf("func %s ", node.getName());
        List<String> argNames = node.getType().argNames;
        for (int i = 0; i < argNames.size(); ++i) {
            VirtualRegister reg = node.argVarRegList.get(i);
            out.printf("$%s ", regId(reg));
        }
        out.printf("{\n");

        List<BasicBlock> RPO = node.getReversePostOrder();
        RPO.forEach(this::visit);

        out.printf("}\n\n");
    }

    @Override
    public void visit(BinaryOperation node) {
        out.print("    ");
        String op = null;
        switch (node.getOp()) {
            case ADD: op = "add"; break;
            case SUB: op = "sub"; break;
            case MUL: op = "mul"; break;
            case DIV: op = "div"; break;
            case MOD: op = "rem"; break;
            case SHL: op = "shl"; break;
            case SHR: op = "ashr"; break;
            case AND: op = "and"; break;
            case OR: op = "or"; break;
            case XOR: op = "xor"; break;
        }

        node.getDest().accept(this);
        out.printf(" = %s ", op);
        node.getLhs().accept(this);
        out.printf(" ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(UnaryOperation node) {
        out.print("    ");
        String op = null;
        switch (node.getOp()) {
            case NEG: op = "neg"; break;
            case NOT: op = "not"; break;
        }

        node.getDest().accept(this);
        out.printf(" = %s ", op);
        node.getOperand().accept(this);
        out.println();
    }

    @Override
    public void visit(IntComparison node) {
        out.print("    ");
        String op = null;
        switch (node.getCond()) {
            case EQ: op = "seq"; break;
            case NE: op = "sne"; break;
            case GT: op = "sgt"; break;
            case GE: op = "sge"; break;
            case LT: op = "slt"; break;
            case LE: op = "sle"; break;
        }

        node.getDest().accept(this);
        out.printf(" = %s ", op);
        node.getLhs().accept(this);
        out.printf(" ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(IntImmediate node) {
        out.print(node.getValue());
    }

    @Override
    public void visit(Call node) {
        out.print("    ");
        if (node.getDest() != null) {
            node.getDest().accept(this);
            out.print(" = ");
        }
        out.printf("call %s ", node.getFunc().getName());
        node.getArgs().forEach(x -> {
            x.accept(this);
            out.print(" ");
        });
        out.println();
    }

    @Override
    public void visit(SystemCall node) {
        assert false;
    }

    @Override
    public void visit(PhiInstruction node) {
        out.print("    ");
        visit(node.dest);
        out.print(" = phi");
        for (Map.Entry<BasicBlock, IntValue> e : node.paths.entrySet()) {
            BasicBlock BB = e.getKey();
            IntValue reg = e.getValue();
            String src = null;
            if (reg == null) src = "undef";
            else if (reg instanceof VirtualRegister) src = "$"+regId((VirtualRegister) reg);
            else if (reg instanceof IntImmediate) src = String.valueOf(((IntImmediate) reg).getValue());
            else assert false;
            out.printf(" %%%s %s", labelId(BB), src);
        }
        out.println();
    }

    @Override
    public void visit(Branch node) {
        out.print("    br ");
        node.getCond().accept(this);
        out.println(" %" + labelId(node.getThen()) + " %" + labelId(node.getElse()));
        out.println();
    }

    @Override
    public void visit(Return node) {
        out.print("    ret ");
        if (node.getRet() != null) node.getRet().accept(this);
        out.println();
        out.println();
    }

    @Override
    public void visit(Jump node) {
        out.printf("    jump %%%s\n\n", labelId(node.getTarget()));
    }

    @Override
    public void visit(VirtualRegister node) {
        out.print("$" + regId(node));
    }

    @Override
    public void visit(PhysicalRegister node) {
        //TODO
    }

    @Override
    public void visit(StackSlot node) {
        //TODO
    }

    @Override
    public void visit(HeapAllocate node) {
        out.print("    ");
        node.getDest().accept(this);
        out.print(" = alloc ");
        node.getAllocSize().accept(this);
        out.println();
    }

    @Override
    public void visit(Load node) {
        out.print("    ");
        node.getDest().accept(this);
        out.printf(" = load %d ", node.getSize());
        node.getAddress().accept(this);
        out.println(" " + node.offset);
    }

    @Override
    public void visit(Store node) {
        out.printf("    store %d ", node.getSize());
        node.getAddress().accept(this);
        out.print(" ");
        node.getValue().accept(this);
        out.println(" 0");
    }

    @Override
    public void visit(Move node) {
        out.print("    ");
        node.getDest().accept(this);
        out.print(" = move ");
        node.getSource().accept(this);
        out.println();
    }

    @Override
    public void visit(StaticSpace node) {
        if (definingStatic) out.printf("space @%s %d\n", dataId(node), node.length);
        else out.print("@" + dataId(node));
    }

    @Override
    public void visit(StaticString node) {
        if (definingStatic) out.printf("asciiz @%s %s\n", dataId(node), node.value);
        else out.print("@" + dataId(node));
    }
}
