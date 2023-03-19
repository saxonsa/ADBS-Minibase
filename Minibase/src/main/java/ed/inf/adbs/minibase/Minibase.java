package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.operator.common.Interpreter;
import ed.inf.adbs.minibase.operator.db.Catalog;

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

//        long startTime = System.currentTimeMillis();
        evaluateCQ(databaseDir, inputFile, outputFile);
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//        System.out.println("Execution time of evaluateCQ() in milliseconds: " + duration);
    }

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        Catalog catalog = Catalog.getCatalog();
        catalog.init(databaseDir);
        Interpreter interpreter = new Interpreter(inputFile, outputFile);
        interpreter.dump();
    }
}
