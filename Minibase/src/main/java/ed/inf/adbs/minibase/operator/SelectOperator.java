package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.operator.common.SelectionEvaluator;
import ed.inf.adbs.minibase.operator.db.Tuple;
import jdk.nashorn.internal.runtime.regexp.joni.constants.StackPopLevel;

import java.util.List;

public class SelectOperator extends Operator {
    private final Operator child;
    private final RelationalAtom relationalAtom;
    private final List<ComparisonAtom> predicates;

    public SelectOperator(Operator child, RelationalAtom relationalAtom, List<ComparisonAtom> predicates) {
        this.child = child;
        this.relationalAtom = relationalAtom;
        this.predicates = predicates;
    }

    @Override
    public Tuple getNextTuple() {
        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {
            // check if current tuple satisfies all the predicates
            if (checkTuplePassAllPredicates(tuple)) {
                return tuple;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        child.reset();
    }

    private boolean checkTuplePassAllPredicates(Tuple tuple) {

        return predicates.stream().allMatch(predicate -> {
            System.out.println("predicates: " + predicate);
            SelectionEvaluator selectionEvaluator = new SelectionEvaluator(tuple, predicate, relationalAtom);
            return selectionEvaluator.check();
        });
    }
}
