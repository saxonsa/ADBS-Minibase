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

    //  this constructor creates a projection operator perojecting over tuples resulting from a varying number of joins
    public ProjectOperator(Operator child, Query query, RelationalAtom relationalAtom) {
        this.child = child;
        this.relationalAtom = relationalAtom;
        this.query = query;
        this.reportedTuples = new ArrayList<>();
    }

    /**
     * Gets the next tuple from the child operator and returns the relevant columns as specified in the operator
     *
     * Incorporates set semantics by adding to reportedTuples of tuples emitted, updating the reportedTuples from only keeping the required columns.
     * If the current child tuple's projection has been output previously, continue searching until a unique one is found.
     *       Upon discovery, emit the new tuple and include it in the set of released tuples.
     *
     * @return the next valid tuple. if none found, return null.
     */
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

    /**
     * get the tuples with projected attributes specified in variables in head
     * @param tuple Fetched tuple from child operator which contains full attributes
     * @return Tuples with projected attributes
     */
    private Tuple getProjectedTuples(Tuple tuple) {
        List<Constant> projectedAttributes = new ArrayList<>();
        for (Variable var : query.getHead().getVariables()) {
            int index = relationalAtom.getTerms().indexOf(var);
            projectedAttributes.add(tuple.getAttributes().get(index));
        }
        return new Tuple(projectedAttributes);
    }
}
