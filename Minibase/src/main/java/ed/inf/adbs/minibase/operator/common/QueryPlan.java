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

        // transform Q(x,y) :- R(x, y, 'adbs') to Q(x, y) :- R(x, y, z), z = 'adbs'

        // modify relational Atom from the atom like "R(x, y, 'adbs')" to the atom like R(x, y, z)
        // from now on, we need to pass the new relationalAtom to the next Operator
        RelationalAtom reconstructedRA = relationalAtoms.get(0);
        for (int i = 0; i < reconstructedRA.getTerms().size(); i++) {
            if (reconstructedRA.getTerms().get(i) instanceof Constant) {
                // generate a new letter
                String newLetter = generateNewLetterForConstantTerm();
                while(reconstructedRA.getTerms().contains(newLetter)) {
                    newLetter = generateNewLetterForConstantTerm();
                }

                // replace the Constant with the new generated variable and add the pair as Comparison atom
                comparisonAtoms.add(new ComparisonAtom(
                        new Variable(newLetter), reconstructedRA.getTerms().get(i), ComparisonOperator.EQ));
                reconstructedRA.getTerms().set(i, new Variable(newLetter));
            }
        }

        if (comparisonAtoms.size() > 0) {
            root = new SelectOperator(root, reconstructedRA, comparisonAtoms);
        }

        // check if the order of head has been changed, or if any terms in the head have been projected away
        // if so, create a ProjectOperator as a root
        Head head = query.getHead();
        if ((head.getVariables().size() < reconstructedRA.getTerms().size()) ||
                checkQueryHeadOrderChanged(head.getVariables(), reconstructedRA)) {
            // some variables have been projected away
            root = new ProjectOperator(root, query, reconstructedRA);
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

    private String generateNewLetterForConstantTerm() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuffer = new StringBuilder();
        // generate a random number between 0 and length of characters set
        int randomIndex = (int)(Math.random() * alphabet.length());
        stringBuffer.append(alphabet.charAt(randomIndex));
        randomIndex = (int)(Math.random() * alphabet.length());
        stringBuffer.append(alphabet.charAt(randomIndex));
        return new String(stringBuffer);
    }

    public Operator getRoot() {
        return root;
    }

    public Query getQuery() {
        return query;
    }
}
