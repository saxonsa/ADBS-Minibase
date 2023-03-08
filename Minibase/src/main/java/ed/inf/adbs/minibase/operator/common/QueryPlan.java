package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.Operator;
import ed.inf.adbs.minibase.operator.ProjectOperator;
import ed.inf.adbs.minibase.operator.ScanOperator;
import ed.inf.adbs.minibase.operator.SelectOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QueryPlan {
    private Operator root;
    private final Query query;

    public QueryPlan(Query query) {
        this.query = query;
        constructQueryTree();
    }

    private void constructQueryTree() {
        // create a ScamOperator as a root for any queries
        List<RelationalAtom> relationalAtoms = query.getBody().stream()
                .filter(RelationalAtom.class::isInstance)
                .map(RelationalAtom.class::cast).collect(Collectors.toList());
        root = new ScanOperator(relationalAtoms.get(0));

        // check if there is any selection predicates, if it is, create a root for SelectOperator
        List<ComparisonAtom> comparisonAtoms = query.getBody().stream()
                        .filter(ComparisonAtom.class::isInstance)
                                .map(ComparisonAtom.class::cast).collect(Collectors.toList());
        if (comparisonAtoms.size() > 0) {
            root = new SelectOperator(root, relationalAtoms.get(0), comparisonAtoms);
        }

        // check if the order of head has been changed, or if any terms in the head have been projected away
        // if so, create a ProjectOperator as a root
        Head head = query.getHead();
        if ((head.getVariables().size() < relationalAtoms.get(0).getTerms().size()) ||
                checkQueryHeadOrderChanged(head.getVariables(), relationalAtoms.get(0))) {
            // some variables have been projected away
            root = new ProjectOperator(root, query, relationalAtoms.get(0));
        }
    }

    private boolean checkQueryHeadOrderChanged(List<Variable> head, RelationalAtom relationalAtom) {
        List<Variable> body = new ArrayList<>();
        for (Term term : relationalAtom.getTerms()) {
            body.add((Variable) term);
        }
        
        for (int i = 0; i < head.size(); i++) {
            if (body.indexOf(head.get(i)) != i) {
                return true;
            }
        }
        return false;
    }

    public Operator getRoot() {
        return root;
    }

    public Query getQuery() {
        return query;
    }
}
