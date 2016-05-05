package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.IR.*;
import com.abcdabcd987.compiler2016.Utility.Utility;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-05-01.
 */
public class MIPSPrinter implements IIRVisitor {
    private PrintStream out;
    private Map<String, Integer> counter = new HashMap<>();
    private Map<Object, String> nameMap = new HashMap<>();
    private Map<BasicBlock, Integer> blockNumber = new HashMap<>();
    private BasicBlock curBB;
    private boolean isDefiningStaticData;

    public MIPSPrinter(PrintStream out) {
        this.out = out;
    }

    private String newId(String name) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
//        if (cnt == 1) return name;
        return name + "_" + cnt;
    }

    private String blockLabel(BasicBlock BB) {
        String id = nameMap.get(BB);
        if (id == null) {
            id = newId(BB.getHintName());
            nameMap.put(BB, id);
        }
        return id;
    }

    private String dataId(StaticData node) {
        String id = nameMap.get(node);
        if (id == null) {
            id = newId(node.getHintName());
            nameMap.put(node, id);
        }
        return id;
    }

    @Override
    public void visit(IRRoot node) {
        out.println(".data");
        out.println("    .align 2");
        isDefiningStaticData = true;
        node.stringPool.values().forEach(this::visit);
        node.dataList.forEach(x -> x.accept(this));
        isDefiningStaticData = false;
        out.println();
        out.println(".text");
        out.println("main:");
        out.println("    sub $sp, $sp, 4");
        out.println("    sw $ra, 0($sp)");
        out.println("    jal " + blockLabel(node.functions.get("__init").getStartBB()));
//        // print exitcode
//        out.println("    move $a0, $v0");
//        out.println("    li $v0, 1");
//        out.println("    syscall");
        out.println("    lw $ra, 0($sp)");
        out.println("    jr $ra");
        out.println();
        node.functions.values().forEach(this::visit);
        out.println();
        out.println();
        out.println();
        try {
            BufferedReader br = new BufferedReader(new FileReader("lib/builtin_functions.s"));
            String line;
            while ((line = br.readLine()) != null) out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        out.printf("%s:\n", blockLabel(node));
        curBB = node;
        for (IRInstruction i = node.getHead(); i != null; i = i.getNext()) {
            i.accept(this);
        }
        out.println();
    }

    @Override
    public void visit(Function node) {
        out.printf("###### function %s:\n", node.getName());
        node.calcReversePostOrder();
        List<BasicBlock> BBOrder = node.getReversePostOrder();
        for (int i = 0; i < BBOrder.size(); ++i)
            blockNumber.put(BBOrder.get(i), i);
        BBOrder.forEach(this::visit);
    }

    @Override
    public void visit(BinaryOperation node) {
        String op = null;
        switch (node.getOp()) {
            case ADD: op = "add"; break;
            case SUB: op = "sub"; break;
            case MUL: op = "mul"; break;
            case DIV: op = "div"; break;
            case MOD: op = "rem"; break;
            case SHL: op = "sll"; break;
            case SHR: op = "srl"; break;
            case AND: op = "and"; break;
            case OR: op = "or"; break;
            case XOR: op = "xor"; break;
            default: assert false;
        }
        //if (node.getRhs() instanceof IntImmediate) op += "i";
        out.printf("    %s ", op);
        node.getDest().accept(this);
        out.print(", ");
        node.getLhs().accept(this);
        out.print(", ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(UnaryOperation node) {
        String op = null;
        switch (node.getOp()) {
            case NEG: op = "neg"; break;
            case NOT: op = "not"; break;
            default: assert false;
        }
        out.printf("    %s ", op);
        node.getDest().accept(this);
        out.print(", ");
        node.getOperand().accept(this);
        out.println();
    }

    @Override
    public void visit(IntComparison node) {
        String op = null;
        switch (node.getCond()) {
            case EQ: op = "seq"; break;
            case NE: op = "sne"; break;
            case GT: op = "sgt"; break;
            case GE: op = "sge"; break;
            case LT: op = "slt"; break;
            case LE: op = "sle"; break;
            default: assert false;
        }
        out.printf("    %s ", op);
        node.getDest().accept(this);
        out.print(", ");
        assert !(node.getLhs() instanceof IntImmediate);
        node.getLhs().accept(this);
        out.print(", ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(IntImmediate node) {
        out.print(node.getValue());
    }

    @Override
    public void visit(Call node) {
        Function func = node.getFunc();
        if (func.builtinFunctionHackName == null) {
            out.printf("    jal %s\n", blockLabel(node.getFunc().getStartBB()));
        } else {
            out.printf("    jal %s\n", func.builtinFunctionHackName);
        }
    }

    @Override
    public void visit(SystemCall node) {
        out.println("    syscall");
    }

    @Override
    public void visit(PhiInstruction node) {
        assert false;
    }

    @Override
    public void visit(Branch node) {
        out.printf("    beq ");
        node.getCond().accept(this);
        out.printf(", 0, %s\n", blockLabel(node.getElse()));
        if (blockNumber.get(curBB)+1 != blockNumber.get(node.getThen())) {
            out.printf("    j %s\n", blockLabel(node.getThen()));
        }
    }

    @Override
    public void visit(Return node) {
        out.println("    jr $ra");
    }

    @Override
    public void visit(Jump node) {
        if (blockNumber.get(curBB)+1 != blockNumber.get(node.getTarget())) {
            out.println("    j "+ blockLabel(node.getTarget()));
        }
    }

    @Override
    public void visit(VirtualRegister node) {
        assert false;
    }

    @Override
    public void visit(PhysicalRegister node) {
        out.print(node.getName());
    }

    @Override
    public void visit(StackSlot node) {
        assert false;
    }

    @Override
    public void visit(HeapAllocate node) {
        out.println("    li $v0, 9");
        out.println("    syscall");
    }

    @Override
    public void visit(Load node) {
        String op = null;
        switch (node.getSize()) {
            case 1: op = "lb"; break;
            case 2: op = "lh"; break;
            case 4: op = "lw"; break;
            default: assert false;
        }
        if (node.isStaticData && node.isLoadAddress) op = "la";
        out.printf("    %s ", op);
        node.getDest().accept(this);
        if (node.isStaticData) {
            out.print(", ");
            node.getAddress().accept(this);
            out.println();
        } else {
            out.printf(", %d(", node.getOffset());
            node.getAddress().accept(this);
            out.println(")");
        }
    }

    @Override
    public void visit(Store node) {
        String op = null;
        switch (node.getSize()) {
            case 1: op = "sb"; break;
            case 2: op = "sh"; break;
            case 4: op = "sw"; break;
            default: assert false;
        }
        out.printf("    %s ", op);
        node.getValue().accept(this);
        if (node.isStaticData) {
            out.print(", ");
            node.getAddress().accept(this);
            out.println();
        } else {
            out.printf(", %d(", node.getOffset());
            node.getAddress().accept(this);
            out.println(")");
        }
    }

    @Override
    public void visit(Move node) {
        if (node.getSource() instanceof IntImmediate) {
            int value = ((IntImmediate) node.getSource()).getValue();
            if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
                out.print("    add ");
                node.getDest().accept(this);
                out.printf(", $0, %d\n", value);
            } else {
                out.print("    li ");
                node.getDest().accept(this);
                out.printf(", %d\n", value);
            }
        } else if (node.getSource() instanceof PhysicalRegister) {
            out.print("    move ");
            node.getDest().accept(this);
            out.print(", ");
            node.getSource().accept(this);
            out.println();
        } else {
            assert false;
        }
    }

    @Override
    public void visit(StaticSpace node) {
        if (isDefiningStaticData) out.printf("%s: .space %d\n", dataId(node), node.length);
        else out.print(dataId(node));
    }

    @Override
    public void visit(StaticString node) {
        if (isDefiningStaticData) {
            out.printf("%s:\n", dataId(node));
            out.printf("    .word %d\n", Utility.unescape(node.value).length());
            out.printf("    .asciiz \"%s\"\n", node.value);
            out.println("    .align 2");
        }
        else out.print(dataId(node));
    }
}
