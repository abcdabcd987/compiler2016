package com.abcdabcd987.compiler2016.FrontEnd;

import com.abcdabcd987.compiler2016.AST.SourcePosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-03-31.
 */
public class CompilationError {
    private List<String> errors = new ArrayList<>();

    public void add(SourcePosition pos, String reason) {
        reason = pos + ": " + reason;
        errors.add(reason);
        throw new RuntimeException(reason);
    }
}
