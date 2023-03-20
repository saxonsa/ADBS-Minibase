package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.ResultWriter;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract class of Operator
 * Define the interface of iterator model
 */

public abstract class Operator {
    public abstract Tuple getNextTuple();
    public abstract void reset();

    /**
     * repeatedly call getNextTuple to write the result to output file using predefined ResultWriter
     */
    public void dump() throws IOException {
        Tuple tuple;
        FileWriter csvWriter = ResultWriter.getFileWriter();

        while ((tuple = getNextTuple()) != null) {

            if (csvWriter != null) {
                csvWriter.write(tuple + "\n");
            } else {
                System.out.println(tuple);
            }
        }
        if (csvWriter != null)
            csvWriter.flush();

    }
}
