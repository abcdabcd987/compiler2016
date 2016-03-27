package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class RecordDecl extends Decl {
    public final List<VariableDecl> members;
    public final Symbol name;

    public static class Builder {
        private Symbol name;
        private List<VariableDecl> members = new ArrayList<>();

        public void add(Object node) {
            if (node instanceof VariableDecl) members.add((VariableDecl)node);
            else throw new RuntimeException("Invalid type");
        }

        public void setName(Symbol name) {
            this.name = name;
        }

        public RecordDecl build() {
            return new RecordDecl(name, members);
        }
    }

    public RecordDecl(Symbol name, List<VariableDecl> members) {
        this.name = name;
        this.members = members;
    }
}
