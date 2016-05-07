package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.AST.ArrayTypeNode;
import com.abcdabcd987.compiler2016.AST.StructTypeNode;
import com.abcdabcd987.compiler2016.AST.TypeNode;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class GlobalSymbolTable {
    // Builtin type
    public final static PrimitiveType intType = new PrimitiveType(Type.Types.INT);
    public final static PrimitiveType boolType = new PrimitiveType(Type.Types.BOOL);
    public final static PrimitiveType voidType = new PrimitiveType(Type.Types.VOID);
    public final static PrimitiveType nullType = new PrimitiveType(Type.Types.NULL);
    public final static PrimitiveType stringType = new PrimitiveType(Type.Types.STRING);

    // Builtin string function
    public final static FunctionType stringLength = new FunctionType(intType, "string.length") {{
        addArg("this", stringType);
    }};
    public final static FunctionType stringSubString = new FunctionType(stringType, "string.substring") {{
        addArg("this", stringType);
        addArg("arg0", intType);
        addArg("arg1", intType);
    }};
    public final static FunctionType stringParseInt = new FunctionType(intType, "string.parseInt") {{
        addArg("this", stringType);
    }};
    public final static FunctionType stringOrd = new FunctionType(intType, "string.ord") {{
        addArg("this", stringType);
        addArg("arg0", intType);
    }};
    public final static Map<String, FunctionType> stringBuiltinMethods = Collections.unmodifiableMap(new HashMap<String, FunctionType>() {{
        put("length", stringLength);
        put("substring", stringSubString);
        put("parseInt", stringParseInt);
        put("ord", stringOrd);
    }});

    // Builtin array function
    public final static FunctionType arraySize = new FunctionType(intType, "array.size") {{
        addArg("this", null);
    }};
    public final static Map<String, FunctionType> arrayBuiltinMethods = Collections.unmodifiableMap(new HashMap<String, FunctionType>() {{
        put("size", arraySize);
    }});

    // Builtin function
    public final static FunctionType printStringFunc = new FunctionType(voidType, "print") {{
        addArg("arg0", stringType);
    }};
    public final static FunctionType printlnStringFunc = new FunctionType(voidType, "println") {{
        addArg("arg0", stringType);
    }};
    public final static FunctionType printIntFunc = new FunctionType(voidType, "printInt") {{
        addArg("arg0", intType);
    }};
    public final static FunctionType printlnIntFunc = new FunctionType(voidType, "printlnInt") {{
        addArg("arg0", intType);
    }};
    public final static FunctionType getStringFunc = new FunctionType(stringType, "getString");
    public final static FunctionType getIntFunc = new FunctionType(intType, "getInt");
    public final static FunctionType toStringFunc = new FunctionType(stringType, "toString") {{
        addArg("arg0", intType);
    }};
    public final static FunctionType stringConcatFunc = new FunctionType(stringType, "stringConcat") {{
        addArg("arg0", stringType);
        addArg("arg1", stringType);
    }};
    public final static FunctionType stringLess = new FunctionType(stringType, "stringLess") {{
        addArg("arg0", stringType);
        addArg("arg1", stringType);
    }};
    public final static FunctionType stringEqual = new FunctionType(stringType, "stringEqual") {{
        addArg("arg0", stringType);
        addArg("arg1", stringType);
    }};
    public final static Map<String, FunctionType> builtinMethods = Collections.unmodifiableMap(new HashMap<String, FunctionType>() {{
        put(stringConcatFunc.name, stringConcatFunc);
        put(stringLess.name, stringLess);
        put(stringEqual.name, stringEqual);

        put(printStringFunc.name, printStringFunc);
        put(printlnStringFunc.name, printlnStringFunc);
        put(printIntFunc.name, printIntFunc);
        put(printlnIntFunc.name, printlnIntFunc);
        put(getStringFunc.name, getStringFunc);
        put(getIntFunc.name, getIntFunc);
        put(toStringFunc.name, toStringFunc);
    }});
    private final static Set<FunctionType> builtinMethodSet = Collections.unmodifiableSet(new HashSet<FunctionType>() {{
        add(stringLength);
        add(stringSubString);
        add(stringParseInt);
        add(stringOrd);

        add(arraySize);

        add(stringConcatFunc);
        add(stringLess);
        add(stringEqual);

        add(printStringFunc);
        add(printlnStringFunc);
        add(printIntFunc);
        add(printlnIntFunc);
        add(getStringFunc);
        add(getIntFunc);
        add(toStringFunc);
    }});

    public static boolean isBuiltinMethod(FunctionType functionType) {
        return builtinMethodSet.contains(functionType);
    }


    private Map<String, Type> typeMap = new LinkedHashMap<>();

    public SymbolTable globals = new SymbolTable(null);

    public GlobalSymbolTable() {
        typeMap.put("int", intType);
        typeMap.put("bool", boolType);
        typeMap.put("void", voidType);
        typeMap.put("null", nullType);
        typeMap.put("string", stringType);

        builtinMethods.forEach(globals::define);
    }

    public void defineType(String name, Type type) {
        typeMap.put(name, type);
    }

    public Type resolveType(String name) {
        return typeMap.get(name);
    }

    public VariableType resolveVariableTypeFromAST(TypeNode node) {
        switch (node.getType()) {
            case INT: return intType;
            case BOOL: return boolType;
            case STRING: return stringType;
            case VOID: return voidType;
            case ARRAY:
                ArrayTypeNode t = (ArrayTypeNode) node;
                VariableType baseType = resolveVariableTypeFromAST(t.baseType);
                if (baseType != null) return new ArrayType(baseType);
                return null;
            case STRUCT:
                Type s = resolveType(((StructTypeNode) node).name);
                if (s != null) return (StructType) s;
                return null;
            default: return null;
        }
    }

    public String toStructureString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------GLOBAL TYPES:\n");
        typeMap.entrySet().stream().forEachOrdered(x ->
                sb.append(x.getKey()).append(": \n").append(x.getValue().toStructureString("  "))
        );
        sb.append("\n------GLOBAL SYMBOL TABLE:\n");
        sb.append(globals.toStructureString(""));
        return sb.toString();
    }
}
