package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.common.ComparisonEvaluator;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.ArrayList;
import java.util.List;

public class JoinOperator extends Operator {
    private final Operator leftChild;
    private final Operator rightChild;
    private final RelationalAtom leftChildRelationalAtom;
    private final RelationalAtom rightChildRelationalAtom;
    private final List<ComparisonAtom> predicates;

    public JoinOperator(Operator leftChild, Operator rightChild, RelationalAtom leftChildRelationalAtom,
                        RelationalAtom rightChildRelationalAtom, List<ComparisonAtom> predicates) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.leftChildRelationalAtom = leftChildRelationalAtom;
        this.rightChildRelationalAtom = rightChildRelationalAtom;
        this.predicates = predicates;
    }

    @Override
    public Tuple getNextTuple() {
        // extract join Condition and selection condition
        // check if two terms of a predicate come from two different relations
        List<ComparisonAtom> predicatesOnLeftChild = new ArrayList<>();
        List<ComparisonAtom> predicatesOnRightChild = new ArrayList<>();
        List<ComparisonAtom> predicatesOnJoin = new ArrayList<>();
        for (ComparisonAtom predicate : predicates) {
            Term term1 = predicate.getTerm1();
            Term term2 = predicate.getTerm2();
            if (term1 instanceof Constant) {
                if (term2 instanceof Constant) { // term1: Constant; term2: constant
                    predicatesOnLeftChild.add(predicate); // assume we test left firstly
                } else { // term1: Constant; term2: Variable
                    if (leftChildRelationalAtom.getTerms().contains(term2)) {
                        predicatesOnLeftChild.add(predicate);
                    }
                    if (rightChildRelationalAtom.getTerms().contains(term2)) {
                        predicatesOnRightChild.add(predicate);
                    }
                }
            } else {
                if (term2 instanceof Constant) { // term1: Variable; term2: Constant
                    if (leftChildRelationalAtom.getTerms().contains(term1)) {
                        predicatesOnLeftChild.add(predicate);
                    }
                    if (rightChildRelationalAtom.getTerms().contains(term1)) {
                        predicatesOnRightChild.add(predicate);
                    }
                } else { // term1: Variable; term2: Variable
                    if ((leftChildRelationalAtom.getTerms().contains(term1)) && (leftChildRelationalAtom.getTerms().contains(term2))) {
                        predicatesOnLeftChild.add(predicate);
                    } else if ((rightChildRelationalAtom.getTerms().contains(term1)) && (rightChildRelationalAtom.getTerms().contains(term2))) {
                        predicatesOnRightChild.add(predicate);
                    } else {
                        predicatesOnJoin.add(predicate);
                    }
                }
            }
        }


        // Simple nested loop join algorithm
        // for each outer tuple, scan the inner(right Child) tuple of the whole table iteratively
        Tuple outerTuple, innerTuple;
        while((outerTuple = leftChild.getNextTuple()) != null) {
            System.out.println("outerTuple: " + outerTuple);
            System.out.println("predicatesOnLeft: " + predicatesOnLeftChild);
            if (!checkTuplePassAllPredicates(outerTuple, predicatesOnLeftChild, leftChildRelationalAtom)) continue;

            while((innerTuple = rightChild.getNextTuple()) != null) {
                if (checkTuplePassAllPredicates(innerTuple, predicatesOnRightChild, rightChildRelationalAtom)) {
                    Tuple mergedTuple = new Tuple(outerTuple, innerTuple);
                    RelationalAtom mergedRelationalAtom = mergeRelationalAtom(leftChildRelationalAtom, rightChildRelationalAtom);
                    if (checkTuplePassAllPredicates(mergedTuple, predicatesOnJoin, mergedRelationalAtom)) {
                        return mergedTuple;
                    }
                }
            }

            // reset on rightChild
            rightChild.reset();
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

    private RelationalAtom mergeRelationalAtom(RelationalAtom ra1, RelationalAtom ra2) {
        List<Term> mergedTerms = new ArrayList<>();
        mergedTerms.addAll(ra1.getTerms());
        mergedTerms.addAll(ra2.getTerms());
        return new RelationalAtom(ra1.getName()+ " " + ra2.getName(), mergedTerms);
    }

}
