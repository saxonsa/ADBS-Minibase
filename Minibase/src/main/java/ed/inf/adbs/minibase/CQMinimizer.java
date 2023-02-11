package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */
    public static void minimizeCQ(String inputFile, String outputFile) {
        Query query = parsingQuery(inputFile);

        // extract x1...xk from head into a list
        Head head = query.getHead();

        // iterate every atom in body to choose one for minimization
        List<Atom> body = query.getBody();

        for (Iterator<Atom> iterator = body.iterator(); iterator.hasNext();) {
            Atom currentAtom = iterator.next();
            // test if "body \ {atom}" contains x1...xk in head
            List<Atom> bodyCloned = new ArrayList<>(body);
            bodyCloned.remove(currentAtom);
            if (!checkFreeVariablesContained(bodyCloned, head)) {
                continue;
            }

            // test if there is a query homomorphism from "Q(x1...xk) :- body" to "Q(x1...xk) :- body\{atom}"
            if (!checkHomomorphismExistence(body, bodyCloned)) {
                // Not exist
                continue;
            }

            // if true, update body to body \ {atom}
            iterator.remove();
        }

        outputMinimizedQuery(query, outputFile);
    }

    /**
     * Check if there is a query homomorphism from the original query to the minimized query
     */
    public static boolean checkHomomorphismExistence(List<Atom>originalBody, List<Atom>minimizedBody) {
        // extract relation name from both body query and check if they match, Like {R, S, T...}
        HashSet<String> originalRelation = new HashSet<>();
        HashSet<String> minimizedRelation = new HashSet<>();
        for (Atom a : originalBody) {
            RelationalAtom atom = (RelationalAtom) a;
            originalRelation.add(atom.getName());
        }
        for (Atom a : minimizedBody) {
            RelationalAtom atom = (RelationalAtom) a;
            minimizedRelation.add(atom.getName());
        }
        if (!minimizedRelation.containsAll(originalRelation)) {
            System.out.println("relation of original body: " + originalRelation);
            System.out.println("relation of minimized body: " + minimizedRelation);
            System.out.println("cannot remove this atom due to the relation table does not match!");
            return false;
        }

        // for each Relation (R, T, S...)
        ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> homo = new ArrayList<>();
        for (String r : originalRelation) {
            boolean validHomo = true;

            // find all relations associated with table r in both original and minimized body
            List<RelationalAtom> originalBodyWithRelationR = new ArrayList<>();
            List<RelationalAtom> minimizedBodyWithRelationR = new ArrayList<>();
            for (Atom a : originalBody) {
                RelationalAtom atom = (RelationalAtom) a;
                if (atom.getName().equals(r)) {
                    originalBodyWithRelationR.add(atom);
                }
            }
            for (Atom a : minimizedBody) {
                RelationalAtom atom = (RelationalAtom) a;
                if (atom.getName().equals(r)) {
                    minimizedBodyWithRelationR.add(atom);
                }
            }

            // List all the mappings from original query to minimized query

            ArrayList<ArrayList<HashMap<String, String>>> homoOfEachRelation = new ArrayList<>();
            for (RelationalAtom originalRA : originalBodyWithRelationR) {

                // homo of each sub relation: like [(t1->t2, t3->t4), (s1->s2, s3->s3)]
                ArrayList<HashMap<String, String>> homoOfEachSubRelation = new ArrayList<>();

                for (RelationalAtom minimizedRA : minimizedBodyWithRelationR) {
                    boolean validUnitHomo = true;
                    // unit homomorphism, Like (t1->t2, t3->t4)
                    HashMap<String, String> unitHomo = new HashMap<>();
                    List<Term> originalBodyTerms = originalRA.getTerms();
                    List<Term> minimizedBodyTerms = minimizedRA.getTerms();
                    for (int i = 0; i < originalBodyTerms.size(); i++) {
                        Term originalBodyTerm = originalBodyTerms.get(i);
                        Term minimizedBodyTerm = minimizedBodyTerms.get(i);
                        // check if the unit homomorphism is valid
                        if (checkValidUnitHomoMapping(originalBodyTerm, minimizedBodyTerm)) {
                            unitHomo.put(originalBodyTerm.toString(), minimizedBodyTerm.toString());
                        } else {
                            validUnitHomo = false;
                            break;
                        }
                    }
                    if (validUnitHomo) {
                        homoOfEachSubRelation.add(unitHomo);
                    }

                }

                if (homoOfEachSubRelation.size() > 0) {
                    homoOfEachRelation.add(homoOfEachSubRelation);
                } else {
                    validHomo = false;
                    break;
                }
            }

            if (validHomo) {
                homo.add(homoOfEachRelation);
            } else {
                break;
            }
        }


        System.out.println("Homo: " + homo);

        if (homo.size() == 0) {
            return false;
        }


        // A dfs algorithm trying to explore all the possible mappings

        ArrayList<ArrayList<HashMap<String, String>>> groups = new ArrayList<>();
        for (ArrayList<ArrayList<HashMap<String, String>>> homoOfEachRelation : homo) {
            groups.addAll(homoOfEachRelation);
        }

        ArrayList<HashMap<String, String>> validHomoCombinations = getValidHomoCombinations(groups);

        System.out.println("query homomorphism: " + validHomoCombinations);

        return validHomoCombinations.size() > 0;
    }

    public static ArrayList<HashMap<String, String>> getValidHomoCombinations(ArrayList<ArrayList<HashMap<String, String>>> groups) {
        ArrayList<HashMap<String, String>> validHomoCombinations = new ArrayList<>();
        dfs(groups, 0, new HashMap<>(), validHomoCombinations);
        return validHomoCombinations;
    }

    public static void dfs(ArrayList<ArrayList<HashMap<String, String>>> groups,
                      int groupIndex, HashMap<String, String> currentHomoCombination,
                      ArrayList<HashMap<String, String>> validHomoCombinations) {
        if (groupIndex == groups.size()) {
            // All groups have been processed
            validHomoCombinations.add(currentHomoCombination);
            return;
        }

        // explore all the possible choices in the current group
        ArrayList<HashMap<String, String>> group = groups.get(groupIndex);
        for (HashMap<String, String> choice : group) {
            HashMap<String, String> beforeNextHomo = new HashMap<>(currentHomoCombination);
            if (checkValidHomoCombination(currentHomoCombination, choice)) {
                addHomoCombination(currentHomoCombination, choice);
            } else {
                continue;
            }
            dfs(groups, groupIndex + 1, currentHomoCombination, validHomoCombinations);

            // remove the original future choice
            currentHomoCombination = new HashMap<>(beforeNextHomo);
        }
    }

    public static void addHomoCombination(HashMap<String, String> current, HashMap<String, String> choice) {
        for (Map.Entry<String, String> entry : choice.entrySet()) {
            if (!current.containsKey(entry.getKey())) {
                current.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static boolean checkValidHomoCombination(HashMap<String, String> homoCombination, HashMap<String, String> choice) {
        HashMap<String, String> beforeHomoCombination = new HashMap<>(homoCombination);
        for (Map.Entry<String, String> entry : choice.entrySet()) {
            if (!beforeHomoCombination.containsKey(entry.getKey())) {
                beforeHomoCombination.put(entry.getKey(), entry.getValue());
            } else {
                if (!Objects.equals(beforeHomoCombination.get(entry.getKey()), entry.getValue())) {
                    // back track it to the situation when the new choice has not been merged
                    return false;
                }
            }
        }
        return true;
    }

    /**
    * check if a unit homomorphism mapping (x->y) is valid
     * Specifically, avoid constant->variable and constant1->constant2(c1 \neq c2)
     */
    public static boolean checkValidUnitHomoMapping(Term originalBodyTerm, Term minimizedBodyTerm) {
        if (originalBodyTerm instanceof Constant) {
            return (minimizedBodyTerm instanceof Constant) &&
                    (Objects.equals(originalBodyTerm.toString(), minimizedBodyTerm.toString()));
        } else if (originalBodyTerm instanceof Variable) {
            return true;
        }
        // we temporarily skip the check for aggregation term
        return true;
    }

    /**
     * Check if the free variables are contained in the body \ {atom} set
     */
    public static boolean checkFreeVariablesContained(List<Atom> body, Head head) {
        // boolean CQ
        if (head.getVariables().size() == 0) {
            return true;
        }

        HashSet<String> bodyVariables = new HashSet<>();

        // extract free variables from head query
        HashSet<String> freeVariables = new HashSet<>();
        for (Variable var : head.getVariables()) {
            freeVariables.add(var.toString());
        }

        // extract variables from body
        for (Atom a : body) {
            RelationalAtom atom = (RelationalAtom) a;
            for (Term t : atom.getTerms()) {
                if (t instanceof Variable) {
                    bodyVariables.add(t.toString());
                }
            }
        }

        // check if free variables are contained in the new(remained) body set
        return bodyVariables.containsAll(freeVariables);
    }

    /**
     * parse query in input file
     *
     * @return Parsed query in Java object
     */
    public static Query parsingQuery(String filename) {
        Query query = null;
        try {
            query = QueryParser.parse(Paths.get(filename));
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
        return query;
    }

    /**
     * write minimized query into output/query{?} file
     */
    public static void outputMinimizedQuery(Query query, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(query.toString() + '\n');
            writer.close();
        } catch (IOException e) {
            System.err.println("Exception occurred during writer minimized query to output/query{?}.txt");
            e.printStackTrace();
        }
    }
}
