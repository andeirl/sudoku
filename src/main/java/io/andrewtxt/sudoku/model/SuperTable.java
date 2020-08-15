package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperTable {

    private static final int ROW_NUMBER = 3;
    private static final int COLUMN_NUMBER = 3;

    private final List<Table> tables;

    public SuperTable(int[][] values) {
        this.tables = toTables(values);
    }

    private List<Table> toTables(int[][] values) {
        List<Table> result = new ArrayList<>();
        for (int i = 0; i < SuperTable.ROW_NUMBER; i++) {
            for (int j = 0; j < SuperTable.COLUMN_NUMBER; j++) {
                int[][] tableValues = new int[Table.ROW_NUMBER][Table.COLUMN_NUMBER];
                for (int k = 0; k < Table.ROW_NUMBER; k++) {
                    tableValues[k] = Arrays.copyOfRange(
                            values[Table.ROW_NUMBER * i + k],
                            Table.COLUMN_NUMBER * j,
                            Table.COLUMN_NUMBER * j + Table.COLUMN_NUMBER
                    );
                }
                result.add(new Table(i, j, tableValues));
            }
        }
        return result;
    }

}