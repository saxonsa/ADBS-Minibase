package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.common.ComparisonEvaluator;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SelectOperator extends Operator {
    private final Operator child;
    private final RelationalAtom relationalAtom;
    private final List<ComparisonAtom> predicates;
    private List<Tuple> reportedTuples;

    public SelectOperator(Operator child, RelationalAtom relationalAtom, List<ComparisonAtom> predicates) {
        this.child = child;
        this.relationalAtom = relationalAtom;
        this.predicates = predicates;
        this.reportedTuples = new ArrayList<>();
    }

    @Override
    public Tuple getNextTuple() {
        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {
            // check if current tuple satisfies all the predicates
            if (checkTuplePassAllPredicates(tuple)) {
                // remember we need to check the tuples that are after processed
                if (!reportedTuples.contains(tuple)) {
                    reportedTuples.add(tuple);
                    return tuple;
                }
            }
        }
        return null;
    }

    @Override
    public void reset() {
        child.reset();
    }

    /**
     * check if the given tuple could pass all the selection conditions in predicates
     * @param tuple A given tuple used to test if it fulfills the selective condition
     * @return Boolean result indicates the tuple could pass selective condition or not
     */
    private boolean checkTuplePassAllPredicates(Tuple tuple) {

        return predicates.stream().allMatch(predicate -> {
            ComparisonEvaluator comparisonEvaluator = new ComparisonEvaluator(tuple, predicate, relationalAtom);
            return comparisonEvaluator.check();
        });
    }
}
