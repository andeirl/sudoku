package io.andrewtxt.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cell {

    public static final int SUB_TABLE_ROW_NUMBER = 3;
    public static final int SUB_TABLE_COLUMN_NUMBER = 3;

    private static final String VALUE_FORMAT = "         ";

    private static final List<Byte> VARIANTS = IntStream
            .range(1, Cell.SUB_TABLE_ROW_NUMBER * Cell.SUB_TABLE_COLUMN_NUMBER + 1)
            .boxed()
            .map(Integer::byteValue)
            .collect(Collectors.toList());

    private final int rowIndex;
    private final int columnIndex;

    private Byte value;
    private List<Cell> initialEmptyConnectedCells;

    private final List<Byte> remainingVariants;
    private final Table parentTable;

    public Cell(int rowIndex, int columnIndex, byte value, Table parentTable) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.value = value == 0 ? null : value;
        this.parentTable = parentTable;
        this.remainingVariants = new ArrayList<>();
        if (this.value == null) {
            remainingVariants.addAll(VARIANTS);
        }
    }

    public Byte getValue() {
        return value;
    }

    public List<Byte> getRemainingVariants() {
        return remainingVariants;
    }

    public Stream<Cell> actualEmptyConnectedCells() {
        return initialEmptyConnectedCells.stream().filter(cell -> cell.getValue() == null);
    }

    public Stream<Cell> actualEmptyConnectedCellsAndThis() {
        return Stream.concat(actualEmptyConnectedCells(), Stream.of(this));
    }

    public Stream<Cell> connectedCells() {
        return parentTable.cells().filter(this::isConnected);
    }

    public String getVariantsAsKey() {
        return remainingVariants
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
    }

    public boolean tryExcludeVariantAndSetValue(Byte variantToExclude) {
        remainingVariants.remove(variantToExclude);
        if (remainingVariants.size() == 1) {
            value = remainingVariants.remove(0);
        }
        return value != null;
    }

    public boolean tryExcludeVariantsAndSetValue(List<Byte> variantsToExclude) {
        variantsToExclude.forEach(this::tryExcludeVariantAndSetValue);
        return value != null;
    }

    public boolean trySetValue(Byte value) {
        if (remainingVariants.contains(value)) {
            this.value = value;
            remainingVariants.clear();
        }
        return value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cell cell = (Cell) o;
        return rowIndex == cell.rowIndex && columnIndex == cell.columnIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowIndex, columnIndex);
    }

    @Override
    public String toString() {
        if (value == null) {
            String variantsStr = getVariantsAsKey();
            return " " + variantsStr + VALUE_FORMAT.substring(variantsStr.length()) + " ";
        }
        return " " + value.toString() + VALUE_FORMAT.substring(1) + " ";
    }

    void initEmptyConnectedCells() {
        this.initialEmptyConnectedCells = connectedCells().collect(Collectors.toList());
    }

    private boolean isConnected(Cell cell) {
        return !this.equals(cell) &&
                (isFromThisRow(cell) || isFromThisColumn(cell) || isFromThisSubTable(cell));
    }

    public boolean isFromThisRow(Cell cell) {
        return rowIndex == cell.rowIndex;
    }

    public boolean isFromThisColumn(Cell cell) {
        return columnIndex == cell.columnIndex;
    }

    public boolean isFromThisSubTable(Cell cell) {
        return (rowIndex / SUB_TABLE_ROW_NUMBER == cell.rowIndex / SUB_TABLE_ROW_NUMBER) &&
                (columnIndex / SUB_TABLE_COLUMN_NUMBER == cell.columnIndex / SUB_TABLE_COLUMN_NUMBER);
    }

}