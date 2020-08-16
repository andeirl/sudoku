package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.SuperTable;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class DataReader {

    private static final String FILE_NAME = "data.txt";

    private static final String LINE_REGEXP = String.format("^[0-%s]{%s}$", SuperTable.ROW_NUMBER, SuperTable.ROW_NUMBER);
    private static final Pattern LINE_PATTERN = Pattern.compile(LINE_REGEXP);

    public int[][] readData() {
        int[][] result = new int[SuperTable.ROW_NUMBER][SuperTable.COLUMN_NUMBER];
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
             Reader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            for (int i = 0; i < SuperTable.ROW_NUMBER; i++) {
                String line = bufferedReader.readLine();
                result[i] = validateAndParseLine(line);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect input data", e);
        }
    }

    private int[] validateAndParseLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Not enough rows, minimal number is " + SuperTable.ROW_NUMBER);
        }
        String lineForParse = line.trim();
        if (!LINE_PATTERN.matcher(lineForParse).matches()) {
            throw new IllegalArgumentException("Line must be of strictly " + SuperTable.COLUMN_NUMBER + " digits");
        }
        return Arrays.stream(lineForParse.split(""))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

}