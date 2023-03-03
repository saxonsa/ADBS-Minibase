package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.ScanOperator;
import ed.inf.adbs.minibase.operator.common.Catalog;
import ed.inf.adbs.minibase.parser.QueryParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ScanOperatorTest {
    @Test
    public void testDump() {
        Catalog catalog = Catalog.getInstance();
        catalog.init("data/evaluation/db", "data/evaluation/input/query1.txt", "data/evaluation/output/query1.txt");
        Query query = QueryParser.parse("Q(x, y, z) :- R(x, y, z)");
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        System.out.println("test ra: " + ra);
        ScanOperator scanOperator = new ScanOperator(ra);
        scanOperator.dump();
        assertTrue(true);
    }
}
