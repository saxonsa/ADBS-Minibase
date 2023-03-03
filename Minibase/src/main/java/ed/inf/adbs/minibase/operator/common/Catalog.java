package ed.inf.adbs.minibase.operator.common;

import java.io.File;

/**
 * Catalog of Minibase database designed following Singleton Pattern
 * Keep track of following information:
 * - where a file for a given relation is located
 * - what the schema of relation is
 */

public class Catalog {
    private static volatile Catalog instance;
    private String databaseDir;
    private String inputFile;
    private String outputFile;

    private Catalog() {};

    // generate instance with double check mechanism, safe for thread pool
    public static Catalog getInstance() {
        if (null == instance) {
            synchronized (Catalog.class) {
                if (null == instance) {
                    instance = new Catalog();
                }
            }
        }
        return instance;
    }

    public void init(String databaseDir, String inputFile, String outputFile) {
        this.databaseDir = databaseDir;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public String getInputFile() {
        return this.inputFile;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    public String getRelationFilePath(String relationName) {
        if (instance == null) {
            throw new RuntimeException("Catalog is not yet initialized");
        }
        return instance.databaseDir + "/files/" + relationName + ".csv";
    }

    public String getSchema() {
        if (instance == null) {
            throw new RuntimeException("Catalog is not yet initialized");
        }
        return instance.databaseDir + "/schema.txt";
    }
}
