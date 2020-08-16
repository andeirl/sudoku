package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Cell;
import io.andrewtxt.sudoku.model.SuperTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuResolver {

    private static final int CELLS_NUMBER = SuperTable.ROW_NUMBER * SuperTable.COLUMN_NUMBER;

    public void resolve(int[][] values) {
        SuperTable superTable = new SuperTable(values);
        List<Cell> notEmptyCells = getNotEmptyCells(superTable);
        tryFillCells(notEmptyCells, notEmptyCells);
        System.out.println(superTable);
    }

    private List<Cell> getNotEmptyCells(SuperTable superTable) {
        return superTable.getCells()
                .stream()
                .flatMap(Collection::stream)
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