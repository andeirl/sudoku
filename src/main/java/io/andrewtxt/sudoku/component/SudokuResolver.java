package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Cell;
import io.andrewtxt.sudoku.model.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuResolver {

    private static final int CELLS_NUMBER = Table.ROW_NUMBER * Table.COLUMN_NUMBER;

    public void resolve(int[][] values) {
        Table table = new Table(values);
        List<Cell> notEmptyCells = getNotEmptyCells(table);
        tryFillCells(notEmptyCells, notEmptyCells);
        System.out.println(table);
    }

    private List<Cell> getNotEmptyCells(Table table) {
        return table.getCellStream()
                .filter(cell -> cell.getValue() != null)
                .collect(Collectors.toList());
    }

    private void tryFillCells(List<Cell> oldGen, List<Cell> allFilledCells) {
        if (oldGen.isEmpty()) {
            return;
        }
        List<Cell> newGen = new ArrayList<>();
        Collections.shuffle(oldGen);
        oldGen.forEach(cell -> tryFillEmptyConnectedCells(cell, newGen));
        allFilledCells.addAll(newGen);
        tryFillCells(newGen, allFilledCells);
    }

    private void tryFillEmptyConnectedCells(Cell cell, List<Cell> filledCells) {
        Stream<Cell> emptyConnectedCells = cell.getActualEmptyConnectedCells();
        emptyConnectedCells
                .filter(connectedCell -> tryFillEmptyConnectedCell(connectedCell, cell.getValue()))
                .forEach(filledCells::add);
    }

    private boolean tryFillEmptyConnectedCell(Cell cell, Integer variantToExclude) {
        cell.tryExcludeVariantAndSetValue(variantToExclude);
        return cell.getValue() != null;
    }

}