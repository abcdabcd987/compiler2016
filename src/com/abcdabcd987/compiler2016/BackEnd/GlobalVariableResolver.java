package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-05-02.
 */
public class GlobalVariableResolver {
    private IRRoot ir;
    private static class FunctionInfo {
        Map<StaticData, VirtualRegister> staticMap = new HashMap<>();
        Set<StaticData> writtenStatic = new HashSet<>();
        Set<StaticData> recursiveStaticUse = new HashSet<>();
    }
    private Map<Function, FunctionInfo> funcInfo = new HashMap<>();

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
        FunctionInfo info = new FunctionInfo();
        funcInfo.put(func, info);
        Map<StaticData, VirtualRegister> staticMap = info.staticMap;
        Set<StaticData> writtenStatic = info.writtenStatic;
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
                    // postpone write back
                    writtenStatic.add((StaticData) defined);
                }
            }

        // load all on entry
        BasicBlock b = func.getStartBB();
        IRInstruction i = b.getHead();
        staticMap.forEach((data, vr) -> i.prepend(new Load(b, vr, data.getRegisterSize(), data, data instanceof StaticString)));
    }

    private void calcRecursiveUse() {
        ir.builtinFunctions.forEach(x -> funcInfo.put(x, new FunctionInfo()));
        for (Function func : ir.functions.values()) {
            FunctionInfo info = funcInfo.get(func);
            info.recursiveStaticUse.addAll(info.staticMap.keySet());
            func.recursiveCalleeSet.forEach(x -> info.recursiveStaticUse.addAll(funcInfo.get(x).staticMap.keySet()));
        }
    }

    public void run() {
        for (Function func : ir.functions.values()) {
            processFunction(func);
        }

        calcRecursiveUse();

        Set<StaticData> reloadSet = new HashSet<>();
        for (Function func : ir.functions.values()) {
            FunctionInfo info = funcInfo.get(func);
            Set<StaticData> usedSet = info.staticMap.keySet();
            if (usedSet.isEmpty()) continue;
            for (BasicBlock BB : func.getReversePostOrder()) {
                for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                    if (!(inst instanceof Call)) continue;
                    Call call = (Call) inst;
                    Function callee = call.getFunc();
                    FunctionInfo calleeInfo = funcInfo.get(callee);

                    // write back before function call
                    for (StaticData data : info.writtenStatic)
                        if (calleeInfo.recursiveStaticUse.contains(data))
                            call.prepend(new Store(BB, info.staticMap.get(data), data.getRegisterSize(), data));

                    // reload after function call
                    if (calleeInfo == null) continue; // ignore builtin hack
                    if (calleeInfo.writtenStatic.isEmpty()) continue;
                    reloadSet.clear();
                    reloadSet.addAll(calleeInfo.writtenStatic);
                    reloadSet.retainAll(usedSet);
                    for (StaticData data : reloadSet) {
                        call.append(new Load(BB, info.staticMap.get(data), data.getRegisterSize(), data, data instanceof StaticString));
                    }
                }
            }
        }

        // write back on exit
        for (Function func : ir.functions.values()) {
            Return ret = func.retInstruction.get(0);
            FunctionInfo info = funcInfo.get(func);
            for (StaticData data : info.writtenStatic) {
                ret.prepend(new Store(ret.getBasicBlock(), info.staticMap.get(data), data.getRegisterSize(), data));
            }
        }
    }
}
