package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Cell;
import io.andrewtxt.sudoku.model.Table;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuResolver {

    private static final int CELLS_NUMBER = Table.ROW_NUMBER * Table.COLUMN_NUMBER;

    public Table resolve(int[][] values) {
        Clock clock = Clock.systemUTC();
        ZonedDateTime startTime = ZonedDateTime.now();

        Table table = new Table(values);
        List<Cell> filledCells = getFilledCells(table);
        tryFillCells(filledCells, filledCells);

        boolean solved = filledCells.size() == CELLS_NUMBER;
        long milliseconds = Duration.between(startTime, ZonedDateTime.now(clock)).toMillis();

        System.out.println(solved ? "Sudoku is solved" : "Sudoku has no solution");
        System.out.println("Time of algorithm working: " + milliseconds + " milliseconds");
        System.out.println(table);

        return table;
    }

    private List<Cell> getFilledCells(Table table) {
        return table.getCellStream()
                .filter(cell -> cell.getValue() != null)
                .collect(Collectors.toList());
    }

    private void tryFillCells(List<Cell> prevFilledCells, List<Cell> allFilledCells) {
        List<Cell> nextFilledCells = new ArrayList<>();
        Collections.shuffle(prevFilledCells);
        prevFilledCells.forEach(cell -> tryFillEmptyConnectedCells(cell, nextFilledCells));
        allFilledCells.addAll(nextFilledCells);
        if (nextFilledCells.isEmpty()) {
            return;
        }
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