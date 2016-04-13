package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class LLVMIRPrinter implements IIRVisitor {
    private Map<String, Integer> counter = new HashMap<>();
    private Map<WordValue, String> valueMap = new IdentityHashMap<>();
    private Map<BasicBlock, String> labelMap = new IdentityHashMap<>();
    private Map<BasicBlock, Boolean> BBVisited = new IdentityHashMap<>();

    private String newId(String name) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        if (cnt == 1) return name;
        return name + "_" + cnt;
    }

    private String labelId(BasicBlock BB) {
        String id = labelMap.get(BB);
        if (id == null) {
            id = newId(BB.hintName);
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
        System.out.println(labelId(node) + ":");
        for (IRNode i = node.getHead(); i != null; i = i.next)
            i.accept(this);
    }

    @Override
    public void visit(Function node) {
        System.out.println("define i32 @" + node.getName() + "() {");
        visit(node.getStartBB());
        System.out.println("}");
        System.out.println();
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

        System.out.println("    " + id + " = " + op + " i32 " + lhs + ", " + rhs);
    }

    @Override
    public void visit(UnaryOperation node) {
        if (valueMap.containsKey(node)) return;
        visit(node.operand.getIRNode());

        String o = valueMap.get(node.operand);
        String id = "%" + newId("t");
        valueMap.put(node, id);
        switch (node.op) {
            case NEG: System.out.println("    " + id + " = sub i32 0, " + o); break;
            case NOT: System.out.println("    " + id + " = xor i32 -1, " + o); break;
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

        System.out.println("    " + id + " = icmp " + op + " i32 " + lhs + ", " + rhs);
    }

    @Override
    public void visit(IntImmediate node) {
        if (valueMap.containsKey(node)) return;
        valueMap.put(node, String.valueOf(node.value));
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
        if (node.cond instanceof IntComparison) {
            System.out.println("    " + "br i1 " + cond + ", label %" + ifTrue + ", label %" + ifFalse);
        } else {
            // convert i32 to i1
            String i1 = "%" + newId("i32toi1");
            System.out.println("    " + i1 + " = trunc i32 " + cond + " to i1");
            System.out.println("    " + "br i1 " + i1 + ", label %" + ifTrue + ", label %" + ifFalse);
        }
        System.out.println();
        visit(node.then);
        visit(node.otherwise);
    }

    @Override
    public void visit(Return node) {
        if (valueMap.containsKey(node)) return;
        visit(node.ret.getIRNode());
        String ret = valueMap.get(node.ret);
        System.out.println("    ret i32 " + ret);
        System.out.println();
    }

    @Override
    public void visit(Jump node) {
        System.out.println("    br label %" + labelId(node.target));
        System.out.println();
        visit(node.target);
    }

    @Override
    public void visit(Alloca node) {
        if (valueMap.containsKey(node)) return;
        String id = "%" + newId(node.hintName);
        valueMap.put(node, id);
        System.out.println("    " + id + " = alloca i32");
    }

    @Override
    public void visit(Load node) {
        if (valueMap.containsKey(node)) return;
        visit(node.address.getIRNode());
        String addr = valueMap.get(node.address);
        String id = "%" + newId(node.hintName);
        valueMap.put(node, id);
        System.out.println("    " + id + " = load i32* " + addr);
    }

    @Override
    public void visit(Store node) {
        visit(node.address.getIRNode());
        visit(node.value.getIRNode());
        String addr = valueMap.get(node.address);
        String val = valueMap.get(node.value);
        if (node.value instanceof IntComparison) {
            // convert i1 to i32
            String i32 = "%" + newId("i1toi32");
            System.out.println("    " + i32 + " = sext i1 " + val + " to i32");
            System.out.println("    store i32 " + i32 + ", i32* " + addr);
        } else {
            System.out.println("    store i32 " + val + ", i32* " + addr);
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
