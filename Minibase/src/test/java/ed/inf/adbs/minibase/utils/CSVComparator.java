package ed.inf.adbs.minibase.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVComparator {

    public static boolean areCSVFilesEqual(String filePath1, String filePath2) throws IOException {
        List<List<String>> csv1 = readCSV(filePath1);
        List<List<String>> csv2 = readCSV(filePath2);

        if (csv1.size() != csv2.size()) {
            return false;
        }

        for (int i = 0; i < csv1.size(); i++) {
            List<String> row1 = csv1.get(i);
            List<String> row2 = csv2.get(i);

            if (!row1.equals(row2)) {
                return false;
            }
        }

        return true;
    }

    private static List<List<String>> readCSV(String filePath) throws IOException {
        List<List<String>> csv = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));
                csv.add(row);
            }
        }

        return csv;
    }
}
