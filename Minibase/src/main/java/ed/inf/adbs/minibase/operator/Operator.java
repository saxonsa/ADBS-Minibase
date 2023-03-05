package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.ResultWriter;
import ed.inf.adbs.minibase.operator.db.Tuple;

import java.io.FileWriter;
import java.io.IOException;

/**
 * interface of Operator
 */

public abstract class Operator {
    public abstract Tuple getNextTuple();
    public abstract void reset();


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
