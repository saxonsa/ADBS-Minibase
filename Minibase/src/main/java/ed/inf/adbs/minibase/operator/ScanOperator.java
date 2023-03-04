package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.Utils;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.operator.common.Catalog;
import ed.inf.adbs.minibase.operator.common.Schema;
import ed.inf.adbs.minibase.operator.common.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ScanOperator extends Operator{

    private final RelationalAtom relationalAtom;
    private Schema schema = null;
    private final Catalog catalog = Catalog.getInstance();
    private Scanner scanner = null;

    public ScanOperator(RelationalAtom relationalAtom) {
        this.relationalAtom = relationalAtom;
        init();
    }

    public void init() {
        ArrayList<String> attributeTypes = null;
        // fill schema
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Catalog.getInstance().getSchema()));

            // line will be in the format: "R int int string"
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                String[] termAttr = line.split("\\s+");
                if (termAttr[0].equals(relationalAtom.getName())) {
                    attributeTypes = new ArrayList<>(Arrays.asList(termAttr).subList(1, termAttr.length));
                    break;
                }
                line = br.readLine();
            }
            this.schema = new Schema(relationalAtom.getTerms(), attributeTypes);
        } catch (IOException e) {
            // FileNotFound for FileReader, IOException for BufferReader readLine
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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
       if (scanner.hasNextLine()) {
           return new Tuple(this.schema, scanner.nextLine());
       } else {
           scanner.close();
       }
       return null;
    }

    @Override
    public void reset() {
        String relationFilePath = catalog.getRelationFilePath(relationalAtom.getName());
        try {
            this.scanner = new Scanner(new File(relationFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dump() {
        Tuple tuple = this.getNextTuple();
        while (tuple != null) {
            writeFile(tuple, catalog.getOutputFile());
            tuple = this.getNextTuple();
        }
    }

    /**
     * printStream for testing the operator
     * @param t
     * @param outputFile
     */
    public void writeFile(Tuple t, String outputFile) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(outputFile, true);
            bw = new BufferedWriter(fw);
            String str = Utils.join(t.getAttributes(), ",");
            bw.write(str);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
