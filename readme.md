# Compiler 2016

This is a toy compiler for the course [Compiler 2016](http://acm.sjtu.edu.cn/wiki/Compiler_2016) at ACM Class, SJTU. **The source language is [Mx\*](http://acm.sjtu.edu.cn/w/images/9/93/Mx_language_manual.pdf). The target is MIPS assembly (in SPIM format).**

You can refer to [my presentation slides](https://github.com/abcdabcd987/compiler2016/blob/master/design/presentation.pdf) to know something about this compiler and also what I've learnt during the course.

When debugging this compiler, I wrote another project [LLIRInterpreter](https://github.com/abcdabcd987/LLIRInterpreter) which reads text IR and does interpretation.

## Feature (what you can found in the source code)

- SSA Transform
  - Construction
  - Destruction
  - Naive dead code elimination
  - Simple constant propagation
  - Copy elimination
- Register Allocation
  - Local bottom-up allocator
  - Interference graph coloring allocator
- Function inlining

## Usage

```
$ java com.abcdabcd987.compiler2016.Mill --help
Mill - Mx* language implementation made with love by abcdabcd987
Usage: mill [options] [input]
Options:
  -help              Print this help message
  -o <file>          Write output to <file>
  -reg-alloc <val>   Set register allocator to <val>
                     Available register allocators:
                       no:    Don't allocate at all. (CISC-like)
                       local: Local bottom-up allocator
                       color: Global allocation by interference graph coloring
  -print-ast         Print the abstract semantic tree
  -print-ir          Print the intermediate representation
  -print-ssa-ir      Print the intermediate representation after SSA transforms
  -no-inline         Disable function inlining
  -no-ssa            Disable single static assignment analysis and transforms
  -no-naive-dce      Disable naive dead code elimination
  -no-scp            Disable simple constant propagate
```

