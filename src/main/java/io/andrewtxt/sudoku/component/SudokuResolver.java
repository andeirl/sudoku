package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Cell;
import io.andrewtxt.sudoku.model.Table;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuResolver {

    private static final int CELLS_NUMBER = Table.ROW_NUMBER * Table.COLUMN_NUMBER;

    public Table resolve(int[][] values) {
        Clock clock = Clock.systemUTC();
        ZonedDateTime startTime = ZonedDateTime.now();

        Table table = new Table(values);
        List<Cell> filledCells = getFilledCells(table);
        tryFillCells(filledCells, filledCells, table);

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

    private void tryFillCells(List<Cell> prevFilledCells, List<Cell> allFilledCells, Table table) {
        List<Cell> nextFilledCells = new ArrayList<>();
        Collections.shuffle(prevFilledCells);
        prevFilledCells.forEach(cell -> tryFillEmptyConnectedCells(cell, nextFilledCells));
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            removeSameVariants(table, nextFilledCells);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            removeExclusiveVariants(table, nextFilledCells, Cell::isFromThisRow);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            removeExclusiveVariants(table, nextFilledCells, Cell::isFromThisColumn);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            removeExclusiveVariants(table, nextFilledCells, Cell::isFromThisSubTable);
        }
        allFilledCells.addAll(nextFilledCells);
        if (nextFilledCells.isEmpty()) {
            return;
        }
        tryFillCells(nextFilledCells, allFilledCells, table);
    }

    private void removeSameVariants(Table table, List<Cell> filledCells) {
        List<Cell> cells = table.getCellStream()
                .filter(cell -> cell.getValue() == null || cell.getValue().equals(0))
                .collect(Collectors.toList());
        cells.forEach(cell -> removeSameVariants(cell, filledCells));
    }

    private void removeSameVariants(Cell cell, List<Cell> filledCells) {
        List<Cell> cells = cell.getActualEmptyConnectedCells().collect(Collectors.toList());
        cells.add(cell);
        tryFillSameVariantsConnectedCells(cells.stream().filter((Cell c) -> c.isFromThisRow(cell)), filledCells);
        tryFillSameVariantsConnectedCells(cells.stream().filter((Cell c) -> c.isFromThisColumn(cell)), filledCells);
        tryFillSameVariantsConnectedCells(cells.stream().filter((Cell c) -> c.isFromThisSubTable(cell)), filledCells);
    }

    private void tryFillSameVariantsConnectedCells(Stream<Cell> cellStream, List<Cell> filledCells) {
        List<Cell> cells = cellStream.collect(Collectors.toList());
        for (Cell cell : cells) {
            List<Cell> implicitlyConnectedCells = cells
                    .stream()
                    .filter(c -> c.getRemainingVariants().size() == cell.getRemainingVariants().size())
                    .filter(c -> c.getRemainingVariants().containsAll(cell.getRemainingVariants()))
                    .collect(Collectors.toList());
            if (implicitlyConnectedCells.size() == cell.getRemainingVariants().size() &&
                    cell.getRemainingVariants().size() < Table.ROW_NUMBER) {
                cells.stream()
                        .filter(c -> !implicitlyConnectedCells.contains(c))
                        .forEach(c -> {
                            c.tryExcludeVariantsAndSetValue(cell.getRemainingVariants());
                            if (c.getValue() != null) {
                                filledCells.add(c);
                            }
                        });
            }
        }
    }

    private void removeExclusiveVariants(Table table, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        List<Cell> cells = table.getCellStream()
                .filter(cell -> cell.getValue() == null || cell.getValue().equals(0))
                .collect(Collectors.toList());
        cells.forEach(cell -> removeExclusiveVariants(cell, filledCells, condition));
    }

    private void removeExclusiveVariants(Cell cell, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        List<Cell> cells = cell.getActualEmptyConnectedCells().collect(Collectors.toList());
        cells.add(cell);
        tryFillExclusiveVariantsConnectedCells(cells.stream().filter(c -> condition.test(c, cell)), filledCells);
    }

    private void tryFillExclusiveVariantsConnectedCells(Stream<Cell> cellStream, List<Cell> filledCells) {
        Map<Integer, List<Cell>> cells = new TreeMap<>();
        cellStream.forEach(cell ->
                cell.getRemainingVariants().forEach(variant ->
                        cells.computeIfAbsent(variant, v -> new ArrayList<>()).add(cell)));
        cells.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> {
                    Cell cell = entry.getValue().get(0);
                    cell.trySetValue(entry.getKey());
                    if (cell.getValue() != null) {
                        filledCells.add(cell);
                    }
                });
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