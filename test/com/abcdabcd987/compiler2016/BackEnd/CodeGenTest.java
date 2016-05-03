package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.Mill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
@RunWith(Parameterized.class)
public class CodeGenTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        for (File f : new File("testcase/ir/").listFiles()) {
            if (f.isFile() && f.getName().endsWith(".mx")) {
                params.add(new Object[] { "testcase/ir/" + f.getName() });
            }
        }
        return params;
    }

    private String filename;

    public CodeGenTest(String filename) {
        this.filename = filename;
    }

    @Test
    public void testPass() throws Exception {
        CompilerOptions.ifPrintAST        = false;
        CompilerOptions.ifPrintRawIR      = true;
        CompilerOptions.ifPrintSSAIR      = false;
        CompilerOptions.enableSSA         = false;
        CompilerOptions.registerAllocator = "no";

        System.out.println(filename);
        InputStream is = new FileInputStream(filename);
        new Mill(is, System.out).run();
    }
}
