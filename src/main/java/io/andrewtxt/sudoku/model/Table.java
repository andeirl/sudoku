package io.andrewtxt.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;

public class Table {

    public static final int ROW_NUMBER = 9;
    public static final int COLUMN_NUMBER = 9;

    private final List<List<Cell>> cells;

    public Table(int[][] values) {
        this.cells = toCells(values);
        connectCells();
    }

    public List<List<Cell>> getCells() {
        return cells;
    }

    @Override
    public String toString() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            String line = cells.get(i)
                    .stream()
                    .map(cell -> Optional.ofNullable(cell.getValue()).orElse(0))
                    .map(String::valueOf)
                    .collect(Collectors.joining(""));
            lines.add(line);
        }
        return String.join("\n", lines);
    }

    private List<List<Cell>> toCells(int[][] values) {
        List<List<Cell>> result = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            int[] arrayRow = values[i];
            List<Cell> listRow = new ArrayList<>();
            result.add(listRow);
            for (int j = 0; j < COLUMN_NUMBER; j++) {
                listRow.add(new Cell(i, j, arrayRow[j], this));
            }
        }
        return result;
    }

    private void connectCells() {
        cells.stream()
                .flatMap(Collection::stream)
                .forEach(Cell::initEmptyConnectedCells);
    }

}