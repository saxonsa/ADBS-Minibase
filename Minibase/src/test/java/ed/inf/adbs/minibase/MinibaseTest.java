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
    public void ProjectTaskTest() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/query3.txt", "data/evaluation/output/query3.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/query3.csv", "data/evaluation/expected_output/query3.csv"));
    }
}

