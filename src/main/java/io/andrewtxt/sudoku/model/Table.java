package io.andrewtxt.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {

    public static final byte SIDE_SIZE = 9;

    private final List<List<Cell>> cells;

    public Table(Byte[][] values) {
        this.cells = toCells(values);
        connectCells();
    }

    public Stream<Cell> cells() {
        return cells.stream().flatMap(Collection::stream);
    }

    public Stream<Cell> filledCells() {
        return cells().filter(cell -> cell.getValue() != null);
    }

    public Stream<Cell> emptyCells() {
        return cells().filter(cell -> cell.getValue() == null);
    }

    public boolean hasEmptyCells() {
        return emptyCells().count() > 0;
    }

    public boolean hasCollisions() {
        return cells().anyMatch((Cell cell) -> hasValue(cell.connectedCells(), cell.getValue()));
    }

    @Override
    public String toString() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < SIDE_SIZE; i++) {
            String line = cells.get(i)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(""));
            lines.add(line);
        }
        return String.join("\n", lines);
    }

    private List<List<Cell>> toCells(Byte[][] values) {
        List<List<Cell>> result = new ArrayList<>();
        for (byte i = 0; i < SIDE_SIZE; i++) {
            result.add(new ArrayList<>());
            for (byte j = 0; j < SIDE_SIZE; j++) {
                result.get(i).add(new Cell(i, j, values[i][j], this));
            }
        }
        return result;
    }

    private void connectCells() {
        cells.stream().flatMap(Collection::stream).forEach(Cell::initEmptyConnectedCells);
    }

    private boolean hasValue(Stream<Cell> cells, Byte value) {
        return cells.map(Cell::getValue).filter(Objects::nonNull).anyMatch(cellValue -> cellValue.equals(value));
    }

}