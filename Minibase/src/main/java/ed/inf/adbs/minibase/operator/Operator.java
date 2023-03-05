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


    public void dump() {
        Tuple nextTuple;
        FileWriter outWriter = null;
        if (ResultWriter.outputWriterInitialised()) {
            outWriter = ResultWriter.getFileWriter();
        }
        try {
            while ((nextTuple = getNextTuple()) != null) {

                if (outWriter != null) {
                    outWriter.write(nextTuple + "\n");
                } else {
                    System.out.println(nextTuple);
                }
            }
            if (outWriter != null)
                outWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
