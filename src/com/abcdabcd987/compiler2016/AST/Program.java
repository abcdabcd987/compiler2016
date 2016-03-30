package com.abcdabcd987.compiler2016.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-26.
 */
public class Program extends ASTNode {
    public final List<Decl> decls;

    public static class Builder {
        private List<Decl> decls = new ArrayList<>();

        public void add(Object node) {
            if (node instanceof Decl) decls.add((Decl)node);
            else if (node instanceof List) ((List) node).stream().forEachOrdered(this::add);
            else throw new RuntimeException("Invalid type.");
        }

        public Program build() {
            return new Program(decls);
        }
    }

    public Program(List<Decl> decls) {
        this.decls = decls;
    }

    @Override
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
    }
}
