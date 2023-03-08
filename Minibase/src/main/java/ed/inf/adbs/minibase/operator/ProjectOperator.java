package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends Operator {

    // maintain a List to note the tuples that have already been reported
    private final List<Tuple> reportedTuples;
    private final Operator child;
    private final RelationalAtom relationalAtom;
    private final Query query;

    public ProjectOperator(Operator child, Query query, RelationalAtom relationalAtom) {
        this.child = child;
        this.relationalAtom = relationalAtom;
        this.query = query;
        this.reportedTuples = new ArrayList<>();
    }

    @Override
    public Tuple getNextTuple() {
        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {
            // process tuples on projection
            tuple = getProjectedTuples(tuple);

            // remember we need to check the tuples that are after processed
            if (!reportedTuples.contains(tuple)) {
                reportedTuples.add(tuple);
                return tuple;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        child.reset();
    }

    private Tuple getProjectedTuples(Tuple tuple) {
        List<Constant> projectedAttributes = new ArrayList<>();
        for (Variable var : query.getHead().getVariables()) {
            int index = relationalAtom.getTerms().indexOf(var);
            projectedAttributes.add(tuple.getAttributes().get(index));
        }
        return new Tuple(projectedAttributes);
    }
}
