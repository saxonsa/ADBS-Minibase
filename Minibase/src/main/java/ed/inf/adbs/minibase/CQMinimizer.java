package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

        for (Atom currentAtom : body) {
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
            body.remove(currentAtom);
        }

        outputMinimizedQuery(query, outputFile);
    }

    /**
     * Check if there is a query homomorphism from the original query to the minimized query
     */
    public static boolean checkHomomorphismExistence(List<Atom>originalBody, List<Atom>minimizedBody) {
        // extract relation name from both body query and check if they match
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
            return false;
        }

        // for each Relation (R, T, S...)
        ArrayList<Object> homo = new ArrayList<>();
        for (String r : originalRelation) {

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

            ArrayList<Object> homoOfEachRelation = new ArrayList<>();
            for (RelationalAtom originalRA : originalBodyWithRelationR) {

                // homo of each sub relation: like [(t1->t2, t3->t4), (s1->s2, s3->s3)]
                ArrayList<Object> homoOfEachSubRelation = new ArrayList<>();

                for (RelationalAtom minimizedRA : minimizedBodyWithRelationR) {
                    // unit homomorphism, Like (t1->t2, t3->t4)
                    HashMap<Term, Term> unitHomo = new HashMap<>();
                    List<Term> originalBodyTerms = originalRA.getTerms();
                    List<Term> minimizedBodyTerms = minimizedRA.getTerms();
                    for (int i = 0; i < originalBodyTerms.size(); i++) {
                        Term originalBodyTerm = originalBodyTerms.get(i);
                        Term minimizedBodyTerm = minimizedBodyTerms.get(i);
                        // check if the unit homomorphism is valid
                        // avoid constant -> variable
                        if (!((originalBodyTerm instanceof Constant) && (minimizedBodyTerm instanceof Variable))) {
                            unitHomo.put(originalBodyTerm, minimizedBodyTerm);
                        }
                    }
                    homoOfEachSubRelation.add(unitHomo);
                }
                homoOfEachRelation.add(homoOfEachSubRelation);
            }
            homo.add(homoOfEachRelation);
        }

        // select correct homomorphism combination




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
            writer.write(query.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("Exception occurred during writer minimized query to output/query{?}.txt");
            e.printStackTrace();
        }
    }
}
