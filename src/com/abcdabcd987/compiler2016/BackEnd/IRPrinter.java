package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class IRPrinter implements IIRVisitor {
    private PrintStream out;
    private Map<BasicBlock, Boolean> BBVisited = new IdentityHashMap<>();
    private Map<BasicBlock, String> labelMap = new IdentityHashMap<>();
    private Map<VirtualRegister, String> regMap = new IdentityHashMap<>();
    private Map<String, Integer> counter = new HashMap<>();

    public IRPrinter(PrintStream out) {
        this.out = out;
    }

    private String newId(String name) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        return name + "_" + cnt;
    }

    private String regId(VirtualRegister reg) {
        String id = regMap.get(reg);
        if (id == null) {
            id = newId(reg.getHintName() == null ? "t" : reg.getHintName());
            regMap.put(reg, id);
        }
        return id;
    }

    private String labelId(BasicBlock BB) {
        String id = labelMap.get(BB);
        if (id == null) {
            id = newId(BB.getHintName());
            labelMap.put(BB, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        node.functions.values().forEach(this::visit);
    }

    @Override
    public void visit(BasicBlock node) {
        if (BBVisited.containsKey(node)) return;
        BBVisited.put(node, true);
        out.println("%" + labelId(node) + ":");
        for (IRInstruction i = node.getHead(); i != null; i = i.getNext()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Function node) {
        out.printf("func %s ", node.getName());
        for (int i = 0; i < node.getType().argTypes.size(); ++i) {
            out.printf("$op%d ", i);
        }
        out.printf("{\n");

        visit(node.getStartBB());

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

        visit(node.getDest());
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
            case NEG: op = "neg";
            case NOT: op = "not";
        }

        visit(node.getDest());
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

        visit(node.getDest());
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
            visit(node.getDest());
            out.print(" = ");
        }
        out.printf("call %s ", node.getFunc().getName());
        node.getArgs().forEach(x -> {
            x.accept(this);
            out.print(" ");
        });
    }

    @Override
    public void visit(PhiInstruction node) {

    }

    @Override
    public void visit(Branch node) {
        out.print("    br ");
        node.getCond().accept(this);
        out.println(" %" + labelId(node.getThen()) + " %" + labelId(node.getElse()));
        out.println();

        visit(node.getThen());
        visit(node.getElse());
    }

    @Override
    public void visit(Return node) {
        out.print("    ret ");
        node.getRet().accept(this);
        out.println();
        out.println();
    }

    @Override
    public void visit(Jump node) {
        out.printf("    jump %%%s\n\n", labelId(node.getTarget()));

        visit(node.getTarget());
    }

    @Override
    public void visit(VirtualRegister node) {
        out.print("$" + regId(node));
    }

    @Override
    public void visit(HeapAllocate node) {
        out.print("    ");
        visit(node.getDest());
        out.print(" = alloc ");
        node.getAllocSize().accept(this);
        out.println();
    }

    @Override
    public void visit(Load node) {
        out.print("    ");
        visit(node.getDest());
        out.printf(" = load %d ", node.getSize());
        node.getAddress().accept(this);
        out.println();
    }

    @Override
    public void visit(Store node) {
        out.printf("    store %d ", node.getSize());
        node.getAddress().accept(this);
        out.print(" ");
        node.getValue().accept(this);
        out.println();
    }

    @Override
    public void visit(Move node) {
        out.print("    ");
        visit(node.getDest());
        out.print(" = move ");
        node.getSource().accept(this);
        out.println();
    }
}
