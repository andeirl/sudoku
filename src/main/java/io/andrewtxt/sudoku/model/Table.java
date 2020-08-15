package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

    static final int ROW_NUMBER = 3;
    static final int COLUMN_NUMBER = 3;

    private final int rowIndex;
    private final int columnIndex;

    private final List<Cell> cells;
    private final List<Row> rows;
    private final List<Column> columns;

    public Table(int rowIndex, int columnIndex, int[][] values) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.cells = toCells(values);
        this.rows = toRows(cells);
        this.columns = toColumns(cells);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    private List<Cell> toCells(int[][] values) {
        List<Cell> result = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            for (int j = 0; j < COLUMN_NUMBER; j++) {
                result.add(new Cell(i, j, values[i][j]));
            }
        }
        return result;
    }

    private List<Row> toRows(List<Cell> cells) {
        List<Row> result = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            List<Cell> subList = cells.subList(i * COLUMN_NUMBER, i * COLUMN_NUMBER + COLUMN_NUMBER);
            result.add(new Row(subList));
        }
        return result;
    }

    private List<Column> toColumns(List<Cell> cells) {
        List<Column> result = new ArrayList<>();
        for (int i = 0; i < COLUMN_NUMBER; i++) {
            List<Cell> subList = new ArrayList<>();
            for (int j = 0; j < ROW_NUMBER; j++) {
                subList.add(cells.get(j * COLUMN_NUMBER + i));
            }
            result.add(new Column(subList));
        }
        return result;
    }

}