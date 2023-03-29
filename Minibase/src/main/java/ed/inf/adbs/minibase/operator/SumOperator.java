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

        // if there is no group variables, and no result has been returned before
        // then only perform Sum Operator on sum Aggregate atom, SUM(int) and SUM(var)
        if (!returned && groupVariables.size() == 0) {
            if (sumAggregate.getProductTerms().size() == 1) {
                Term singleSumTerm = sumAggregate.getProductTerms().get(0);

                if (singleSumTerm instanceof IntegerConstant) { // SUM(int)
                    while(child.getNextTuple() != null) {
                        int times = ((IntegerConstant) singleSumTerm).getValue();
                        sum += times;
                    }
                } else { // SUM(var)
                    while((tuple = child.getNextTuple()) != null) {
                        // fetch attribute of var
                        int extractedSumTerm = extractSumTerm(tuple, singleSumTerm);
                        sum += extractedSumTerm;
                    }
                }
            } else {
                // production terms SUM(int * int * var)
                while((tuple = child.getNextTuple()) != null) {
                    sum += getProductResult(tuple);
                }
            }
            returned = true;
            return constructTupleWithInt(sum);
        }

        if (groupVariables.size() > 0) {
            Tuple sumTuple = null;
            // group by
            while (currentTuple != null) {
                Tuple tupleWithGroupVariable = getTupleWithGroupVariable(currentTuple);
                if (sumTuple == null) {
                    sumTuple = tupleWithGroupVariable;
                } else {
                    // check if the group-by conditions are the same,
                    // i.e. same variables for current sum tuple and coming tuple
                    // if they are different, output current tuple
                    // if they are same, aggregate the result and continue to read next tuple
                    for (int i = 0; i < tupleWithGroupVariable.getAttributes().size() - 1; i++) {
                        if (tupleWithGroupVariable.getAttributes().get(i) instanceof IntegerConstant) {
                            if (!Objects.equals(((IntegerConstant) tupleWithGroupVariable.getAttributes().get(i)).getValue(),
                                    ((IntegerConstant) sumTuple.getAttributes().get(i)).getValue())) {
                                return sumTuple;
                            }
                        } else {
                            if (!Objects.equals(((StringConstant) tupleWithGroupVariable.getAttributes().get(i)).getValue(),
                                    ((StringConstant) sumTuple.getAttributes().get(i)).getValue())) {
                                return sumTuple;
                            }
                        }

                    }

                    // aggregate current tuple to sumTuple
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

    /**
     * Extract the corresponding term in the SUM operator in the tuple
     * @param tuple current tuple to fetch the corresponding term
     * @param var term to extract
     * @return the term extracted, generally should be a integer to support SUM only
     */
    private int extractSumTerm(Tuple tuple, Term var) {
        return ((IntegerConstant) tuple.getAttributes().get(relationalAtom.getTerms().indexOf(var))).getValue();
    }

    /**
     * combine group variables with aggregate terms: Tuple(group varibles... SUM..)
     * @param tuple The original tuple used to genereate aggregation result
     * @return The tuples with specified group variables and aggregation result
     */
    private Tuple getTupleWithGroupVariable(Tuple tuple) {
        // group variable terms
        List<Constant> tupleWithGroupVariableAttributes = new ArrayList<>();
        for (Variable groupVariable : groupVariables) {
            int index = relationalAtom.getTerms().indexOf(groupVariable);
            tupleWithGroupVariableAttributes.add(tuple.getAttributes().get(index));
        }

        // add aggregate term
        tupleWithGroupVariableAttributes.add(new IntegerConstant(getProductResult(tuple)));

        return new Tuple(tupleWithGroupVariableAttributes);
    }

    /**
     * get the result of product according to the formula in aggregation rules
     * @param tuple The original tuple used to genereate aggregation result
     * @return The product in integer
     */
    private int getProductResult(Tuple tuple) {
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
        return product;
    }

    /**
     * transform a pure integer to a tuple element
     * @param integer given integer waiting to transform
     * @return the result tuple with the given integer
     */
    private Tuple constructTupleWithInt(int integer) {
        List<Constant> tupleElements = new ArrayList<>();
        tupleElements.add(new IntegerConstant(integer));
        return new Tuple(tupleElements);
    }
}
