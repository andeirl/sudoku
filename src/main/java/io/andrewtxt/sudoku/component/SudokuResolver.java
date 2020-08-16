package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Cell;
import io.andrewtxt.sudoku.model.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuResolver {

    private static final int CELLS_NUMBER = Table.ROW_NUMBER * Table.COLUMN_NUMBER;

    public void resolve(int[][] values) {
        Table table = new Table(values);
        List<Cell> filledCells = getFilledCells(table);
        tryFillCells(filledCells, filledCells);
        System.out.println(table);
    }

    private List<Cell> getFilledCells(Table table) {
        return table.getCellStream()
                .filter(cell -> cell.getValue() != null)
                .collect(Collectors.toList());
    }

    private void tryFillCells(List<Cell> prevFilledCells, List<Cell> allFilledCells) {
        if (prevFilledCells.isEmpty()) {
            return;
        }
        List<Cell> nextFilledCells = new ArrayList<>();
        Collections.shuffle(prevFilledCells);
        prevFilledCells.forEach(cell -> tryFillEmptyConnectedCells(cell, nextFilledCells));
        allFilledCells.addAll(nextFilledCells);
        tryFillCells(nextFilledCells, allFilledCells);
    }

    private void tryFillEmptyConnectedCells(Cell cell, List<Cell> filledCells) {
        cell.getActualEmptyConnectedCells()
                .filter(connectedCell -> tryFillEmptyConnectedCell(connectedCell, cell.getValue()))
                .forEach(filledCells::add);
    }

    private boolean tryFillEmptyConnectedCell(Cell cell, Integer variantToExclude) {
        cell.tryExcludeVariantAndSetValue(variantToExclude);
        return cell.getValue() != null;
    }

}