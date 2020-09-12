package io.andrewtxt.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {

    public static final int ROW_NUMBER = 9;
    public static final int COLUMN_NUMBER = 9;

    private final List<List<Cell>> cells;

    public Table(int[][] values) {
        this.cells = toCells(values);
        connectCells();
    }

    public Stream<Cell> getCellStream() {
        return cells.stream().flatMap(Collection::stream);
    }

    public boolean hasEmptyCells() {
        return getCellStream()
                .map(Cell::getValue)
                .anyMatch((Integer value) -> value == null || value.equals(0));
    }

    public boolean hasCollisions() {
        return getCellStream()
                .anyMatch((Cell cell) -> hasValue(cell.getConnectedCells(), cell.getValue()));
    }

    @Override
    public String toString() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            String line = cells.get(i)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(""));
            lines.add(line);
        }
        return String.join("\n", lines);
    }

    private List<List<Cell>> toCells(int[][] values) {
        List<List<Cell>> result = new ArrayList<>();
        for (int i = 0; i < ROW_NUMBER; i++) {
            result.add(new ArrayList<>());
            for (int j = 0; j < COLUMN_NUMBER; j++) {
                result.get(i).add(new Cell(i, j, values[i][j], this));
            }
        }
        return result;
    }

    private void connectCells() {
        cells.stream()
                .flatMap(Collection::stream)
                .forEach(Cell::initEmptyConnectedCells);
    }

    private boolean hasValue(Stream<Cell> cellStream, Integer value) {
        return cellStream
                .map(Cell::getValue)
                .filter(Objects::nonNull)
                .anyMatch(cellValue -> cellValue.equals(value));
    }

}