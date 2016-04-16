package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
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
    private Map<String, String> llvmType = new HashMap<>();
    private String ptrLLVMSize = "i" + (CompilerOptions.getSizePointer()*8);

    public LLVMIRPrinter(PrintStream out) {
        this.out = out;
    }

    private String getLLVMType(String name) {
        return llvmType.get(name.replace("%", ""));
    }

    private String newId(String name, String llvmtype) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        if (cnt != 1) name = name + "_" + cnt;
        llvmType.put(name, llvmtype);
        return name;
    }

    private String labelId(BasicBlock BB) {
        String id = labelMap.get(BB);
        if (id == null) {
            id = newId(BB.getHintName(), null);
            labelMap.put(BB, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        node.functions.values().forEach(x -> x.accept(this));
        out.println("declare i8* @malloc(i64)");
        out.println();
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

        String lhsLLVMType = getLLVMType(lhs);
        if (lhsLLVMType != null && lhsLLVMType.endsWith("*")) {
            String ptrtoint = "%" + newId("ptrtoint", ptrLLVMSize);
            out.printf("    %s = ptrtoint %s %s to %s\n", ptrtoint, lhsLLVMType, lhs, ptrLLVMSize);
            lhs = ptrtoint;
        }

        String rhsLLVMType = getLLVMType(rhs);
        if (rhsLLVMType != null && rhsLLVMType.endsWith("*")) {
            String ptrtoint = "%" + newId("ptrtoint", ptrLLVMSize);
            out.printf("    %s = ptrtoint %s %s to %s\n", ptrtoint, rhsLLVMType, rhs, ptrLLVMSize);
            rhs = ptrtoint;
        }

        String id = "%" + newId("t", lhsLLVMType);
        valueMap.put(node, id);

        out.println("    " + id + " = " + op + " i" + (node.getSize()*8) + " " + lhs + ", " + rhs);
    }

    @Override
    public void visit(UnaryOperation node) {
        if (valueMap.containsKey(node)) return;
        visit(node.getOperand().getIRNode());

        String o = valueMap.get(node.getOperand());
        String id = "%" + newId("t", "i32");
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
        String id = "%" + newId("t", "i1");
        valueMap.put(node, id);

        out.printf("    %s = icmp %s %s %s, %s\n", id, op, getLLVMType(lhs), lhs, rhs);
    }

    @Override
    public void visit(IntImmediate node) {
        if (valueMap.containsKey(node)) return;
        valueMap.put(node, String.valueOf(node.getValue()));
    }

    @Override
    public void visit(Call node) {

    }

    @Override
    public void visit(PhiNode node) {

    }

    @Override
    public void visit(Branch node) {
        visit(node.cond.getIRNode());
        String cond = valueMap.get(node.cond);
        if (!getLLVMType(valueMap.get(node.cond)).equals("i1")) {
            String iconv = "%" + newId("iconv", "i1");
            out.printf("    %s = trunc %s %s to i1\n", iconv, getLLVMType(valueMap.get(node.cond)), cond);
            cond = iconv;
        }
        String ifTrue = labelId(node.then);
        String ifFalse = labelId(node.otherwise);
        out.println("    " + "br i1 " + cond + ", label %" + ifTrue + ", label %" + ifFalse);
        out.println();
        visit(node.then);
        visit(node.otherwise);
    }

    @Override
    public void visit(Return node) {
        visit(node.ret.getIRNode());
        String ret = valueMap.get(node.ret);
        out.println("    ret i" + (node.ret.getSize()*8) + " " + ret);
        out.println();
    }

    @Override
    public void visit(Jump node) {
        out.println("    br label %" + labelId(node.target));
        out.println();
        visit(node.target);
    }

    @Override
    public void visit(StackAllocate node) {
        if (valueMap.containsKey(node)) return;
        String llvmSize = "i" + (node.getSize()*8);
        String id = "%" + newId(node.getHintName(), llvmSize + "*");
        valueMap.put(node, id);
        out.println("    " + id + " = alloca " + llvmSize);
    }

    @Override
    public void visit(HeapAllocate node) {
        if (valueMap.containsKey(node)) return;
        String mallocid = "%" + newId("malloc", "i8");
        String id = "%" + newId("ptrtoint", ptrLLVMSize);
        out.printf("    %s = call i8* @malloc(i64 %d)\n", mallocid, node.getAllocSize());
        out.printf("    %s = ptrtoint i8* %s to %s\n", id, mallocid, ptrLLVMSize);
        valueMap.put(node, id);
    }

    @Override
    public void visit(Load node) {
        if (valueMap.containsKey(node)) return;
        visit(node.getAddress().getIRNode());
        String addr = valueMap.get(node.getAddress());
        String id = "%" + newId(node.getHintName(), "i" + (node.getSize()*8));
        valueMap.put(node, id);
        if (!getLLVMType(addr).endsWith("*")) {
            String target = "i" + (node.getSize()*8) + "*";
            String inttoptr = "%" + newId("inttoptr", target);
            out.printf("    %s = inttoptr %s %s to %s\n", inttoptr, getLLVMType(addr), addr, target);
            addr = inttoptr;
        }
        out.println("    " + id + " = load i" + (node.getSize()*8) + "* " + addr);
    }

    @Override
    public void visit(Store node) {
        visit(node.address.getIRNode());
        visit(node.value.getIRNode());
        String addr = valueMap.get(node.address);
        String val = valueMap.get(node.value);
        if (!getLLVMType(addr).endsWith("*")) {
            String target = getLLVMType(addr) + "*";
            String inttoptr = "%" + newId("inttoptr", target);
            out.printf("    %s = inttoptr %s %s to %s\n", inttoptr, getLLVMType(addr), addr, target);
            addr = inttoptr;
        }
        String addrSize = getLLVMType(addr);
        String valSize = node.value instanceof IntImmediate ? addrSize.replace("*", "") : getLLVMType(val);
        if (!valSize.equals(addrSize.replace("*", ""))) {
            int src = Integer.parseInt(valSize.replace("i", ""));
            int tar = Integer.parseInt(addrSize.replace("*", "").replace("i", ""));
            String iconv = "%" + newId("iconv", "i" + tar);
            if (src > tar) {
                out.printf("    %s = trunc i%d %s to i%d\n", iconv, src, val, tar);
            } else {
                out.printf("    %s = sext i%d %s to i%d\n", iconv, src, val, tar);
            }
            valSize = "i" + tar;
            val = iconv;
        }
        out.println("    store " + valSize + " " + val + ", " + addrSize + " " + addr);
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
