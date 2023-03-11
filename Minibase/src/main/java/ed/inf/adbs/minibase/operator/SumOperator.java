package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SumOperator extends Operator {

    private Operator child;
    private List<Variable> groupVariables;
    private SumAggregate sumAggregate;
    private RelationalAtom relationalAtom;
    private boolean returned;

    public SumOperator(Operator child, List<Variable> groupVariables,
                       SumAggregate sumAggregate, RelationalAtom relationalAtom) {
        this.child = child;
        this.groupVariables = groupVariables;
        this.sumAggregate = sumAggregate;
        this.relationalAtom = relationalAtom;
        this.returned = false;
    }

    @Override
    public Tuple getNextTuple() {
        int sum = 0;
        Tuple tuple;
        System.out.println("groupVariables: " + groupVariables);
        if (!returned && groupVariables.size() == 0) {
            if (sumAggregate.getProductTerms().size() == 1) {
                Term singleSumTerm = sumAggregate.getProductTerms().get(0);

                if (singleSumTerm instanceof IntegerConstant) { // SUM(int)
                    while(child.getNextTuple() != null) {
                        int times = ((IntegerConstant) singleSumTerm).getValue();
                        sum += times;
                    }
                    List<Constant> tupleElements = new ArrayList<>();
                    tupleElements.add(new IntegerConstant(sum));
                    returned = true;
                    return new Tuple(tupleElements);
                } else { // SUM(var)
                    while((tuple = child.getNextTuple()) != null) {
                        // fetch attribute of var
                        int extractedSumTerm = extractSumTerm(tuple, singleSumTerm);
                        sum += extractedSumTerm;
                    }
                    returned = true;
                    return new Tuple(new ArrayList<>(sum));
                }
            }
        }

        return null;
    }

    @Override
    public void reset() {}

    private int extractSumTerm(Tuple tuple, Term var) {
        return ((IntegerConstant) tuple.getAttributes().get(relationalAtom.getTerms().indexOf(var))).getValue();
    }
}
