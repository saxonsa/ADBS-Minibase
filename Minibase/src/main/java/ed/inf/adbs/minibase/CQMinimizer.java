package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        for (int i = 0; i < body.size(); i++) {
            // test if "body \ {atom}" contains x1...xk in head
            List<Atom> bodyCloned = new ArrayList<>(body);
            Atom removedAtom = bodyCloned.remove(i);
            if(!checkFreeVariablesContained(bodyCloned, head)) {
                continue;
            }

            // test if there is a query homomorphism from "Q(x1...xk) :- body" to "Q(x1...xk) :- body\{atom}"


            // if true, update body to body \ {atom}

            // else continue

            // until the last atom
        }

        outputMinimizedQuery(query, outputFile);
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
