package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.Interpreter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SumOperatorTest {
    private final Catalog catalog = Catalog.getCatalog();

    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

    @Test
    public void testSumOperatorQ1() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/sum_q1.txt", "data/evaluation/output/sum_q1.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/sum_q1.csv", "data/evaluation/expected_output/sum_q1.csv"));
    }

    @Test
    public void testSumOperatorQ2() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/sum_q2.txt", "data/evaluation/output/sum_q2.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/sum_q2.csv", "data/evaluation/expected_output/sum_q2.csv"));
    }

    @Test
    public void testSumOperatorQ3() throws IOException {
        Interpreter interpreter = new Interpreter("data/evaluation/input/sum_q3.txt", "data/evaluation/output/sum_q3.csv");
        interpreter.dump();
        assertTrue(CSVComparator.areCSVFilesEqual("data/evaluation/output/sum_q3.csv", "data/evaluation/expected_output/sum_q3.csv"));
    }
}
