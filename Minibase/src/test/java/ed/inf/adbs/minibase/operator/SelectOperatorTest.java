package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.Interpreter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SelectOperatorTest {
    private Catalog catalog = null;

    @Before
    public void setup() {
        catalog = Catalog.getCatalog();
    }

    @Test
    public void testSelectTask1() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/select_q1.txt", "data/evaluation/output/select_q1.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/select_q1.csv", "data/evaluation/expected_output/select_q1.csv"));
    }

    @Test
    public void testSelectTask2() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/select_q2.txt", "data/evaluation/output/select_q2.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/select_q2.csv", "data/evaluation/expected_output/select_q2.csv"));
    }

    @Test
    public void testSelectTask3() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/select_q3.txt", "data/evaluation/output/select_q3.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/select_q3.csv", "data/evaluation/expected_output/select_q3.csv"));
    }
}
