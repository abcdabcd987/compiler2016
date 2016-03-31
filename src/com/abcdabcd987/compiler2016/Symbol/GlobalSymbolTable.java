package com.abcdabcd987.compiler2016.Symbol;

import com.abcdabcd987.compiler2016.AST.ArrayTypeNode;
import com.abcdabcd987.compiler2016.AST.StructTypeNode;
import com.abcdabcd987.compiler2016.AST.TypeNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class GlobalSymbolTable {
    public final static PrimitiveType intType = new PrimitiveType(Type.Types.INT);
    public final static PrimitiveType boolType = new PrimitiveType(Type.Types.BOOL);
    public final static PrimitiveType voidType = new PrimitiveType(Type.Types.VOID);
    public final static PrimitiveType stringType = new PrimitiveType(Type.Types.STRING);

    private Map<String, Type> typeMap = new LinkedHashMap<>();

    public SymbolTable globals = new SymbolTable();

    public GlobalSymbolTable() {
        typeMap.put("int", intType);
        typeMap.put("bool", boolType);
        typeMap.put("void", voidType);
        typeMap.put("string", stringType);
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
                sb.append(x.getKey() + ": \n").append(x.getValue().toStructureString("  "))
        );
        sb.append("\n------GLOBAL SYMBOL TABLE:\n");
        sb.append(globals.toStructureString(""));
        return sb.toString();
    }
}
