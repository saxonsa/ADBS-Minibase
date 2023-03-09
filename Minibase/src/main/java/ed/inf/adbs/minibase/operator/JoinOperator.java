package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.List;

public class JoinOperator extends Operator {
    private Operator leftChild;
    private Operator rightChild;
    private List<ComparisonAtom> predicates;

    public JoinOperator(Operator leftChild, Operator rightChild, List<ComparisonAtom> predicates) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.predicates = predicates;
    }

    @Override
    public Tuple getNextTuple() {
        // scan left firstly


        return null;
    }

    @Override
    public void reset() {

    }
}
