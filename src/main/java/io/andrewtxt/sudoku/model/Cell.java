package io.andrewtxt.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Cell {

    private static final List<Integer> ALL_VARIANTS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final int row;
    private final int column;

    private int value;

    private final List<Integer> variants;

    public Cell(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.variants = new ArrayList<>();
        if (value == 0) {
            variants.addAll(ALL_VARIANTS);
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Integer> getVariants() {
        return variants;
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
        return row == cell.row && column == cell.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

}