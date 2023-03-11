package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class SumOperator extends Operator {

    private final Operator child;
    private final List<Variable> groupVariables;
    private final SumAggregate sumAggregate;
    private final RelationalAtom relationalAtom;
    private boolean returned;
    private Tuple currentTuple;

    public SumOperator(Operator child, List<Variable> groupVariables,
                       SumAggregate sumAggregate, RelationalAtom relationalAtom) {
        this.child = child;
        this.groupVariables = groupVariables;
        this.sumAggregate = sumAggregate;
        this.relationalAtom = relationalAtom;
        this.returned = false;

        if ((groupVariables != null) && (groupVariables.size() > 0)) {
            this.currentTuple = this.child.getNextTuple();
        }
    }

    @Override
    public Tuple getNextTuple() {
        int sum = 0;
        Tuple tuple;
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
                    List<Constant> tupleElements = new ArrayList<>();
                    tupleElements.add(new IntegerConstant(sum));
                    return new Tuple(tupleElements);
                }
            } else {
                // production terms

                while((tuple = child.getNextTuple()) != null) {
                    int product = 1;
                    for (Term term : sumAggregate.getProductTerms()) {
                        if (term instanceof IntegerConstant) {
                            int value = ((IntegerConstant) term).getValue();
                            product = value * product;
                        } else { // variable
                            int index = relationalAtom.getTerms().indexOf(term);
                            int value = ((IntegerConstant) tuple.getAttributes().get(index)).getValue();
                            product *= value;
                        }
                    }
                    sum += product;
                }
                List<Constant> tupleElements = new ArrayList<>();
                tupleElements.add(new IntegerConstant(sum));
                returned = true;
                return new Tuple(tupleElements);
            }
        }

        if (groupVariables.size() > 0) {
            Tuple sumTuple = null;
            // group by
            while (currentTuple != null) {
                Tuple tupleWithGroupVariable = getTupleWithGroupVariable(currentTuple);
                if (sumTuple == null) {
                    sumTuple = tupleWithGroupVariable;
                } else {
                    // check(currentTuple)
                    for (int i = 0; i < tupleWithGroupVariable.getAttributes().size() - 1; i++) {
                        if (!Objects.equals(((IntegerConstant) tupleWithGroupVariable.getAttributes().get(i)).getValue(),
                                ((IntegerConstant) sumTuple.getAttributes().get(i)).getValue())) {
                            return sumTuple;
                        }
                    }

                    // add to sumTuple
                    int finalIndex = tupleWithGroupVariable.getAttributes().size() - 1;
                    int valueOnCurrentTuple = ((IntegerConstant)tupleWithGroupVariable.getAttributes().get(finalIndex)).getValue();
                    int valueOnSumTuple = ((IntegerConstant)sumTuple.getAttributes().get(finalIndex)).getValue();
                    sumTuple.getAttributes().set(finalIndex, new IntegerConstant(valueOnSumTuple + valueOnCurrentTuple));
                }

                // read next
                currentTuple = child.getNextTuple();
            }
            return sumTuple;
        }
        return null;
    }

    @Override
    public void reset() {}

    private int extractSumTerm(Tuple tuple, Term var) {
        return ((IntegerConstant) tuple.getAttributes().get(relationalAtom.getTerms().indexOf(var))).getValue();
    }

    private Tuple getTupleWithGroupVariable(Tuple tuple) {
        // group variable terms
        List<Constant> tupleWithGroupVariableAttributes = new ArrayList<>();
        for (Variable groupVariable : groupVariables) {
            int index = relationalAtom.getTerms().indexOf(groupVariable);
            tupleWithGroupVariableAttributes.add(tuple.getAttributes().get(index));
        }

        // aggregation terms
        int product = 1;
        for (Term term : sumAggregate.getProductTerms()) {
            if (term instanceof IntegerConstant) {
                int value = ((IntegerConstant) term).getValue();
                product = value * product;
            } else { // variable
                int index = relationalAtom.getTerms().indexOf(term);
                int value = ((IntegerConstant) tuple.getAttributes().get(index)).getValue();
                product *= value;
            }
        }
        tupleWithGroupVariableAttributes.add(new IntegerConstant(product));

        return new Tuple(tupleWithGroupVariableAttributes);
    }
}
