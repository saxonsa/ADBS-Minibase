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
    private final Catalog catalog = Catalog.getCatalog();
    private Scanner scanner = null;
    private List<Tuple> reportedTuples;

    public ScanOperator(RelationalAtom relationalAtom) {
        this.relationalAtom = relationalAtom;
        this.reportedTuples = new ArrayList<>();
        init();
    }

    public void init() {
        // initialize file scanner with given relation atom
        String relationFilePath = catalog.getRelationFilePath(relationalAtom.getName());
        try {
            this.scanner = new Scanner(new File(relationFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tuple getNextTuple() {
        while (scanner.hasNextLine()) {
            Tuple tuple = parseDBLineToTuple(scanner.nextLine());

            // remember we need to check the tuples that are after processed
            if (!reportedTuples.contains(tuple)) {
                reportedTuples.add(tuple);
                return tuple;
            }
        }
        scanner.close();
        return null;
    }

    @Override
    public void reset() {
        this.reportedTuples = new ArrayList<>();
        String relationFilePath = catalog.getRelationFilePath(relationalAtom.getName());
        try {
            this.scanner = new Scanner(new File(relationFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Tuple parseDBLineToTuple(String dbLine) {
        String[] values = dbLine.split(",");
//        System.out.println("values: " + Arrays.toString(values));
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
