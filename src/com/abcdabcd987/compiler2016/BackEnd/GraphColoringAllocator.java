package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Register allocation by graph coloring
 * Chaitin's Algorithm
 * see: http://kodu.ut.ee/~varmo/TM2010/slides/tm-reg.pdf
 * Created by abcdabcd987 on 2016-05-05.
 */
public class GraphColoringAllocator extends RegisterAllocator {
    private static class VirtualRegisterInfo {
        Set<VirtualRegister> neighbour = new HashSet<>();
        int degree = 0;
        boolean deleted = false;
        Register color = null;
        Set<VirtualRegister> suggestSame = new HashSet<>();
    }

    private IRRoot ir;
    private List<PhysicalRegister> physicalRegisters;
    private int colors;
    private Function curFunc;
    private PhysicalRegister tmpPR1;
    private PhysicalRegister tmpPR2;
    private Map<VirtualRegister, VirtualRegisterInfo> vrInfo = new HashMap<>();
    private Stack<VirtualRegister> stack = new Stack<>();
    private Set<VirtualRegister> nodes = new HashSet<>();
    private Set<VirtualRegister> smallDegreeNodes = new HashSet<>();
    private Map<Register, Register> renameMap = new HashMap<>();
    private Set<PhysicalRegister> usedColor = new HashSet<>();
    private final int wordSize = CompilerOptions.getSizeInt();

    public GraphColoringAllocator(IRRoot ir, Collection<PhysicalRegister> physicalRegisters, PhysicalRegister tmpPR1, PhysicalRegister tmpPR2) {
        this.ir = ir;
        this.tmpPR1 = tmpPR1;
        this.tmpPR2 = tmpPR2;
        this.physicalRegisters = new ArrayList<>(physicalRegisters);
        this.physicalRegisters.remove(tmpPR1);
        this.physicalRegisters.remove(tmpPR2);
        this.colors = physicalRegisters.size();
    }

    private VirtualRegisterInfo getVRInfo(VirtualRegister vr) {
        VirtualRegisterInfo info = vrInfo.get(vr);
        if (info == null) {
            info = new VirtualRegisterInfo();
            vrInfo.put(vr, info);
        }
        return info;
    }

    private void addEdge(VirtualRegister x, VirtualRegister y) {
        getVRInfo(x).neighbour.add(y);
        getVRInfo(y).neighbour.add(x);
    }

    /**
     * build the interference graph
     * see: http://www.eng.utah.edu/~cs5470/schedule/Lec25.pdf
     * */
    private void buildGraph() {
        List<VirtualRegister> args = curFunc.argVarRegList;
        args.forEach(this::getVRInfo);
//        for (int i = 0; i < args.size(); ++i)
//            for (int j = i+1; j < args.size(); ++j)
//                addEdge(args.get(i), args.get(j));

        for (BasicBlock BB : curFunc.getReversePreOrder()) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                Register defined = inst.getDefinedRegister();
                if (!(defined instanceof VirtualRegister)) continue;
                VirtualRegisterInfo info = getVRInfo((VirtualRegister) defined);// ensure vrInfo.get(define) != null
                if (inst instanceof Move) {
                    IntValue src = ((Move) inst).getSource();
                    if (src instanceof VirtualRegister) {
                        info.suggestSame.add((VirtualRegister) src);
                        getVRInfo((VirtualRegister) src).suggestSame.add((VirtualRegister) defined);
                    }
                    inst.liveOut.stream()
                            .filter(x -> x != src && x != defined)
                            .forEach(x -> addEdge(x, (VirtualRegister) defined));
                } else {
                    inst.liveOut.stream()
                            .filter(x -> x != defined)
                            .forEach(x -> addEdge(x, (VirtualRegister) defined));
                }
            }
        }

        vrInfo.values().forEach(x -> x.degree = x.neighbour.size());
    }

    private void removeNode(VirtualRegister node) {
        VirtualRegisterInfo info = vrInfo.get(node);
        for (VirtualRegister neighbour : info.neighbour) {
            VirtualRegisterInfo ni = vrInfo.get(neighbour);
            if (!ni.deleted) {
                --ni.degree;
                if (ni.degree < colors) smallDegreeNodes.add(neighbour);
            }
        }
        info.deleted = true;
        stack.push(node);
        nodes.remove(node);
    }

    private void colorize() {
        vrInfo.keySet().forEach(nodes::add);
        nodes.stream().filter(x -> vrInfo.get(x).neighbour.size() < colors).forEach(smallDegreeNodes::add);
        while (!nodes.isEmpty()) {
            // if exist a node whose degree < colors
            while (!smallDegreeNodes.isEmpty()) {
                Iterator<VirtualRegister> iter = smallDegreeNodes.iterator();
                VirtualRegister node = iter.next();
                iter.remove();
                removeNode(node);
            }
            if (nodes.isEmpty()) break;

            // if degree >= colors for all nodes
            Iterator<VirtualRegister> iter = nodes.iterator();
            VirtualRegister node = iter.next();
            iter.remove();
            removeNode(node);
        }

        while (!stack.empty()) {
            VirtualRegister node = stack.pop();
            VirtualRegisterInfo info = vrInfo.get(node);
            usedColor.clear();
            for (VirtualRegister neighbour : info.neighbour) {
                VirtualRegisterInfo ni = vrInfo.get(neighbour);
                if (!ni.deleted && ni.color instanceof PhysicalRegister)
                    usedColor.add((PhysicalRegister) ni.color);
            }

            PhysicalRegister forced = node.forcedPhysicalRegister;
            if (forced != null) {
                assert !usedColor.contains(forced);
                info.color = forced;
            } else {
                for (VirtualRegister vr : info.suggestSame) {
                    Register reg = getVRInfo(vr).color;
                    if (reg instanceof PhysicalRegister && !usedColor.contains(reg)) {
                        info.color = reg;
                        break;
                    }
                }
                if (info.color == null) {
                    for (PhysicalRegister pr : physicalRegisters) {
                        if (!usedColor.contains(pr)) {
                            info.color = pr;
                            break;
                        }
                    }
                    if (info.color == null) {
                        info.color = curFunc.argStackSlotMap.get(node);
                        if (info.color == null) info.color = new StackSlot(curFunc, node.getHintName());
                    }
                }
            }
            info.deleted = false;
        }
    }

    private void rewriteInstruction() {
        for (BasicBlock BB : curFunc.getReversePreOrder()) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                Collection<Register> used = inst.getUsedRegister();
                if (!(inst instanceof Call)) {
                    if (!used.isEmpty()) {
                        boolean tmpPR1Used = false;
                        renameMap.clear();
                        for (Register reg : used) {
                            if (reg instanceof VirtualRegister) {
                                Register color = vrInfo.get(reg).color;
                                if (color instanceof StackSlot) {
                                    PhysicalRegister pr = tmpPR1Used ? tmpPR1 : tmpPR2;
                                    inst.prepend(new Load(BB, pr, wordSize, color, 0));
                                    tmpPR1Used = true;
                                    renameMap.put(reg, pr);
                                    curFunc.usedPhysicalGeneralRegister.add(pr);
                                } else {
                                    renameMap.put(reg, color);
                                    curFunc.usedPhysicalGeneralRegister.add((PhysicalRegister) color);
                                }
                            } else {
                                renameMap.put(reg, reg);
                            }
                        }
                        inst.setUsedRegister(renameMap);
                    }
                } else {
                    List<IntValue> args = ((Call) inst).getArgs();
                    for (int i = 0; i < args.size(); ++i) {
                        IntValue val = args.get(i);
                        if (val instanceof VirtualRegister) args.set(i, vrInfo.get(val).color);
                    }
                }

                Register defined = inst.getDefinedRegister();
                if (defined instanceof VirtualRegister) {
                    Register color = vrInfo.get(defined).color;
                    if (color instanceof StackSlot) {
                        inst.append(new Store(BB, tmpPR1, wordSize, color, 0));
                        inst.setDefinedRegister(tmpPR1);
                        curFunc.usedPhysicalGeneralRegister.add(tmpPR1);
                        inst = inst.getNext();
                    } else {
                        inst.setDefinedRegister(color);
                        curFunc.usedPhysicalGeneralRegister.add((PhysicalRegister) color);
                    }
                }
            }
        }
    }

    public void run() {
        new InstructionLivenessAnalysis(ir).run();

        for (Function func : ir.functions.values()) {
            curFunc = func;
            vrInfo.clear();
            stack.clear();
            nodes.clear();
            smallDegreeNodes.clear();

            buildGraph();
            colorize();
            rewriteInstruction();
        }
    }
}
