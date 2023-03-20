package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.db.Catalog;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScanOperator extends Operator{

    public RelationalAtom relationalAtom;

    // fetch global Catalog instance to read the input, output, and DB folder
    private final Catalog catalog = Catalog.getCatalog();
    private Scanner scanner = null;
    private List<Tuple> reportedTuples;

    public ScanOperator(RelationalAtom relationalAtom) {
        this.relationalAtom = relationalAtom;
        this.reportedTuples = new ArrayList<>();
        init();
    }

    /**
     * Initialize scanner with the associated file "{Relation}.csv"
     */
    public void init() {
        // fetch db file associated with relation atom
        String relationFilePath = catalog.getRelationFilePath(relationalAtom.getName());
        try {
            this.scanner = new Scanner(new File(relationFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch next line in the {Relation}.csv file and parse it into predefined Tuple format
     * @return the next tuple in the given relation
     */
    @Override
    public Tuple getNextTuple() {
        while (scanner.hasNextLine()) {
            Tuple tuple = parseDBLineToTuple(scanner.nextLine());

            // remember we need to check the tuples that are after processed
            // to keep the set semantics for output
            // This case when it will inside the if clause will only happen when the inputs contain duplicated tuples
            if (!reportedTuples.contains(tuple)) {
                reportedTuples.add(tuple);
                return tuple;
            }
        }
        scanner.close();
        return null;
    }

    /**
     * Reset file scanner to the beginning of the file
     * and clear the list containing the reported tuples (use to avoid the case when the input contains duplication)
     */
    @Override
    public void reset() {
        this.reportedTuples = new ArrayList<>();
        // fetch the file path of given relation
        String relationFilePath = catalog.getRelationFilePath(relationalAtom.getName());
        try {
            // reconstruct (reset) the scanner
            this.scanner = new Scanner(new File(relationFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * parse a line in {Relation}.csv from string to predefined tuple format
     * @param dbLine A line in String format read from {Relation}.csv file
     * @return A Line in tuple format
     */
    public Tuple parseDBLineToTuple(String dbLine) {
        String[] values = dbLine.split(",");
        return new Tuple(IntStream.range(0, values.length)
                .mapToObj(item -> {
                    try {
                        Constructor<? extends Constant> constructor = catalog.getSchemaMap()
                                .get(relationalAtom.getName())
                                .getAttributeTypes()
                                .get(item)
                                .getDeclaredConstructor(String.class);
                        return constructor.newInstance(values[item].trim().replaceAll("'", ""));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                             InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
    }

    public RelationalAtom getRelationalAtom() {
        return relationalAtom;
    }
}
