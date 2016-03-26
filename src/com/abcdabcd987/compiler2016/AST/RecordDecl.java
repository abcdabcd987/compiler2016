package com.abcdabcd987.compiler2016.AST;

import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class RecordDecl extends Decl {
    public final List<VariableDecl> members;
    public final Symbol name;

    public RecordDecl(Symbol name, List<VariableDecl> members) {
        this.name = name;
        this.members = members;
    }
}
