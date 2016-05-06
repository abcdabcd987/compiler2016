package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.IR.*;

/**
 * Register Allocator Base Class
 * Target dependent register information should be injected in advance.
 * Before allocation, registers can be {@link VirtualRegister} and {@link IntImmediate}.
 * After allocation, registers in (almost) all {@link IRInstruction} are {@link PhysicalRegister} and {@link IntImmediate},
 * except that address field in {@link Load#address}/{@link Store#address} can be {@link StackSlot}.
 * Created by abcdabcd987 on 2016-05-02.
 */
public abstract class RegisterAllocator {
    public abstract void run();
}
