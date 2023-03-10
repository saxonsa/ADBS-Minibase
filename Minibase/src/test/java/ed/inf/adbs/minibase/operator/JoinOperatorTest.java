package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.parser.QueryParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JoinOperatorTest {
    private final Catalog catalog = Catalog.getCatalog();

    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

    @Test
    public void testGetNextTuple() {
        Query query = QueryParser.parse("Q(x,y,z,u,v,w) :- R(x,y,z), S(u, v, w), x = u, z = 'adbs'");
        RelationalAtom leftRelationalAtom = (RelationalAtom) query.getBody().get(0);
        RelationalAtom rightRelationalAtom = (RelationalAtom) query.getBody().get(1);
        List<ComparisonAtom> predicates = query.getBody().stream()
                .filter(ComparisonAtom.class::isInstance)
                .map(ComparisonAtom.class::cast).collect(Collectors.toList());
        ScanOperator leftChild = new ScanOperator(leftRelationalAtom);
        ScanOperator rightChild = new ScanOperator(rightRelationalAtom);
        JoinOperator joinOperator = new JoinOperator(leftChild, rightChild, leftRelationalAtom, rightRelationalAtom,
                predicates);
        assertEquals(joinOperator.getNextTuple().toString(), "1, 9, 'adbs', 1, 'smith', 8");
        assertNull(joinOperator.getNextTuple());
    }

    @Test
    public void testReset() {
        Query query = QueryParser.parse("Q(x,y,z,u,v,w) :- R(x,y,z), S(u, v, w), x = u, z = 'adbs'");
        RelationalAtom leftRelationalAtom = (RelationalAtom) query.getBody().get(0);
        RelationalAtom rightRelationalAtom = (RelationalAtom) query.getBody().get(1);
        List<ComparisonAtom> predicates = query.getBody().stream()
                .filter(ComparisonAtom.class::isInstance)
                .map(ComparisonAtom.class::cast).collect(Collectors.toList());
        ScanOperator leftChild = new ScanOperator(leftRelationalAtom);
        ScanOperator rightChild = new ScanOperator(rightRelationalAtom);
        JoinOperator joinOperator = new JoinOperator(leftChild, rightChild, leftRelationalAtom, rightRelationalAtom,
                predicates);
        assertEquals(joinOperator.getNextTuple().toString(), "1, 9, 'adbs', 1, 'smith', 8");
        assertNull(joinOperator.getNextTuple());
        joinOperator.reset();
        assertEquals(joinOperator.getNextTuple().toString(), "1, 9, 'adbs', 1, 'smith', 8");
        assertNull(joinOperator.getNextTuple());
    }
}
