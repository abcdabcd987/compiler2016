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
    private final int wordSize = CompilerOptions.getSizeInt();

    public RegisterInformationInjector(IRRoot irRoot) {
        this.irRoot = irRoot;
    }

    private void replaceFunctionArg(Function func) {
        Map<Register, Register> argMap = new HashMap<>();
        Map<Register, Register> useMap = new HashMap<>();

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

        // replace function arg use
        if (argMap.isEmpty()) return;
        for (BasicBlock BB : func.getReversePostOrder()) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
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
        }
    }

    private void replaceImmediateNumber(Function func, BasicBlock BB, IRInstruction inst) {
        // fix binary operation lhs immediate number
        if (inst instanceof BinaryOperation) {
            BinaryOperation binop = (BinaryOperation) inst;
            if (binop.getLhs() instanceof IntImmediate) {
                VirtualRegister lhs = new VirtualRegister("lhs");
                Move move = new Move(BB, lhs, binop.getLhs());
                binop.setLhs(lhs);
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

    private boolean modifyBuiltinFunctionCall(Function func, BasicBlock BB, Call call, Function callee, List<IntValue> args) {
        if (callee == irRoot.builtinPrint) {
            call.prepend(new Move(BB, A0, args.get(0)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            call.remove();
            return true;
        } else if (callee == irRoot.builtinPrintln) {
            StaticString data = irRoot.stringPool.get("\\n");
            call.prepend(new Move(BB, A0, args.get(0)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            call.prepend(new Load(BB, A0, data.getRegisterSize(), data, true));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            call.remove();
            return true;
        }
        return false;
    }

    private void modifyFunctionCall(Function func, BasicBlock BB, IRInstruction inst) {
        if (!(inst instanceof Call)) return;
        Call call = (Call) inst;
        Function callee = call.getFunc();
        List<IntValue> args = call.getArgs();
        if (modifyBuiltinFunctionCall(func, BB, call, callee, args)) return;

    }

    private void modifyHeapAllocation(Function func, BasicBlock BB, IRInstruction inst) {
        if (!(inst instanceof HeapAllocate)) return;
        HeapAllocate h = (HeapAllocate) inst;
        inst.prepend(new Move(BB, A0, h.getAllocSize()));
        inst.append(new Move(BB, h.getDest(), V0));
        if (func.argVarRegList.size() > 0) {
            inst.append(new Load(BB, A0, wordSize, func.argVarRegList.get(0), 0));
        }
    }

    public void run() {
        for (Function func : irRoot.functions.values()) replaceFunctionArg(func);
        for (Function func : irRoot.functions.values()) {
            for (BasicBlock BB : func.getReversePostOrder()) {
                for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                    replaceImmediateNumber(func, BB, inst);
                    modifyFunctionCall(func, BB, inst);
                    modifyHeapAllocation(func, BB, inst);
                }
            }
        }
    }
}
