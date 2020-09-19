package io.andrewtxt.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cell {

    public static final int SUB_TABLE_ROW_NUMBER = 3;
    public static final int SUB_TABLE_COLUMN_NUMBER = 3;

    private static final String VALUE_FORMAT = "         ";

    private static final List<Integer> VARIANTS = IntStream
            .range(1, Cell.SUB_TABLE_ROW_NUMBER * Cell.SUB_TABLE_COLUMN_NUMBER + 1)
            .boxed()
            .collect(Collectors.toList());

    private final int rowIndex;
    private final int columnIndex;

    private Integer value;
    private List<Cell> initialEmptyConnectedCells;

    private final List<Integer> remainingVariants;
    private final Table parentTable;

    public Cell(int rowIndex, int columnIndex, int value, Table parentTable) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.value = value == 0 ? null : value;
        this.parentTable = parentTable;
        this.remainingVariants = new ArrayList<>();
        if (this.value == null) {
            remainingVariants.addAll(VARIANTS);
        }
    }

    public Integer getValue() {
        return value;
    }

    public List<Integer> getRemainingVariants() {
        return remainingVariants;
    }

    public Stream<Cell> getActualEmptyConnectedCells() {
        return initialEmptyConnectedCells
                .stream()
                .filter(cell -> cell.getValue() == null);
    }

    public Stream<Cell> getActualEmptyConnectedCellsAndThis() {
        return Stream.concat(getActualEmptyConnectedCells(), Stream.of(this));
    }

    public Stream<Cell> getConnectedCells() {
        return parentTable.getCellStream()
                .filter(this::isConnected);
    }

    public void tryExcludeVariantAndSetValue(Integer variantToExclude) {
        remainingVariants.remove(variantToExclude);
        if (remainingVariants.size() == 1) {
            value = remainingVariants.remove(0);
        }
    }

    public void tryExcludeVariantsAndSetValue(List<Integer> variantsToExclude) {
        variantsToExclude.forEach(this::tryExcludeVariantAndSetValue);
    }

    public void trySetValue(Integer value) {
        if (remainingVariants.contains(value)) {
            this.value = value;
            remainingVariants.clear();
        }
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
            String variantsStr = remainingVariants
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(""));
            return " " + variantsStr + VALUE_FORMAT.substring(variantsStr.length()) + " ";
        }
        return " " + value.toString() + VALUE_FORMAT.substring(1) + " ";
    }

    void initEmptyConnectedCells() {
        this.initialEmptyConnectedCells = getConnectedCells().collect(Collectors.toList());
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