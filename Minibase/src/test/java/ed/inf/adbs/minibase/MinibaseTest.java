package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.operator.common.Interpreter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for Minibase.
 */

public class MinibaseTest {
    private final Catalog catalog = Catalog.getCatalog();

    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

    @Test
    public void ScanTaskTest() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query1.txt", "data/evaluation/output/query1.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query1.csv", "data/evaluation/expected_output/query1.csv"));
    }

    @Test
    public void SelectTaskTest() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query2.txt", "data/evaluation/output/query2.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query2.csv", "data/evaluation/expected_output/query2.csv"));
    }

    @Test
    public void ProjectTaskTestQ3() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query3.txt", "data/evaluation/output/query3.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query3.csv", "data/evaluation/expected_output/query3.csv"));
    }

    @Test
    public void ProjectTaskTestQ4() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query4.txt", "data/evaluation/output/query4.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query4.csv", "data/evaluation/expected_output/query4.csv"));
    }

    @Test
    public void ProjectTaskTestQ5() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query5.txt", "data/evaluation/output/query5.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query5.csv", "data/evaluation/expected_output/query5.csv"));
    }

    @Test
    public void ProjectTaskTestQ6() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query6.txt", "data/evaluation/output/query6.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query6.csv", "data/evaluation/expected_output/query6.csv"));
    }

    @Test
    public void ProjectTaskTestQ7() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query7.txt", "data/evaluation/output/query7.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query7.csv", "data/evaluation/expected_output/query7.csv"));
    }

    @Test
    public void ProjectTaskTestQ8() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query8.txt", "data/evaluation/output/query8.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query8.csv", "data/evaluation/expected_output/query8.csv"));
    }

    @Test
    public void ProjectTaskTestQ9() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query9.txt", "data/evaluation/output/query9.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query9.csv", "data/evaluation/expected_output/query9.csv"));
    }
}

