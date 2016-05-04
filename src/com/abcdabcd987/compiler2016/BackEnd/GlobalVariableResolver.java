package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-05-02.
 */
public class GlobalVariableResolver {
    private IRRoot ir;

    public GlobalVariableResolver(IRRoot ir) {
        this.ir = ir;
    }

    private VirtualRegister getVR(StaticData data, Map<StaticData, VirtualRegister> staticMap) {
        VirtualRegister reg = staticMap.get(data);
        if (reg == null) {
            reg = new VirtualRegister(data.getHintName());
            staticMap.put(data, reg);
        }
        return reg;
    }

    private void processFunction(Function func) {
        Map<StaticData, VirtualRegister> staticMap = new HashMap<>();
        Map<Register, Register> renameMap = new HashMap<>();

        // replace usage in instructions
        for (BasicBlock BB : func.getReversePostOrder())
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                if ((inst instanceof Load && ((Load) inst).isStaticData) ||
                    (inst instanceof Store && ((Store) inst).isStaticData))
                    continue;
                Collection<Register> used = inst.getUsedRegister();
                if (!used.isEmpty()) {
                    renameMap.clear();
                    used.forEach(x -> renameMap.put(x, x));
                    for (Register register : used)
                        if (register instanceof StaticData) {
                            StaticData data = (StaticData) register;
                            renameMap.put(register, getVR(data, staticMap));
                        }
                    inst.setUsedRegister(renameMap);
                }
                Register defined = inst.getDefinedRegister();
                if (defined instanceof StaticData) {
                    VirtualRegister reg = getVR((StaticData) defined, staticMap);
                    inst.setDefinedRegister(reg);
                    // write back immediately
                    inst.append(new Store(BB, ((StaticData) defined).getRegisterSize(), (StaticData) defined, reg));
                }
            }

        // load all on entry
        BasicBlock b = func.getStartBB();
        IRInstruction i = b.getHead();
        staticMap.forEach((data, vr) -> i.prepend(new Load(b, vr, data.getRegisterSize(), data, data instanceof StaticString)));
    }

//    private void processStringData() {
//        Function initFunc = ir.functions.get("__init");
//        BasicBlock BB = initFunc.getStartBB();
//        IRInstruction inst = BB.getHead();
//        VirtualRegister lenReg = new VirtualRegister("len");
//        for (StaticString data : ir.stringPool.values()) {
//            inst.prepend(new Move(BB, lenReg, new IntImmediate(data.value.length())));
//            inst.prepend(new Store(BB, CompilerOptions.getSizeInt(), data, lenReg));
//        }
//    }

    public void run() {
//        processStringData();
        for (Function func : ir.functions.values()) {
            processFunction(func);
        }
    }
}
