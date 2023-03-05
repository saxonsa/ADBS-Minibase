package ed.inf.adbs.minibase.operator.db;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.IntegerConstant;
import ed.inf.adbs.minibase.base.StringConstant;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Catalog of Minibase database designed following Singleton Pattern
 * Keep track of following information:
 * - where a file for a given relation is located
 * - what the schema of relation is
 */

public class Catalog {
    private static volatile Catalog catalog;
    private String databaseDir;
    private Map<String, Schema> schemaMap;

    private Catalog() {};

    // generate instance with double check mechanism, safe for thread pool
    public static Catalog getCatalog() {
        if (null == catalog) {
            synchronized (Catalog.class) {
                if (null == catalog) {
                    catalog = new Catalog();
                }
            }
        }
        return catalog;
    }

    public void init(String databaseDir) {
        this.databaseDir = databaseDir;
        catalog.schemaMap = new HashMap<>();
        this.extractSchema(databaseDir + "/schema.txt");
    }

    public String getRelationFilePath(String relationName) {
        if (catalog == null) {
            throw new RuntimeException("Catalog is not yet initialized");
        }
        return catalog.databaseDir + "/files/" + relationName + ".csv";
    }

    private void extractSchema(String schemaFilePath) {
        File schemaFile = new File(schemaFilePath);
        try {

            BufferedReader reader = new BufferedReader(new FileReader(schemaFile));
            String line;

            // line is in the format like "R int int Sting"
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                List<String> schemaValues = Arrays.asList(line.split("\\s+"));
                String relationName = schemaValues.get(0);
                List<String> attributesInString = new ArrayList<>(schemaValues.subList(1, schemaValues.size()));
                List<Class<? extends Constant>> attributes = attributesInString.stream()
                        .map(this::extractSubclassFromConstant)
                        .collect(Collectors.toList());
                this.schemaMap.put(relationName, new Schema(relationName, attributes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class<? extends Constant> extractSubclassFromConstant(String attribute) {
        return (attribute.equals("int")) ? IntegerConstant.class : StringConstant.class;
    }

    public Map<String, Schema> getSchemaMap() {
        return schemaMap;
    }
}
