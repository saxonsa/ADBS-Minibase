package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.function.Predicate;

public class ComparisonEvaluator {
    private final Tuple tuple;
    private final ComparisonAtom predicate;
    private final RelationalAtom relationalAtom;

    public ComparisonEvaluator(Tuple tuple, ComparisonAtom predicate, RelationalAtom relationalAtom) {
        this.tuple = tuple;
        this.predicate = predicate;
        this.relationalAtom = relationalAtom;
    }

    public boolean check() {
        Term term1 = predicate.getTerm1();
        Term term2 = predicate.getTerm2();
        ComparisonOperator op = predicate.getOp();

        Constant constTerm1 = term1 instanceof Variable ?
                tuple.getAttributes().get(relationalAtom.getTerms().indexOf(term1)) : (Constant) term1;

        Constant constTerm2 = term2 instanceof Variable ?
                tuple.getAttributes().get(relationalAtom.getTerms().indexOf(term2)) : (Constant) term2;

        if (constTerm1 instanceof IntegerConstant) {
            int value1 = ((IntegerConstant) constTerm1).getValue();
            int value2 = ((IntegerConstant) constTerm2).getValue();
            switch (op) {
                case EQ:
                    return value1 == value2;
                case NEQ:
                    return value1 != value2;
                case GT:
                    return value1 > value2;
                case GEQ:
                    return value1 >= value2;
                case LT:
                    return value1 < value2;
                case LEQ:
                    return value1 <= value2;
            }
        } else {
            String s1 = constTerm1.toString();
            String s2 = constTerm2.toString();
            switch (op) {
                case EQ:
                    return s1.equals(s2);
                case NEQ:
                    return !s1.equals(s2);
                case GT:
                    return s1.compareTo(s2) > 0;
                case GEQ:
                    return s1.compareTo(s2) >= 0;
                case LT:
                    return s1.compareTo(s2) < 0;
                case LEQ:
                    return s1.compareTo(s2) <= 0;
            }
        }

        return false;
    }

}
