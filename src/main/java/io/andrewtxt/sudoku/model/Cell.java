package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cell {

    private static final int SUB_TABLE_ROW_NUMBER = 3;
    private static final int SUB_TABLE_COLUMN_NUMBER = 3;

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

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public Integer getValue() {
        return value;
    }

    public Stream<Cell> getActualEmptyConnectedCells() {
        return initialEmptyConnectedCells
                .stream()
                .filter(cell -> cell.getValue() == null);
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

    void initEmptyConnectedCells() {
        List<Cell> initialEmptyCells = parentTable.getCells()
                .stream()
                .flatMap(Collection::stream)
                .filter(this::isConnected)
                .collect(Collectors.toList());
        this.initialEmptyConnectedCells = initialEmptyCells;
    }

    private boolean isConnected(Cell cell) {
        return !this.equals(cell) && (fromThisRow(cell) || fromThisColumn(cell) || fromThisSubTable(cell));
    }

    private boolean fromThisRow(Cell cell) {
        return rowIndex == cell.rowIndex;
    }

    private boolean fromThisColumn(Cell cell) {
        return columnIndex == cell.columnIndex;
    }

    private boolean fromThisSubTable(Cell cell) {
        return (rowIndex / SUB_TABLE_ROW_NUMBER == cell.rowIndex / SUB_TABLE_ROW_NUMBER) &&
                (columnIndex / SUB_TABLE_COLUMN_NUMBER == cell.columnIndex / SUB_TABLE_COLUMN_NUMBER);
    }

}