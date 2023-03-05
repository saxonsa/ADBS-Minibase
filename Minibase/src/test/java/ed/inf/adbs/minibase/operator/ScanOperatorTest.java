package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.common.ResultWriter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.utils.CSVComparator;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

public class ScanOperatorTest {

    @Test
    public void testDumpQuery1() throws IOException {
        Catalog catalog = Catalog.getCatalog();
        catalog.init("data/evaluation/db");
        Query query = QueryParser.parse(Paths.get("data/evaluation/input/query1.txt"));
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        System.out.println("test ra: " + ra);
        ResultWriter.initialiseOutputWriter("data/evaluation/output/query1.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        scanOperator.dump();
        boolean result = CSVComparator.areCSVFilesEqual("data/evaluation/output/query1.csv", "data/evaluation/expected_output/query1.csv");
        assertTrue(result);
    }
    @Test
    public void testDumpQuery2() throws IOException {
        Catalog catalog = Catalog.getCatalog();
        catalog.init("data/evaluation/db");
        Query query = QueryParser.parse("Q(x,y,z) :- S(x, y, z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        ResultWriter.initialiseOutputWriter("data/evaluation/output/S.csv");
        ScanOperator scanOperator = new ScanOperator(ra);
        scanOperator.dump();
        boolean result = CSVComparator.areCSVFilesEqual("data/evaluation/output/S.csv", "data/evaluation/db/files/S.csv");
        assertTrue(result);
    }
}
