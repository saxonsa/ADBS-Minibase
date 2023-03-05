package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.Operator;
import ed.inf.adbs.minibase.operator.ScanOperator;

public class QueryPlan {
    private Operator root;
    private final Query query;

    public QueryPlan(Query query) {
        this.query = query;
        constructQueryTree();
    }

    private void constructQueryTree() {
        RelationalAtom ra = (RelationalAtom) query.getBody().get(0);
        root = new ScanOperator(ra);
    }

    public Operator getRoot() {
        return root;
    }

    public Query getQuery() {
        return query;
    }
}
