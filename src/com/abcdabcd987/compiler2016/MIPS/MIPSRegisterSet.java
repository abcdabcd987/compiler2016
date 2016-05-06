package com.abcdabcd987.compiler2016.MIPS;

import com.abcdabcd987.compiler2016.IR.PhysicalRegister;

import java.util.*;

/**
 * Created by abcdabcd987 on 2016-04-30.
 */
public class MIPSRegisterSet {
    //                                                      id,  name,   gr  ,  er  ,  ee
    public static final MIPSRegister ZERO = new MIPSRegister(0,   "$0", false, false, false);
    public static final MIPSRegister AT   = new MIPSRegister(1,  "$at", false, false, false);
    public static final MIPSRegister V0   = new MIPSRegister(2,  "$v0", false, false, false);
    public static final MIPSRegister V1   = new MIPSRegister(3,  "$v1", true , true , false);
    public static final MIPSRegister A0   = new MIPSRegister(4,  "$a0", false, false, false);
    public static final MIPSRegister A1   = new MIPSRegister(5,  "$a1", false, false, false);
    public static final MIPSRegister A2   = new MIPSRegister(6,  "$a2", false, false, false);
    public static final MIPSRegister A3   = new MIPSRegister(7,  "$a3", false, false, false);
    public static final MIPSRegister T0   = new MIPSRegister(8,  "$t0", true , true , false);
    public static final MIPSRegister T1   = new MIPSRegister(9,  "$t1", true , true , false);
    public static final MIPSRegister T2   = new MIPSRegister(10, "$t2", true , true , false);
    public static final MIPSRegister T3   = new MIPSRegister(11, "$t3", true , true , false);
    public static final MIPSRegister T4   = new MIPSRegister(12, "$t4", true , true , false);
    public static final MIPSRegister T5   = new MIPSRegister(13, "$t5", true , true , false);
    public static final MIPSRegister T6   = new MIPSRegister(14, "$t6", true , true , false);
    public static final MIPSRegister T7   = new MIPSRegister(15, "$t7", true , true , false);
    public static final MIPSRegister T8   = new MIPSRegister(24, "$t8", true , true , false);
    public static final MIPSRegister T9   = new MIPSRegister(25, "$t9", true , true , false);
    public static final MIPSRegister S0   = new MIPSRegister(16, "$s0", true , false, true );
    public static final MIPSRegister S1   = new MIPSRegister(17, "$s1", true , false, true );
    public static final MIPSRegister S2   = new MIPSRegister(18, "$s2", true , false, true );
    public static final MIPSRegister S3   = new MIPSRegister(19, "$s3", true , false, true );
    public static final MIPSRegister S4   = new MIPSRegister(20, "$s4", true , false, true );
    public static final MIPSRegister S5   = new MIPSRegister(21, "$s5", true , false, true );
    public static final MIPSRegister S6   = new MIPSRegister(22, "$s6", true , false, true );
    public static final MIPSRegister S7   = new MIPSRegister(23, "$s7", true , false, true );
    public static final MIPSRegister K0   = new MIPSRegister(26, "$k0", false, false, false);
    public static final MIPSRegister K1   = new MIPSRegister(27, "$k1", false, false, false);
    public static final MIPSRegister GP   = new MIPSRegister(28, "$gp", false, false, false);
    public static final MIPSRegister SP   = new MIPSRegister(29, "$sp", false, false, false);
    public static final MIPSRegister FP   = new MIPSRegister(30, "$fp", true , true , false);
    public static final MIPSRegister RA   = new MIPSRegister(31, "$ra", false, false, false);

    public static final Collection<PhysicalRegister> all;
    public static final Collection<PhysicalRegister> general;
    public static final Collection<PhysicalRegister> callerSave;
    public static final Collection<PhysicalRegister> calleeSave;

    static {
        Collection<MIPSRegister> tmp_all        = new ArrayList<>();
        Collection<MIPSRegister> tmp_general    = new ArrayList<>();
        Collection<MIPSRegister> tmp_callerSave = new ArrayList<>();
        Collection<MIPSRegister> tmp_calleeSave = new ArrayList<>();

        tmp_all.add(ZERO);
        tmp_all.add(AT);
        tmp_all.add(V0);
        tmp_all.add(V1);
        tmp_all.add(A0);
        tmp_all.add(A1);
        tmp_all.add(A2);
        tmp_all.add(A3);
        tmp_all.add(T0);
        tmp_all.add(T1);
        tmp_all.add(T2);
        tmp_all.add(T3);
        tmp_all.add(T4);
        tmp_all.add(T5);
        tmp_all.add(T6);
        tmp_all.add(T7);
        tmp_all.add(T8);
        tmp_all.add(T9);
        tmp_all.add(S0);
        tmp_all.add(S1);
        tmp_all.add(S2);
        tmp_all.add(S3);
        tmp_all.add(S4);
        tmp_all.add(S5);
        tmp_all.add(S6);
        tmp_all.add(S7);
        tmp_all.add(K0);
        tmp_all.add(K1);
        tmp_all.add(GP);
        tmp_all.add(SP);
        tmp_all.add(FP);
        tmp_all.add(RA);

        tmp_all.stream().filter(MIPSRegister::isGeneral).forEach(tmp_general::add);
        tmp_all.stream().filter(MIPSRegister::isCallerSave).forEach(tmp_callerSave::add);
        tmp_all.stream().filter(MIPSRegister::isCalleeSave).forEach(tmp_calleeSave::add);

        all        = Collections.unmodifiableCollection(tmp_all);
        general    = Collections.unmodifiableCollection(tmp_general);
        callerSave = Collections.unmodifiableCollection(tmp_callerSave);
        calleeSave = Collections.unmodifiableCollection(tmp_calleeSave);
    }
}
