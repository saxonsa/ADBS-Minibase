package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.Interpreter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ProjectOperatorTest {
    private final Catalog catalog = Catalog.getCatalog();

    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

    @Test
    public void testProjectTask1() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/project_q1.txt", "data/evaluation/output/project_q1.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/project_q1.csv", "data/evaluation/expected_output/project_q1.csv"));
    }

    @Test
    public void testProjectTask2() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/project_q2.txt", "data/evaluation/output/project_q2.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/project_q2.csv", "data/evaluation/expected_output/project_q2.csv"));
    }

    @Test
    public void testProjectTask3() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/project_q3.txt", "data/evaluation/output/project_q3.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/project_q3.csv", "data/evaluation/expected_output/project_q3.csv"));
    }

    @Test
    public void testProjectTask4() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/project_q4.txt", "data/evaluation/output/project_q4.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/project_q4.csv", "data/evaluation/expected_output/project_q4.csv"));
    }
}
