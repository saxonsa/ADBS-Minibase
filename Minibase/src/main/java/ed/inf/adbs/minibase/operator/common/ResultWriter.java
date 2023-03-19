package ed.inf.adbs.minibase.operator.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * The printStream to write the result to CSV file designed following Singleton Pattern
 */
public class ResultWriter {

    private static FileWriter fileWriter;

    public static FileWriter getFileWriter() {
        if (fileWriter != null)
            return fileWriter;

        else throw new UnsupportedOperationException("The file writer is not initialized!");
    }

    /**
     * Initialize the ResultWriter with the target output file
     *
     * @param outputFile global output CSV file to save result
     * @throws IOException
         * if the named file exists but is a directory rather than a regular file,
         * does not exist but cannot be created,
         * or cannot be opened for any other reason
     */
    public static void init(String outputFile) throws IOException {
        // create output folder for evaluation if it does not exist
        File outputDir = new File("data/evaluation/output");
        if (!outputDir.exists()) {
            if (!outputDir.mkdir()) {
                System.err.println("Fail to create output folder!");
            }
        }

        File saveFile = Paths.get(outputFile).toFile();
        saveFile.createNewFile();

        fileWriter = new FileWriter(outputFile);
    }

    private ResultWriter() {

    }
}
