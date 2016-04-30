package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class RegisterAllocator {
    private static class Range {
        int from;
        int to;

        public Range(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class LifeInterval {
        List<Range> range = new ArrayList<>();

        void addRange(int from, int to) {
            range.add(new Range(from, to));
        }

        void setFrom(int from) {
            if (range.isEmpty()) range.add(new Range(from, from));
            else range.get(0).from = from;
        }
    }

    private static class InstructionInfo {
        int id;
    }

    private static class BlockInfo {
        Set<VirtualRegister> liveIn; // don't new ArrayList
    }

    private Function func;

    /** BB in reverse post order */
    private List<BasicBlock> blocksRPO;

    private Map<IRInstruction, InstructionInfo> instInfo = new HashMap<>();
    private Map<BasicBlock, BlockInfo> blockInfo = new HashMap<>();
    private Map<VirtualRegister, LifeInterval> intervals = new HashMap<>();

    public RegisterAllocator(Function func) {
        this.func = func;
    }

    public void run() {
        blocksRPO = func.getReversePostOrder();
        numberInstructions();
    }

    /**
     * see <a href="https://github.com/qtproject/qtdeclarative/blob/dev/src/qml/compiler/qv4ssa.cpp">BlockScheduler in qtdeclarative source code</a> for more detail
     */
    private static class BlockScheduler {
        private static class WorkForGroup {
            BasicBlock group;
            Stack<BasicBlock> postponed = new Stack<>();
            public WorkForGroup(BasicBlock group) {
                this.group = group;
            }
        }

        private Function func;
        private WorkForGroup currentGroup;
        private Stack<WorkForGroup> postponedGroups = new Stack<>();
        private List<BasicBlock> sequence = new ArrayList<>();
        private Set<BasicBlock> emitted = new HashSet<>();
        private Map<BasicBlock, BasicBlock> loopsStartEnd = new HashMap<>();


    }

    /** give instructions number */
    private void numberInstructions() {
        int id = 0;
        for (BasicBlock BB : blocksRPO) {
            for (IRInstruction inst = BB.getHead(); inst != null; inst = inst.getNext()) {
                InstructionInfo info = new InstructionInfo();
                info.id = id;
                instInfo.put(inst, info);
                id += 2; // see the paper for why +2
            }
        }
    }

    private LifeInterval getLifeInterval(VirtualRegister opd) {
        LifeInterval it = intervals.get(opd);
        if (it == null) {
            it = new LifeInterval();
            intervals.put(opd, it);
        }
        return it;
    }

    private void buildIntervals() {
        Set<VirtualRegister> live = new HashSet<>();
        for (int i = blocksRPO.size()-1; i >= 0; --i) {
            BasicBlock BB = blocksRPO.get(i);
            live.clear();

            for (BasicBlock succBB : BB.getSucc()) {
                BlockInfo info = blockInfo.get(succBB);
                if (info != null) live.addAll(info.liveIn);

                for (PhiInstruction phi : succBB.phi.values()) {
                    live.add(phi.paths.get(BB));
                }
            }

            for (VirtualRegister opd : live) {
                getLifeInterval(opd).addRange(instInfo.get(BB.getHead()).id, instInfo.get(BB.getLast()).id);
            }

            for (IRInstruction inst = BB.getLast(); inst != null; inst = inst.getPrev()) {
                VirtualRegister defined = inst.getDefinedRegister();
                if (defined != null) {
                    getLifeInterval(defined).setFrom(instInfo.get(inst).id);
                    live.remove(defined);
                }
                int from = instInfo.get(BB.getHead()).id;
                int to = instInfo.get(inst).id;
                for (VirtualRegister used : inst.getUsedRegister()) {
                    getLifeInterval(used).addRange(from, to);
                    live.add(used);
                }
            }

            BB.phi.values().forEach(x -> live.remove(x.dest));

            // TODO: Loop analysis & Basic Block Reschedule, refer to: https://github.com/qtproject/qtdeclarative/blob/dev/src/qml/compiler/qv4ssa.cpp
        }
    }
}
