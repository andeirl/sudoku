package io.andrewtxt.sudoku.model;

import java.util.List;

public class Row {

    private final List<Cell> cells;

    public Row(List<Cell> cells) {
        this.cells = cells;
    }

    public List<Cell> getCells() {
        return cells;
    }

}