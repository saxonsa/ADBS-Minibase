package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.common.ComparisonEvaluator;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.List;

public class JoinOperator extends Operator {
    private final Operator leftChild;
    private final Operator rightChild;
    private RelationalAtom mergedRelationalAtom = null;
    private final List<ComparisonAtom> predicates;
    private Tuple leftTuple = null;
    private Tuple rightTuple = null;

    public JoinOperator(Operator leftChild, Operator rightChild,
                        RelationalAtom mergedRelationalAtom, List<ComparisonAtom> predicates) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.predicates = predicates;
        this.mergedRelationalAtom = mergedRelationalAtom;
        this.leftTuple = this.leftChild.getNextTuple();
        this.rightTuple = this.rightChild.getNextTuple();
    }

    @Override
    public Tuple getNextTuple() {

        // Simple nested loop join algorithm
        // for each outer tuple, scan the inner(right Child) tuple of the whole table iteratively
        while(leftTuple != null) {
            Tuple mergedTuple = new Tuple(leftTuple, rightTuple);

            if ((rightTuple = rightChild.getNextTuple()) == null) {
                rightChild.reset();
                leftTuple = leftChild.getNextTuple();
                rightTuple = rightChild.getNextTuple();
            }

            if (checkTuplePassAllPredicates(mergedTuple, predicates, mergedRelationalAtom)) {
                return mergedTuple;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        leftChild.reset();
        rightChild.reset();
    }

    private boolean checkTuplePassAllPredicates(Tuple tuple, List<ComparisonAtom> conditions, RelationalAtom relationalAtom) {

        return conditions.stream().allMatch(predicate -> {
            ComparisonEvaluator comparisonEvaluator = new ComparisonEvaluator(tuple, predicate, relationalAtom);
            return comparisonEvaluator.checkSelectionCondition();
        });
    }
}
