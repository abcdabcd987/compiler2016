package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.AST.BinaryExpr;
import com.abcdabcd987.compiler2016.IR.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class RegisterInformationInjector {
    private IRRoot irRoot;

    public RegisterInformationInjector(IRRoot irRoot) {
        this.irRoot = irRoot;
    }

    /**
     * replace function arg define
     * replace function arg use
     * fix binary operation lhs immediate number
     * fix unary operation immediate number
     *
     * @param func function to be modified
     */
    private void modifyFunction(Function func) {
        // replace function arg define
        Map<Register, Register> argMap = new HashMap<>();
        Map<Register, Register> useMap = new HashMap<>();
        if (func.argVarRegList.size() > 0) argMap.put(func.argVarRegList.get(0), MIPSRegisterSet.A0);
        if (func.argVarRegList.size() > 1) argMap.put(func.argVarRegList.get(1), MIPSRegisterSet.A1);
        if (func.argVarRegList.size() > 2) argMap.put(func.argVarRegList.get(2), MIPSRegisterSet.A2);
        if (func.argVarRegList.size() > 3) argMap.put(func.argVarRegList.get(3), MIPSRegisterSet.A3);
        for (int i = 0; i < func.argVarRegList.size(); ++i) {
            StackSlot slot = new StackSlot("arg" + i);
            argMap.put(func.argVarRegList.get(i), slot);
            func.argVarRegList.set(i, slot);
        }

        for (BasicBlock BB : func.getReversePostOrder()) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                // replace function arg use
                Register defined = inst.getDefinedRegister();
                if (defined != null && argMap.containsKey(defined))
                    inst.setDefinedRegister(argMap.get(defined));

                useMap.clear();
                inst.getUsedRegister().forEach(x -> useMap.put(x, x));
                useMap.putAll(argMap);
                inst.setUsedRegister(useMap);

                // fix binary operation lhs immediate number
                if (inst instanceof BinaryOperation) {
                    BinaryOperation binop = (BinaryOperation) inst;
                    if (binop.lhs instanceof IntImmediate) {
                        VirtualRegister lhs = new VirtualRegister("lhs");
                        Move move = new Move(BB, lhs, binop.lhs);
                        binop.lhs = lhs;
                        inst.prepend(move);
                    }
                }

                // fix unary operation immediate number
                if (inst instanceof UnaryOperation) {
                    UnaryOperation unop = (UnaryOperation) inst;
                    if (unop.getOperand() instanceof IntImmediate) {
                        int value = ((IntImmediate) unop.getOperand()).getValue();
                        switch (unop.getOp()) {
                            case NEG: value = -value; break;
                            case NOT: value = ~value; break;
                            default: assert false;
                        }
                        inst.prepend(new Move(BB, unop.getDest(), new IntImmediate(value)));
                        inst.remove();
                    }
                }
            }
        }
    }

    public void run() {
        for (Function func : irRoot.functions.values()) {
            modifyFunction(func);
        }
    }
}
