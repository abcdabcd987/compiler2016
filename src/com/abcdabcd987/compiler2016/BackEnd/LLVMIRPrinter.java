package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class LLVMIRPrinter implements IIRVisitor {
    private PrintStream out;
    private Map<String, Integer> counter = new HashMap<>();
    private Map<IntValue, String> valueMap = new IdentityHashMap<>();
    private Map<BasicBlock, String> labelMap = new IdentityHashMap<>();
    private Map<BasicBlock, Boolean> BBVisited = new IdentityHashMap<>();

    public LLVMIRPrinter(PrintStream out) {
        this.out = out;
    }

    private String newId(String name) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        if (cnt == 1) return name;
        return name + "_" + cnt;
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
        node.functions.forEach(x -> x.accept(this));
    }

    @Override
    public void visit(BasicBlock node) {
        if (BBVisited.containsKey(node)) return;
        BBVisited.put(node, true);
        out.println(labelId(node) + ":");
        for (IRNode i = node.getHead(); i != null; i = i.next)
            i.accept(this);
    }

    @Override
    public void visit(Function node) {
        out.println("define i32 @" + node.getName() + "() {");
        visit(node.getStartBB());
        out.println("}");
        out.println();
    }

    @Override
    public void visit(BinaryOperation node) {
        if (valueMap.containsKey(node)) return;
        visit(node.lhs.getIRNode());
        visit(node.rhs.getIRNode());

        String op = null;
        switch (node.op) {
            case ADD: op = "add"; break;
            case SUB: op = "sub"; break;
            case MUL: op = "mul"; break;
            case DIV: op = "sdiv"; break;
            case MOD: op = "srem"; break;
            case SHL: op = "shl"; break;
            case SHR: op = "ashr"; break;
            case AND: op = "and"; break;
            case OR: op = "or"; break;
            case XOR: op = "xor"; break;
        }

        String lhs = valueMap.get(node.lhs);
        String rhs = valueMap.get(node.rhs);
        String id = "%" + newId("t");
        valueMap.put(node, id);

        out.println("    " + id + " = " + op + " i" + node.getSize() + " " + lhs + ", " + rhs);
    }

    @Override
    public void visit(UnaryOperation node) {
        if (valueMap.containsKey(node)) return;
        visit(node.getOperand().getIRNode());

        String o = valueMap.get(node.getOperand());
        String id = "%" + newId("t");
        valueMap.put(node, id);
        switch (node.getOp()) {
            case NEG: out.println("    " + id + " = sub i" + node.getOperand().getSize() + " 0, " + o); break;
            case NOT: out.println("    " + id + " = xor i" + node.getOperand().getSize() + " -1, " + o); break;
        }
    }

    @Override
    public void visit(IntComparison node) {
        if (valueMap.containsKey(node)) return;
        visit(node.lhs.getIRNode());
        visit(node.rhs.getIRNode());

        String op = null;
        switch (node.cond) {
            case EQ: op = "eq"; break;
            case NE: op = "ne"; break;
            case GT: op = "sgt"; break;
            case GE: op = "sge"; break;
            case LT: op = "slt"; break;
            case LE: op = "sle"; break;
        }

        String lhs = valueMap.get(node.lhs);
        String rhs = valueMap.get(node.rhs);
        String id = "%" + newId("t");
        valueMap.put(node, id);

        out.println("    " + id + " = icmp " + op + " i" + node.lhs.getSize() + " " + lhs + ", " + rhs);
    }

    @Override
    public void visit(IntImmediate node) {
        if (valueMap.containsKey(node)) return;
        valueMap.put(node, String.valueOf(node.getValue()));
    }

    @Override
    public void visit(PhiNode node) {

    }

    @Override
    public void visit(Branch node) {
        visit(node.cond.getIRNode());
        String cond = valueMap.get(node.cond);
        String ifTrue = labelId(node.then);
        String ifFalse = labelId(node.otherwise);
        out.println("    " + "br i1 " + cond + ", label %" + ifTrue + ", label %" + ifFalse);
        out.println();
        visit(node.then);
        visit(node.otherwise);
    }

    @Override
    public void visit(Return node) {
        if (valueMap.containsKey(node)) return;
        visit(node.ret.getIRNode());
        String ret = valueMap.get(node.ret);
        out.println("    ret i" + node.ret.getSize() + " " + ret);
        out.println();
    }

    @Override
    public void visit(Jump node) {
        out.println("    br label %" + labelId(node.target));
        out.println();
        visit(node.target);
    }

    @Override
    public void visit(Allocate node) {
        if (valueMap.containsKey(node)) return;
        String id = "%" + newId(node.getHintName());
        valueMap.put(node, id);
        out.println("    " + id + " = alloca i" + node.getSize());
    }

    @Override
    public void visit(Load node) {
        if (valueMap.containsKey(node)) return;
        visit(node.getAddress().getIRNode());
        String addr = valueMap.get(node.getAddress());
        String id = "%" + newId(node.getHintName());
        valueMap.put(node, id);
        out.println("    " + id + " = load i" + node.getSize() + "* " + addr);
    }

    @Override
    public void visit(Store node) {
        visit(node.address.getIRNode());
        visit(node.value.getIRNode());
        String addr = valueMap.get(node.address);
        String val = valueMap.get(node.value);
        out.println("    store i" + node.value.getSize() + " " + val + ", i" + node.value.getSize() + "* " + addr);
    }

    @Override
    public void visit(IntConvert node) {
        String id = "%" + newId("iconv");
        valueMap.put(node, id);
        int src = node.getSource().getSize();
        int tar = node.getSize();
        if (src > tar) {
            out.println("    trunc i" + src + " to i" + tar);
        } else {
            out.println("    sext i" + src + " to i" + tar);
        }
    }

    @Override
    public void visit(BranchInstruction node) {
        node.accept(this);
    }

    @Override
    public void visit(IRNode node) {
        node.accept(this);
    }
}
