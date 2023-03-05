package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.operator.common.QueryPlan;
import ed.inf.adbs.minibase.operator.common.ResultWriter;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        evaluateCQ(databaseDir, inputFile, outputFile);
    }

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        try {
            Catalog catalog = Catalog.getCatalog();
            catalog.init(databaseDir);
            Query query = QueryParser.parse(Paths.get(inputFile));
            QueryPlan plan = new QueryPlan(query);
            ResultWriter.init(outputFile);
            plan.getRoot().dump();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
