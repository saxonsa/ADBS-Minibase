package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.common.ComparisonEvaluator;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.List;

public class JoinOperator extends Operator {
    private final Operator leftChild;
    private final Operator rightChild;
    private final RelationalAtom mergedRelationalAtom;
    private final List<ComparisonAtom> predicates;
    private Tuple leftTuple;
    private Tuple rightTuple;

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

            if (checkTuplePassAllPredicates(mergedTuple)) {
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

    private boolean checkTuplePassAllPredicates(Tuple tuple) {

        return predicates.stream().allMatch(predicate -> {
            ComparisonEvaluator comparisonEvaluator = new ComparisonEvaluator(tuple, predicate, mergedRelationalAtom);
            return comparisonEvaluator.check();
        });
    }
}
