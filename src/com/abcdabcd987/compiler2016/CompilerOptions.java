package com.abcdabcd987.compiler2016;

/**
 * Created by abcdabcd987 on 2016-04-11.
 */
public class CompilerOptions {
    private static final int SIZE_INT = 4;
    private static final int SIZE_BOOL = 4; // FIXME!
    private static final int SIZE_POINTER = 4;

    public static boolean ifPrintAST              = false;
    public static boolean ifPrintRawIR            = false;
    public static boolean ifPrintSSAIR            = false;
    public static boolean ifPrintIRAfterInline    = false;
    public static boolean enableInline            = true;
    public static boolean enableSSA               = true;
    public static boolean eliminateDeadCode       = true;
    public static boolean simpleConstantPropagate = true;
    public static String registerAllocator        = "color";

    public static int getSizeInt() {
        return SIZE_INT;
    }

    public static int getSizeBool() {
        return SIZE_BOOL;
    }

    public static int getSizePointer() {
        return SIZE_POINTER;
    }
}
