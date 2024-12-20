package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Query;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CQMinimizerTest {

    @Test
    public void testCQMinimizer() {
        File directory = new File("data/minimization/input");
        File[] files = directory.listFiles();
        assert files != null;

        // iterate all the files inside input/ folder and run CQMinimization
        // and finally compare it with the corresponding one inside expected output folder
        for (File file : files) {
            String inputPath = file.getPath();
            String outputPath = inputPath.replaceAll("input", "output");

            // perform CQ
            CQMinimizer.minimizeCQ(inputPath, outputPath);

            // compare the file content of output with expected output through "query form"
            String expectedOutputPath = inputPath.replaceAll("input", "expected_output");

            // transform to query and then compare the string generated by built-in toString
            // to be tolerant towards the format of txt file (such as space and empty line)
            Query actualOutputQuery = CQMinimizer.parsingQuery(outputPath);
            Query expectedOutputQuery = CQMinimizer.parsingQuery(expectedOutputPath);
            assertEquals(actualOutputQuery.toString(), expectedOutputQuery.toString());

            // Mock test: delete the output file
            File outputFile = new File(outputPath);
            assertTrue(outputFile.delete());
        }
    }
}
