package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Symbol.Type;
import com.abcdabcd987.compiler2016.Symbol.VariableType;

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

    private String toLLVMType(Type type) {
        switch (type.type) {
            case INT: return "i" + (CompilerOptions.getSizeInt()*8);
            case BOOL: return "i" + (CompilerOptions.getSizeBool()*8);
            case VOID: return "void";
            default: return "i" + (CompilerOptions.getSizePointer()*8);
        }
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
        for (IRNode i = node.getHead(); i != null; i = i.next) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Function node) {
        out.print("define " + toLLVMType(node.getType().returnType) + " @" + node.getName() + "(");
        for (int i = 0; i < node.getType().argTypes.size(); ++i) {
            String type = toLLVMType(node.getType().argTypes.get(i));
            if (i != 0) out.print(", ");
            out.printf("%s %%op%d", type, i);
        }
        out.println(") {");

        IRNode x = node.getStartBB().getHead();
        IRNode arg = node.getStartBB().getHead();
        for (int i = 1; i < node.getType().argTypes.size(); ++i) x = x.next;
        for (int i = 0; i < node.getType().argTypes.size(); ++i) {
            IntValue hack = new IntImmediate(0, 0);
            valueMap.put(hack, "%op" + i);
            IRNode next = x.next;
            x.next = new Store((StackAllocate) arg, hack);
            x.next.next = next;
            x = x.next;
            arg = arg.next;
        }

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

        String id = "%" + newId("t", "i" + node.getSize()*8);
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
        if (valueMap.containsKey(node)) return;
        node.getArgs().forEach(x -> visit(x.getIRNode()));
        String retType = toLLVMType(node.getFunc().getType().returnType);
        String id = retType.equals("void") ? null : "%" + newId("call", retType);
        valueMap.put(node, id);
        out.print("    ");
        if (id != null) out.print(id + " = ");
        out.printf("call %s @%s(", retType, node.getFunc().getName());
        for (int i = 0; i < node.getArgs().size(); ++i) {
            String op = valueMap.get(node.getArgs().get(i));
            if (i != 0) out.print(", ");
            out.print(getLLVMType(op) + " " + op);
        }
        out.println(")");
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
        visit(node.getAllocSize().getIRNode());
        String mallocid = "%" + newId("malloc", "i8");
        String id = "%" + newId("ptrtoint", ptrLLVMSize);
        String size = valueMap.get(node.getAllocSize());
        out.printf("    %s = call i8* @malloc(i64 %s)\n", mallocid, size);
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
    public void visit(IntConvert node) {
        int src = node.getSource().getSize() * 8;
        int tar = node.getSize() * 8;
        visit(node.getSource().getIRNode());
        String val = valueMap.get(node.getSource());
        String id = "%" + newId("iconv", "i" + tar);
        valueMap.put(node, id);
        if (src > tar) {
            out.printf("    %s = trunc i%d %s to i%d\n", id, src, val, tar);
        } else {
            out.printf("    %s = sext i%d %s to i%d\n", id, src, val, tar);
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
