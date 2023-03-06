package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;


/**
 * Interpreter Class
 *
 * 1 .initialize output Writer
 * 2. Read the query from inputFile and call dump of query plan to generate result
 */
public class Interpreter {
    private final String inputFile;
    private final String outputFile;

    public Interpreter(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    /**
     * Parse query from input and generate result through dump of plan root
     */
    public void dump() {
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            QueryPlan plan = new QueryPlan(query);
            ResultWriter.init(outputFile);
            plan.getRoot().dump();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
