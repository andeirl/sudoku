package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cell {

    private static final List<Integer> VARIANTS = IntStream
            .range(1, Table.ROW_NUMBER * Table.COLUMN_NUMBER + 1)
            .boxed()
            .collect(Collectors.toList());

    private final int rowIndex;
    private final int columnIndex;

    private Integer value;

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

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getParentRowIndex() {
        return parentTable.getRowIndex();
    }

    public int getParentColumnIndex() {
        return parentTable.getColumnIndex();
    }

    public Integer getValue() {
        return value;
    }

    public Stream<Cell> getEmptyConnectedCells(int rowIndex, int columnIndex) {
        return parentTable.getEmptyConnectedCells(rowIndex, columnIndex);
    }

    public void tryExcludeVariantAndSetValue(Integer variantToExclude) {
        remainingVariants.remove(variantToExclude);
        if (remainingVariants.size() == 1) {
            value = remainingVariants.remove(0);
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

}