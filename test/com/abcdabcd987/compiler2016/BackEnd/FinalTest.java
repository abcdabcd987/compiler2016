package com.abcdabcd987.compiler2016.BackEnd;

import com.abcdabcd987.compiler2016.CompilerOptions;
import com.abcdabcd987.compiler2016.Mill;
import com.abcdabcd987.compiler2016.Utility.TeeOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by abcdabcd987 on 2016-04-13.
 */
@RunWith(Parameterized.class)
public class FinalTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final String dataRoot = "testcase/final/";
        final String srcExt = ".mx";
        final String inExt = ".in";
        final String ansExt = ".out";
        SortedSet<String> files = new TreeSet<>(Arrays.stream(new File(dataRoot).listFiles())
                .filter(File::isFile).map(x -> dataRoot + x.getName()).collect(Collectors.toSet()));
        Collection<Object[]> params = new ArrayList<>();
        for (String file : files) {
            if (!file.endsWith(srcExt)) continue;
            String name = file.substring(0, file.length()-srcExt.length());
            String in = files.contains(name + inExt) ? name + inExt : null;
            String ans = files.contains(name + ansExt) ? name + ansExt : null;
            params.add(new Object[] { file, in, ans });
        }
        return params;
    }

    private String srcFile;
    private String inFile;
    private String ansFile;

    public FinalTest(String srcFile, String inFile, String ansFile) {
        this.inFile = inFile;
        this.ansFile = ansFile;
        this.srcFile = srcFile;
    }

    @Test
    public void testPass() throws Exception {
        System.out.println("# " + srcFile);
        System.out.flush();
        if (ansFile == null) throw new RuntimeException("no ans file");

        final String outputPath           = "./out/out.s";
        final boolean runSPIM             = true;
        final boolean printToStdout       = false;
        CompilerOptions.ifPrintAST        = false;
        CompilerOptions.ifPrintRawIR      = false;
        CompilerOptions.ifPrintSSAIR      = false;
        CompilerOptions.enableSSA         = false;
        CompilerOptions.registerAllocator = "no";

        TeeOutputStream tee = new TeeOutputStream();
        OutputStream fileOut = new FileOutputStream(outputPath);
        PrintStream out = new PrintStream(tee);
        if (printToStdout) tee.add(System.out);
        tee.add(fileOut);

        InputStream is = new FileInputStream(srcFile);
        new Mill(is, out).run();

        out.flush();
        fileOut.close();

        if (runSPIM) {
            Process spim = new ProcessBuilder("./lib/statspim/usr/bin/spim", "-lstack", "33554432", "-ldata", "33554432", "-stat", "-file", outputPath)
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start();
            if (inFile != null) {
                BufferedReader br = new BufferedReader(new FileReader(inFile));
                PrintStream spimOut = new PrintStream(spim.getOutputStream());
                String line;
                while ((line = br.readLine()) != null)
                    spimOut.println(line);
                spimOut.close();
            }
            spim.waitFor();

            BufferedReader brAns = new BufferedReader(new FileReader(ansFile));
            BufferedReader brOut = new BufferedReader(new InputStreamReader(spim.getInputStream()));
            boolean correct = true;
            System.out.println("PROGRAM OUTPUT:");
            while (true) {
                String lineOut = brOut.readLine();
                String lineAns = brAns.readLine();
                if ((lineOut == null) != (lineAns == null)) correct = false;
                if (lineOut == null) break;
                System.out.println(lineOut);
                if (correct && !lineAns.trim().equals(lineOut.trim())) {
                    correct = false;
                    System.out.println("========== ANS OUTPUT: " + lineAns);
                }
            }


            BufferedReader brErr = new BufferedReader(new InputStreamReader(spim.getErrorStream()));
            System.out.println("STDERR:");
            String line;
            while ((line = brErr.readLine()) != null) System.out.println(line);
            assertTrue(correct);
        }
    }
}
