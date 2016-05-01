package com.abcdabcd987.compiler2016.IR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
public class IRRoot {
    public Map<String, Function> functions = new LinkedHashMap<>();
    public Map<String, StaticString> stringPool = new LinkedHashMap<>();
    public List<StaticData> dataList = new ArrayList<>();

    public void accept(IIRVisitor visitor) {
        visitor.visit(this);
    }
}
