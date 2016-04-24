package com.abcdabcd987.compiler2016.BackEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-23.
 */
public class IRTextInterpreter {
    private static class Instruction {
        String operator;
        String dest;        // used as `cond` for `br`
        String op1;
        String op2;
        int size;           // for `load` / `store`
        List<String> args;  // for `call`

        int lineno;
        String text;
    }

    private static class BasicBlock {
        Function function;
        String name;
        List<Instruction> instructions = new ArrayList<>();
    }

    private static class Function {
        boolean hasReturnValue;
        String name;
        BasicBlock entry;
        List<String> args;
    }

    private Map<String, BasicBlock> blocks = new HashMap<>();
    private Map<String, Function> functions = new HashMap<>();
    private BasicBlock curBB = null;
    private Function curFunc = null;
    private Instruction curInst = null;

    //====== read text IR

    // instructions that have no destination
    static private final Set<String> opnames1 = new HashSet<>(Arrays.asList(
            "store", "br", "jump", "ret", "call"
    ));

    // instructions that have destination
    static private final Set<String> opnames2 = new HashSet<>(Arrays.asList(
            "load", "move", "alloc",
            "add", "sub", "mul", "div", "rem", "shl", "shr", "and", "or", "xor", "neg", "not",
            "slt", "sgt", "sle", "sge", "seq", "sne", "call"
    ));

    // instructions that have exactly 1 operand
    static private final Set<String> opnum1 = new HashSet<>(Arrays.asList(
            "ret", "jump", "move", "neg", "not", "alloc"
    ));

    private int lineno = 0;
    private String line;
    private BufferedReader br;

    private String readLine() throws IOException {
        do {
            line = br.readLine();
            if (line == null) break;
            ++lineno;
            line = line.trim();
        } while (line.equals(""));
        return line;
    }

    private List<String> splitBySpaces(String line) {
        return Arrays.asList(line.trim().split(" +"));
    }

    private void readInstruction() throws SemanticError {
        // basic block
        if (line.startsWith("%")) {
            if (!line.endsWith(":")) throw new SemanticError("expected a `:`");
            curBB = new BasicBlock();
            curBB.name = line.substring(0, line.length()-1);
            curBB.function = curFunc;
            if (blocks.containsKey(curBB.name)) throw new SemanticError("label `" + curBB.name + "` has already been defined");
            blocks.put(curBB.name, curBB);
            if (curFunc.entry == null) curFunc.entry = curBB;
            return;
        }

        // instruction
        String[] split = line.split("=");
        Instruction inst = new Instruction();
        List<String> words = splitBySpaces(split[split.length - 1]);
        inst.operator = words.get(0);
        if (split.length == 1) {
            if (!opnames1.contains(inst.operator)) throw new SemanticError("illegal operator " + inst.operator);
        } else if (split.length == 2) {
            if (!opnames2.contains(inst.operator)) throw new SemanticError("illegal operator " + inst.operator);
        } else {
            throw new SemanticError("illegal operator " + inst.operator);
        }
        inst.lineno = lineno;
        inst.text = line;
        curBB.instructions.add(inst);

        // save operands to variables
        switch (inst.operator) {
            case "store":
                inst.op1 = words.get(2);
                inst.op2 = words.get(3);
                inst.size = Integer.valueOf(words.get(1));
                return;
            case "load":
                inst.dest = split[0].trim();
                inst.op1 = words.get(2);
                inst.size = Integer.valueOf(words.get(1));
                return;
            case "alloc":
                inst.dest = split[0].trim();
                inst.op1 = words.get(1);
                return;
            case "call":
                if (split.length == 2) inst.dest = split[0].trim();
                inst.op1 = words.get(1);
                inst.args = words.subList(2, words.size());
                return;
            case "br":
                inst.dest = words.get(1);
                inst.op1 = words.get(2);
                inst.op2 = words.get(3);
                return;
            default:
                if (split.length == 2) inst.dest = split[0].trim();
                inst.op1 = words.get(1);
                if (opnum1.contains(inst.operator)) return;
                inst.op2 = words.get(2);
        }
    }

    private void readFunction() throws IOException, SemanticError {
        List<String> words = splitBySpaces(line);
        if (!words.get(words.size()-1).equals("{")) throw new SemanticError("expected a `{`");
        curFunc = new Function();
        curFunc.hasReturnValue = line.startsWith("func ");
        curFunc.name = words.get(1);
        curFunc.args = words.subList(2, words.size()-1);
        if (functions.containsKey(curFunc.name)) throw new SemanticError("function `" + curFunc.name + "` has already been defined");
        functions.put(curFunc.name, curFunc);

        while (!readLine().equals("}")) {
            readInstruction();
        }

    }

    //====== run IR

    private Map<String, Integer> registers;
    private Map<Integer, Byte> memory = new HashMap<>();
    private int heapTop = 0;
    private int retValue;
    private boolean ret;

    private final Set<String> opjump = new HashSet<>(Arrays.asList(
            "br", "jump", "ret"
    ));

    private byte memoryRead(int addr) throws RuntimeError {
        Byte data = memory.get(addr);
        if (data == null) throw new RuntimeError("memory read violation");
        return data;
    }

    private void memoryWrite(int addr, Byte value) throws RuntimeError {
        if (!memory.containsKey(addr)) throw new RuntimeError("memory write violation");
        memory.put(addr, value);
    }

    private int registerRead(String name) throws RuntimeError {
        Integer data = registers.get(name);
        if (data == null) throw new RuntimeError("register `" + name + "` haven't been defined yet");
        return data;
    }

    private void registerWrite(String name, Integer value) throws RuntimeError {
        if (!name.startsWith("$")) throw new RuntimeError("not a register");
        registers.put(name, value);
    }

    private int readSrc(String name) throws RuntimeError {
        if (name.startsWith("$")) return registerRead(name);
        return Integer.valueOf(name);
    }

    private void jump(String name) throws RuntimeError {
        BasicBlock BB = blocks.get(name);
        if (BB == null || BB.function != curBB.function)
            throw new RuntimeError("cannot resolve block `" + name + "` in function `" + curBB.function.name + "`");
        curBB = BB;
    }

    private void runInstruction() throws RuntimeError {
        switch (curInst.operator) {
            case "load":
                int addr = readSrc(curInst.op1);
                int res = 0;
                for (int i = 0; i < curInst.size; ++i) res = (res << 8) | memoryRead(addr+i);
                registerWrite(curInst.dest, res);
                return;

            case "store":
                int address = readSrc(curInst.op1);
                int data = readSrc(curInst.op2);
                for (int i = curInst.size-1; i >= 0; --i) {
                    memoryWrite(address+i, (byte)(data & 0xFF));
                    data >>= 8;
                }
                return;

            case "alloc":
                int size = readSrc(curInst.op1);
                registerWrite(curInst.dest, heapTop);
                for (int i = 0; i < size; ++i) memory.put(heapTop++, (byte)(Math.random() * 256));
                return;

            case "ret":
                retValue = readSrc(curInst.op1);
                ret = true;
                return;

            case "br":
                int cond = readSrc(curInst.dest);
                jump(cond == 0 ? curInst.op2 : curInst.op1);
                return;

            case "jump":
                jump(curInst.op1);
                return;

            case "call":
                Function func = functions.get(curInst.op1);
                if (func == null) throw new RuntimeError("cannot resolve function `" + curInst.op1 + "`");
                if (curInst.dest != null && !func.hasReturnValue) throw new RuntimeError("function `" + func.name + "` has not return value");
                Map<String, Integer> regs = new HashMap<>();
                if (curInst.args.size() != func.args.size()) throw new RuntimeError("argument size cannot match");
                for (int i = 0; i < curInst.args.size(); ++i) regs.put(func.args.get(i), readSrc(curInst.args.get(i)));

                Map<String, Integer> bakRegisters = registers;
                BasicBlock bakCurBB = curBB;
                Instruction bakCurInst = curInst;
                registers = regs;

                runFunction(func);

                ret = false;
                curInst = bakCurInst;
                curBB = bakCurBB;
                registers = bakRegisters;
                if (curInst.dest != null) registerWrite(curInst.dest, retValue);
                return;

            case "div":
                if (readSrc(curInst.op2) == 0) throw new RuntimeError("divide by zero");
                registerWrite(curInst.dest, readSrc(curInst.op1) / readSrc(curInst.op2));
                return;

            case "rem":
                if (readSrc(curInst.op2) == 0) throw new RuntimeError("mod by zero");
                registerWrite(curInst.dest, readSrc(curInst.op1) % readSrc(curInst.op2));
                return;

            case "move": registerWrite(curInst.dest, readSrc(curInst.op1)); return;
            case "neg": registerWrite(curInst.dest, - readSrc(curInst.op1)); return;
            case "not": registerWrite(curInst.dest, ~ readSrc(curInst.op1)); return;
            case "add": registerWrite(curInst.dest, readSrc(curInst.op1) + readSrc(curInst.op2)); return;
            case "sub": registerWrite(curInst.dest, readSrc(curInst.op1) - readSrc(curInst.op2)); return;
            case "mul": registerWrite(curInst.dest, readSrc(curInst.op1) * readSrc(curInst.op2)); return;
            case "shl": registerWrite(curInst.dest, readSrc(curInst.op1) << readSrc(curInst.op2)); return;
            case "shr": registerWrite(curInst.dest, readSrc(curInst.op1) >> readSrc(curInst.op2)); return;
            case "and": registerWrite(curInst.dest, readSrc(curInst.op1) & readSrc(curInst.op2)); return;
            case "or" : registerWrite(curInst.dest, readSrc(curInst.op1) | readSrc(curInst.op2)); return;
            case "xor": registerWrite(curInst.dest, readSrc(curInst.op1) ^ readSrc(curInst.op2)); return;
            case "slt": registerWrite(curInst.dest, readSrc(curInst.op1) < readSrc(curInst.op2) ? 1 : 0); return;
            case "sgt": registerWrite(curInst.dest, readSrc(curInst.op1) > readSrc(curInst.op2) ? 1 : 0); return;
            case "sle": registerWrite(curInst.dest, readSrc(curInst.op1) <=readSrc(curInst.op2) ? 1 : 0); return;
            case "sge": registerWrite(curInst.dest, readSrc(curInst.op1) >=readSrc(curInst.op2) ? 1 : 0); return;
            case "seq": registerWrite(curInst.dest, readSrc(curInst.op1) ==readSrc(curInst.op2) ? 1 : 0); return;
            case "sne": registerWrite(curInst.dest, readSrc(curInst.op1) !=readSrc(curInst.op2) ? 1 : 0); return;

            default: throw new RuntimeError("unknown operation `" + curInst.operator + "`");
        }
    }

    private void runFunction(Function func) throws RuntimeError {
        curBB = func.entry;
        if (curBB == null || curBB.function != func) throw new RuntimeError("no entry block for function `" + func.name + "`");

        while (true) {
            BasicBlock BB = curBB;
            if (!opjump.contains(BB.instructions.get(BB.instructions.size()-1).operator))
                throw new RuntimeError("block " + BB.name + " has no end instruction");

            for (Instruction inst : BB.instructions) {
                curInst = inst;
                runInstruction();
                if (ret) return;
                if (curBB != BB) break; // jumped
            }
        }
    }

    //====== Exceptions

    private class SemanticError extends Exception {
        SemanticError(String reason) {
            super(reason + " | line " + lineno + ": " + line);
        }
    }

    private class RuntimeError extends Exception {
        RuntimeError(String reason) {
            super(curInst != null ? reason + " | line " + curInst.lineno + ": " + curInst.text : reason);
        }
    }

    //====== public methods

    private int exitcode;
    private boolean exception;

    public IRTextInterpreter(InputStream in) throws IOException {
        try {
            br = new BufferedReader(new InputStreamReader(in));
            while (readLine() != null) {
                if (line.startsWith("func ") || line.startsWith("void ")) {
                    readFunction();
                }
            }
        } catch (SemanticError e) {
            System.err.println("Semantic Error");
            System.err.println("    " + e.getMessage());
            exitcode = -1;
            exception = true;
        }
    }

    public void run() {
        try {
            Function main = functions.get("main");
            if (main == null) throw new RuntimeError("cannot find `main` function");
            registers = new HashMap<>();
            runFunction(main);
            exitcode = retValue;
            exception = false;
        } catch (RuntimeError e) {
            System.err.println("Runtime Error");
            System.err.println("    " + e.getMessage());
            exitcode = -1;
            exception = true;
        }
    }

    public int getExitcode() {
        return exitcode;
    }

    public boolean exitException() {
        return exception;
    }
}
