package io.xpnnt.sudoku.component;

import io.xpnnt.sudoku.model.Table;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class DataReader {

    private static final String FILE_NAME = "data.txt";

    private static final String LINE_REGEXP = String.format("^[0-%s]{%s}$", Table.SIDE_SIZE, Table.SIDE_SIZE);
    private static final Pattern LINE_PATTERN = Pattern.compile(LINE_REGEXP);

    public Byte[][] readData() {
        Byte[][] result = new Byte[Table.SIDE_SIZE][Table.SIDE_SIZE];
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
             Reader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            for (int i = 0; i < Table.SIDE_SIZE; i++) {
                String line = bufferedReader.readLine();
                result[i] = validateAndParseLine(line);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect input data", e);
        }
    }

    private Byte[] validateAndParseLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Not enough rows, minimal number is " + Table.SIDE_SIZE);
        }
        String lineForParse = line.trim();
        if (!LINE_PATTERN.matcher(lineForParse).matches()) {
            throw new IllegalArgumentException("Line must be of strictly " + Table.SIDE_SIZE + " digits");
        }
        return Arrays.stream(lineForParse.split(""))
                .map(Byte::parseByte)
                .toArray(Byte[]::new);
    }

}