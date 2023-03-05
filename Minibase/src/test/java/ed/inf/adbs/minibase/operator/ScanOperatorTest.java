package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.common.ResultWriter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.operator.db.Tuple;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

public class ScanOperatorTest {
    private final Catalog catalog = Catalog.getCatalog();
    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

    @Test
    public void testDumpQuery1() throws IOException {
        Query query = QueryParser.parse("Q(x,y,z) :- R(x,y,z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        ResultWriter.init("data/evaluation/output/R.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        scanOperator.dump();
        boolean result = CSVComparator.areCSVFilesEqual("data/evaluation/output/R.csv", "data/evaluation/db/files/R.csv");
        assertTrue(result);
    }
    @Test
    public void testDumpQuery2() throws IOException {
        Query query = QueryParser.parse("Q(x,y,z) :- S(x, y, z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        ResultWriter.init("data/evaluation/output/S.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        scanOperator.dump();
        boolean result = CSVComparator.areCSVFilesEqual("data/evaluation/output/S.csv", "data/evaluation/db/files/S.csv");
        assertTrue(result);
    }
    @Test
    public void testGetNextTuple() throws IOException {
        Query query = QueryParser.parse("Q(x,y,z) :- R(x,y,z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        ResultWriter.init("data/evaluation/output/R.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        assertEquals(scanOperator.getNextTuple().toString(), "1, 9, 'adbs'");
        assertEquals(scanOperator.getNextTuple().toString(), "2, 7, 'anlp'");
        assertEquals(scanOperator.getNextTuple().toString(), "4, 2, 'ids'");
    }

    @Test
    public void testReset() throws IOException {
        Query query = QueryParser.parse("Q(x,y,z) :- R(x,y,z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        ResultWriter.init("data/evaluation/output/R.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        assertEquals(scanOperator.getNextTuple().toString(), "1, 9, 'adbs'");
        assertEquals(scanOperator.getNextTuple().toString(), "2, 7, 'anlp'");
        scanOperator.reset();
        assertEquals(scanOperator.getNextTuple().toString(), "1, 9, 'adbs'");
        assertEquals(scanOperator.getNextTuple().toString(), "2, 7, 'anlp'");
    }
}
