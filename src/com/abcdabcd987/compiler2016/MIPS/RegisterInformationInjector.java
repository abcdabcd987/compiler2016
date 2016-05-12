package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;

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

        // make stack slot for args and load extra args
        IRInstruction first = func.getStartBB().getHead();
        for (int i = 0; i < func.argVarRegList.size(); ++i) {
            VirtualRegister vr = func.argVarRegList.get(i);
            StackSlot slot = new StackSlot(func, "arg" + i);
            func.argStackSlotMap.put(vr, slot);
            if (i > 3) first.prepend(new Load(func.getStartBB(), vr, wordSize, slot, 0));
        }
        if (func.argVarRegList.size() > 0) func.argVarRegList.get(0).forcedPhysicalRegister = MIPSRegisterSet.A0;
        if (func.argVarRegList.size() > 1) func.argVarRegList.get(1).forcedPhysicalRegister = MIPSRegisterSet.A1;
        if (func.argVarRegList.size() > 2) func.argVarRegList.get(2).forcedPhysicalRegister = MIPSRegisterSet.A2;
        if (func.argVarRegList.size() > 3) func.argVarRegList.get(3).forcedPhysicalRegister = MIPSRegisterSet.A3;
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
        } else

            // fix int comparison operation immediate number
            if (inst instanceof IntComparison) {
                IntComparison icmp = (IntComparison) inst;
                if (icmp.getLhs() instanceof  IntImmediate) {
                    VirtualRegister lhs = new VirtualRegister("lhs");
                    inst.prepend(new Move(BB, lhs, icmp.getLhs()));
                    icmp.setLhs(lhs);
                }
            }
    }

    private boolean modifyBuiltinFunctionCall(Function func, BasicBlock BB, Call call, Function callee, List<IntValue> args) {
        if (callee == irRoot.builtinPrintString) {
            if (func.argVarRegList.size() > 0) call.prepend(new Store(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.prepend(new BinaryOperation(BB, A0, BinaryOperation.BinaryOp.ADD, args.get(0), new IntImmediate(wordSize)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            if (func.argVarRegList.size() > 0) call.append(new Load(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.remove();
            return true;
        } else if (callee == irRoot.builtinPrintlnString) {
            StaticString data = irRoot.stringPool.get("\\n");
            if (func.argVarRegList.size() > 0) call.prepend(new Store(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.prepend(new BinaryOperation(BB, A0, BinaryOperation.BinaryOp.ADD, args.get(0), new IntImmediate(wordSize)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            call.prepend(new Load(BB, A0, data.getRegisterSize(), data, true));
            call.prepend(new BinaryOperation(BB, A0, BinaryOperation.BinaryOp.ADD, A0, new IntImmediate(wordSize)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            if (func.argVarRegList.size() > 0) call.append(new Load(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.remove();
            return true;
        } else if (callee == irRoot.builtinPrintInt) {
            if (func.argVarRegList.size() > 0) call.prepend(new Store(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.prepend(new Move(BB, A0, args.get(0)));
            call.prepend(new Move(BB, V0, new IntImmediate(1)));
            call.prepend(new SystemCall(BB));
            if (func.argVarRegList.size() > 0) call.append(new Load(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.remove();
            return true;
        } else if (callee == irRoot.builtinPrintlnInt) {
            StaticString data = irRoot.stringPool.get("\\n");
            if (func.argVarRegList.size() > 0) call.prepend(new Store(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
            call.prepend(new Move(BB, A0, args.get(0)));
            call.prepend(new Move(BB, V0, new IntImmediate(1)));
            call.prepend(new SystemCall(BB));
            call.prepend(new Load(BB, A0, data.getRegisterSize(), data, true));
            call.prepend(new BinaryOperation(BB, A0, BinaryOperation.BinaryOp.ADD, A0, new IntImmediate(wordSize)));
            call.prepend(new Move(BB, V0, new IntImmediate(4)));
            call.prepend(new SystemCall(BB));
            if (func.argVarRegList.size() > 0) call.append(new Load(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
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
        if (func.argVarRegList.size() > 0) inst.append(new Store(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
        HeapAllocate h = (HeapAllocate) inst;
        inst.prepend(new Move(BB, A0, h.getAllocSize()));
        inst.append(new Move(BB, h.getDest(), V0));
        if (func.argVarRegList.size() > 0) inst.append(new Load(BB, A0, wordSize, func.argStackSlotMap.get(func.argVarRegList.get(0)), 0));
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
