package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abcdabcd987.compiler2016.MIPS.MIPSRegisterSet.*;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class RegisterInformationInjector {
    private IRRoot irRoot;

    public RegisterInformationInjector(IRRoot irRoot) {
        this.irRoot = irRoot;
    }

    private void replaceFunctionArgDefine(Function func, Map<Register, Register> argMap, Map<Register, Register> useMap) {
        // replace function arg define
        if (func.argVarRegList.size() > 0) argMap.put(func.argVarRegList.get(0), MIPSRegisterSet.A0);
        if (func.argVarRegList.size() > 1) argMap.put(func.argVarRegList.get(1), MIPSRegisterSet.A1);
        if (func.argVarRegList.size() > 2) argMap.put(func.argVarRegList.get(2), MIPSRegisterSet.A2);
        if (func.argVarRegList.size() > 3) argMap.put(func.argVarRegList.get(3), MIPSRegisterSet.A3);
        for (int i = 0; i < func.argVarRegList.size(); ++i) {
            StackSlot slot = new StackSlot(func, "arg" + i);
            if (i > 4) argMap.put(func.argVarRegList.get(i), slot);
            func.argVarRegList.set(i, slot);
        }
    }

    private void replaceFunctionArgUse(Function func, BasicBlock BB, IRInstruction inst, Map<Register, Register> argMap, Map<Register, Register> useMap) {
        // replace function arg use
        Register defined = inst.getDefinedRegister();
        if (defined != null && argMap.containsKey(defined))
            inst.setDefinedRegister(argMap.get(defined));

        Collection<Register> used = inst.getUsedRegister();
        if (!used.isEmpty()) {
            useMap.clear();
            used.forEach(x -> useMap.put(x, x));
            useMap.putAll(argMap);
            inst.setUsedRegister(useMap);
        }
    }

    private void replaceImmediateNumber(Function func, BasicBlock BB, IRInstruction inst) {
        // fix binary operation lhs immediate number
        if (inst instanceof BinaryOperation) {
            BinaryOperation binop = (BinaryOperation) inst;
            if (binop.lhs instanceof IntImmediate) {
                VirtualRegister lhs = new VirtualRegister("lhs");
                Move move = new Move(BB, lhs, binop.lhs);
                binop.lhs = lhs;
                inst.prepend(move);
            }
        } else

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
        } else

        // fix store operation immediate number
        if (inst instanceof Store) {
            Store store = (Store) inst;
            if (store.getValue() instanceof IntImmediate) {
                VirtualRegister reg = new VirtualRegister("imm");
                inst.prepend(new Move(BB, reg, store.getValue()));
                store.setValue(reg);
            }
        }
    }

    private void modifyFunctionCall(Function func, BasicBlock BB, IRInstruction inst) {
        if (!(inst instanceof Call)) return;
        Call call = (Call) inst;
        Function callee = call.getFunc();
        List<IntValue> args = call.getArgs();

        // copy argument
        for (int i = 0; i < args.size(); ++i) {
            IntValue value = args.get(i);
            inst.prepend(new Move(BB, A0, args.get(i)));
            inst.prepend(new Store(BB, CompilerOptions.getSizeInt(), callee.argVarRegList.get(i), 0, A0));
        }
        if (args.size() > 0) inst.prepend(new Move(BB, A0, args.get(0)));
        if (args.size() > 1) inst.prepend(new Move(BB, A1, args.get(1)));
        if (args.size() > 2) inst.prepend(new Move(BB, A2, args.get(2)));
        if (args.size() > 3) inst.prepend(new Move(BB, A3, args.get(3)));

        // move result
        if (call.getDest() != null) inst.append(new Move(BB, call.getDest(), V0));
    }

    public void run() {
        Map<Register, Register> argMap = new HashMap<>();
        Map<Register, Register> useMap = new HashMap<>();
        for (Function func : irRoot.functions.values()) {
            argMap.clear();
            useMap.clear();
            replaceFunctionArgDefine(func, argMap, useMap);

            for (BasicBlock BB : func.getReversePostOrder()) {
                for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                    replaceFunctionArgUse(func, BB, inst, argMap, useMap);
                    replaceImmediateNumber(func, BB, inst);
                    modifyFunctionCall(func, BB, inst);
                }
            }
        }
    }
}
