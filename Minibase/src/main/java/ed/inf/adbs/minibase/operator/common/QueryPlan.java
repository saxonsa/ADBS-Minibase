package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.operator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryPlan {
    private Operator root= null;
    private final Query query;
    private RelationalAtom mergedRelationalAtom = null;

    public QueryPlan(Query query) {
        this.query = query;
        constructQueryTree();
    }

    private void constructQueryTree() {
        // create a ScamOperator as a root for any queries
        List<RelationalAtom> relationalAtoms = query.getBody().stream()
                .filter(RelationalAtom.class::isInstance)
                .map(RelationalAtom.class::cast).collect(Collectors.toList());

        // check if there is any selection predicates, if it is, create a root for SelectOperator
        List<ComparisonAtom> comparisonAtoms = query.getBody().stream()
                        .filter(ComparisonAtom.class::isInstance)
                                .map(ComparisonAtom.class::cast).collect(Collectors.toList());

        // modify all relational Atom from the atom like "R(x, y, 'adbs')" to the atom like R(x, y, z)
        // from now on, we need to pass the new relationalAtom to the next Operator
        // such as: transform Q(x,y) :- R(x, y, 'adbs') to Q(x, y) :- R(x, y, z), z = 'adbs'
        List<RelationalAtom> relationalAtomRAs = new ArrayList<>();
        for (RelationalAtom reconstructedRA : relationalAtoms) {
            for (int j = 0; j < reconstructedRA.getTerms().size(); j++) {
                if (reconstructedRA.getTerms().get(j) instanceof Constant) {
                    // generate a new letter
                    String newLetter = generateNewLetterForConstantTerm();
                    while (reconstructedRA.getTerms().contains(newLetter)) {
                        newLetter = generateNewLetterForConstantTerm();
                    }

                    // replace the Constant with the new generated variable and add the pair as Comparison atom
                    comparisonAtoms.add(new ComparisonAtom(
                            new Variable(newLetter), reconstructedRA.getTerms().get(j), ComparisonOperator.EQ));
                    reconstructedRA.getTerms().set(j, new Variable(newLetter));
                }
            }
            relationalAtomRAs.add(reconstructedRA);
        }

        // extract select conditions and join conditions from newly constructed composed predicates
        if (relationalAtomRAs.size() == 1) {
            root = new ScanOperator(relationalAtoms.get(0));
            if (comparisonAtoms.size() > 0) {
                root = new SelectOperator(root, relationalAtoms.get(0), comparisonAtoms);
            }
            mergedRelationalAtom = relationalAtoms.get(0);
        } else { // join
            // construct join operator in recursive manner
            root = constructJoinOperator(root, relationalAtomRAs, comparisonAtoms, 1);
        }

        // check if the order of head has been changed, or if any terms in the head have been projected away
        // if so, create a ProjectOperator as a root
        Head head = query.getHead();

        if (head.getSumAggregate() != null) {
            root = new SumOperator(root, head.getVariables(), head.getSumAggregate(), mergedRelationalAtom);
            return;
        }
        if ((head.getVariables().size() < mergedRelationalAtom.getTerms().size()) ||
                checkQueryHeadOrderChanged(head.getVariables(), mergedRelationalAtom)) {
            // some variables have been projected away
            root = new ProjectOperator(root, query, mergedRelationalAtom);
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

    /**
     * A java polymorphism class to extract `selection condition` for the given relational atom from predicates
     * @param ra relation to check if any predicates work on this relation only
     * @param predicates the initial predicates resolved from query parser used to extract selection condition
     *                   for the given relational atom
     * @return the extracted predicates that contain selection condition on the given relational atom
     *          Or we say the relational atom contains the variables of both terms in the result predicate
     */
    private List<ComparisonAtom> extractPredicates(RelationalAtom ra, List<ComparisonAtom> predicates) {
        List<ComparisonAtom> extractedPredicates = new ArrayList<>();
        for (ComparisonAtom predicate : predicates) {
            Term term1 = predicate.getTerm1();
            Term term2 = predicate.getTerm2();

            if (!(((term1 instanceof Variable) && (!(ra.getTerms().contains(term1))))
                || ((term2 instanceof Variable) && (!ra.getTerms().contains(term2))))) {
                extractedPredicates.add(predicate);
            }
        }
        return extractedPredicates;
    }

    /**
     * A java polymorphism class to extract `join condition` from predicates
     * Main idea: If the predicates involves variables(terms) from two relations, then it's a join condition
     * @param ra1 relation of left child
     * @param ra2 relation of right child
     * @param predicates the initial predicates resolved from query parser used to extract join condition
     * @return the extracted predicates that contain join condition only
     */
    private List<ComparisonAtom> extractPredicates(RelationalAtom ra1, RelationalAtom ra2, List<ComparisonAtom> predicates) {
        List<ComparisonAtom> extractedPredicates = new ArrayList<>();
        for (ComparisonAtom predicate : predicates) {
            Term term1 = predicate.getTerm1();
            Term term2 = predicate.getTerm2();

            if (ra1.getTerms().contains(term1) && ra2.getTerms().contains(term2)) {
                extractedPredicates.add(predicate);
            }
        }
        return extractedPredicates;
    }

    /**
     * Construct join operator in recursive manner
     * @param root use the current joinOperator as leftChild
     * @param relationalAtoms The relational atoms after processed the implicit constant selection built in the tuples
     *                        such as: R(x, y, 4) has already been processed to R(x, y, zz), zz = 4
     * @param predicates the predicates with implicit constant selection, which includes zz=4 for instance.
     * @param rightChildIndex the index to mark the next right child(relation)
     * @return The root join Operator that could all the relations regarding the given query
     */
    private Operator constructJoinOperator(Operator root, List<RelationalAtom> relationalAtoms,
                                           List<ComparisonAtom> predicates, int rightChildIndex) {
        if (rightChildIndex == relationalAtoms.size()) {
            return root;
        }
        if (rightChildIndex == 1) {
            mergedRelationalAtom = relationalAtoms.get(0);
            root = new ScanOperator(relationalAtoms.get(0));
            // check selection condition for leftChild
            List<ComparisonAtom> predicatesOnLeftChild = extractPredicates(relationalAtoms.get(0), predicates);
            if (predicatesOnLeftChild.size() > 0) {
                root = new SelectOperator(root, relationalAtoms.get(0), predicatesOnLeftChild);
            }
        }

        Operator rightChild = new ScanOperator(relationalAtoms.get(rightChildIndex));
        // check selection condition for rightChild
        List<ComparisonAtom> predicatesOnRightChild = extractPredicates(relationalAtoms.get(rightChildIndex), predicates);
        if (predicatesOnRightChild.size() > 0) {
            rightChild = new SelectOperator(rightChild, relationalAtoms.get(rightChildIndex), predicatesOnRightChild);
        }
        List<ComparisonAtom> predicatesOnJoin = extractPredicates(mergedRelationalAtom, relationalAtoms.get(rightChildIndex), predicates);
        mergedRelationalAtom = mergeRelationalAtom(mergedRelationalAtom, relationalAtoms.get(rightChildIndex));
        root = new JoinOperator(root, rightChild, mergedRelationalAtom, predicatesOnJoin);
        return constructJoinOperator(root, relationalAtoms, predicates, rightChildIndex + 1);
    }

    private RelationalAtom mergeRelationalAtom(RelationalAtom ra1, RelationalAtom ra2) {
        List<Term> mergedTerms = new ArrayList<>();
        mergedTerms.addAll(ra1.getTerms());
        mergedTerms.addAll(ra2.getTerms());
        return new RelationalAtom(ra1.getName()+ " " + ra2.getName(), mergedTerms);
    }

    public Operator getRoot() {
        return root;
    }

    public Query getQuery() {
        return query;
    }
}
