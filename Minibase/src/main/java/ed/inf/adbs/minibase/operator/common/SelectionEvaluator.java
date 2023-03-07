package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.HashMap;

public class SelectionEvaluator {
    private final ComparisonAtom predicate;
    private final RelationalAtom relationalAtom;
    private final HashMap<Term, Constant> associatedTuple;

    public SelectionEvaluator(Tuple tuple, ComparisonAtom predicate, RelationalAtom relationalAtom) {
        this.predicate = predicate;
        this.relationalAtom = relationalAtom;

        // mark tuples with variables in relational Atoms
        this.associatedTuple = associateTupleWithVariables(tuple, relationalAtom);
    }

    public boolean check() {
        Term term1 = predicate.getTerm1();
        Term term2 = predicate.getTerm2();
        ComparisonOperator op = predicate.getOp();

        Constant constTerm1 = null;
        Constant constTerm2 = null;

        System.out.println("associated Tuple: " + this.associatedTuple);


        
        if (term1 instanceof Variable) {
            for (Term term : relationalAtom.getTerms()) {
                System.out.println("term " + term);
                System.out.println("term1 " + term1);
                if (term1.toString().compareTo(term.toString()) == 0) {
                    constTerm1 = this.associatedTuple.get(term);
                    break;
                }
            }
        } else {
            constTerm1 = (Constant) term1;
        }

        if (term2 instanceof Variable) {
            for (Term term : relationalAtom.getTerms()) {
                if (term2.toString().compareTo(term.toString()) == 0) {
                    constTerm2 = this.associatedTuple.get(term);
                    break;
                }
            }
        } else {
            constTerm2 = (Constant) term2;
        }

        // map term to constant if they are variables
//        Constant constTerm1 = term1 instanceof Variable ? this.associatedTuple.get(term1) : (Constant) term1;
//        Constant constTerm2 = term2 instanceof Variable ? this.associatedTuple.get(term2) : (Constant) term2;

        // compare terms on Operator
        System.out.println("const 1: " + constTerm1);
        System.out.println("const 2: " + constTerm2);
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
            System.out.println(s1);
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

    private HashMap<Term, Constant> associateTupleWithVariables(Tuple tuple, RelationalAtom relationalAtom) {
        HashMap<Term, Constant> mapping = new HashMap<>();

        for (int i = 0; i < relationalAtom.getTerms().size(); i++) {
            mapping.put(relationalAtom.getTerms().get(i), tuple.getAttributes().get(i));
        }

        return mapping;
    }
}
