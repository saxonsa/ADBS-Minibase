package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.Operator;
import ed.inf.adbs.minibase.operator.ScanOperator;
import ed.inf.adbs.minibase.operator.SelectOperator;

import java.util.List;
import java.util.stream.Collectors;

public class QueryPlan {
    private Operator root;
    private final Query query;

    public QueryPlan(Query query) {
        this.query = query;
        constructQueryTree();
    }

    private void constructQueryTree() {
        List<RelationalAtom> relationalAtoms = query.getBody().stream()
                .filter(RelationalAtom.class::isInstance)
                .map(RelationalAtom.class::cast).collect(Collectors.toList());
        root = new ScanOperator(relationalAtoms.get(0));

        // get all the predicates
        List<ComparisonAtom> comparisonAtoms = query.getBody().stream()
                        .filter(ComparisonAtom.class::isInstance)
                                .map(ComparisonAtom.class::cast).collect(Collectors.toList());
        if (comparisonAtoms.size() > 0) {
            root = new SelectOperator(root, relationalAtoms.get(0), comparisonAtoms);
        }
    }

    public Operator getRoot() {
        return root;
    }

    public Query getQuery() {
        return query;
    }
}
