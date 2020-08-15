package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.andrewtxt.sudoku.model.Table.COLUMN_NUMBER;
import static io.andrewtxt.sudoku.model.Table.ROW_NUMBER;

public class Cell {

    private static final List<Integer> VARIANTS = IntStream
            .range(1, ROW_NUMBER * COLUMN_NUMBER + 1)
            .boxed()
            .collect(Collectors.toList());

    private final int rowIndex;
    private final int columnIndex;

    private int value;

    private final List<Integer> remainingVariants;

    public Cell(int rowIndex, int columnIndex, int value) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.value = value;
        this.remainingVariants = new ArrayList<>();
        if (value == 0) {
            remainingVariants.addAll(VARIANTS);
        }
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Integer> getRemainingVariants() {
        return remainingVariants;
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