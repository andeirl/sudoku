package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Table {

    public static final int ROW_NUMBER = 3;
    public static final int COLUMN_NUMBER = 3;

    private final int rowIndex;
    private final int columnIndex;

    private final List<Cell> cells;
    private final List<Row> rows;
    private final List<Column> columns;

    private final List<Table> rowNeighbours;
    private final List<Table> columnNeighbours;

    public Table(int rowIndex, int columnIndex, int[][] values) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.cells = toCells(values);
        this.rows = toRows(cells);
        this.columns = toColumns(cells);
        this.rowNeighbours = new ArrayList<>();
        this.columnNeighbours = new ArrayList<>();
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public List<Table> getRowNeighbours() {
        return rowNeighbours;
    }

    public List<Table> getColumnNeighbours() {
        return columnNeighbours;
    }

    public Stream<Cell> getEmptyConnectedCells(int row, int column) {
        return Stream.<Stream<Cell>>builder()
                .add(cells.stream())
                .add(rowNeighborsCells(row))
                .add(columnNeighborsCells(column))
                .build()
                .flatMap(Function.identity())
                .filter(cell -> cell.getValue() == null);
    }

    private Stream<Cell> rowNeighborsCells(int row) {
        return rowNeighbours
                .stream()
                .map(neighbor -> neighbor.rows.get(row).getCells())
                .flatMap(Collection::stream);
    }

    private Stream<Cell> columnNeighborsCells(int column) {
        return columnNeighbours
                .stream()
                .map(neighbor -> neighbor.columns.get(column).getCells())
                .flatMap(Collection::stream);
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