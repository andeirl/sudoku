package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperTable {

    public static final int ROW_NUMBER = 3;
    public static final int COLUMN_NUMBER = 3;

    private final List<Table> tables;

    public SuperTable(int[][] values) {
        this.tables = toTables(values);
        connectTables();
    }

    public List<Table> getTables() {
        return tables;
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

    private void connectTables() {
        for (Table table : tables) {
            findNeighbours(table);
        }
    }

    private void findNeighbours(Table baseTable) {
        int baseTableRowIndex = baseTable.getRowIndex();
        int baseTableColumnIndex = baseTable.getColumnIndex();
        for (Table table : tables) {
            if (table.getRowIndex() == baseTableRowIndex && table.getColumnIndex() != baseTableColumnIndex) {
                baseTable.getRowNeighbours().add(table);
            }
            if (table.getColumnIndex() == baseTableColumnIndex && table.getRowIndex() != baseTableRowIndex) {
                baseTable.getColumnNeighbours().add(table);
            }
        }
    }

}