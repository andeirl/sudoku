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
        Set<Cell> filledCells = table.getFilledCellStream().collect(Collectors.toSet());
        processCells(filledCells, filledCells, table);

        boolean solved = filledCells.size() == CELLS_NUMBER;
        long milliseconds = Duration.between(startTime, ZonedDateTime.now(clock)).toMillis();

        System.out.println(solved ? "Sudoku is solved" : "Sudoku has no solution");
        System.out.println("Time of algorithm working: " + milliseconds + " milliseconds");
        System.out.println(table);

        return table;
    }

    private Map<Integer, List<Cell>> getCellsByVariant(Stream<Cell> cellStream) {
        Map<Integer, List<Cell>> cells = new TreeMap<>();
        cellStream.forEach(cell ->
                cell.getRemainingVariants().forEach(variant ->
                        cells.computeIfAbsent(variant, v -> new ArrayList<>()).add(cell)));
        return cells;
    }

    private void processCells(Collection<Cell> prevFilledCells, Collection<Cell> allFilledCells, Table table) {
        List<Cell> nextFilledCells = new ArrayList<>();
        prevFilledCells.forEach(cell -> processConnectedCells(cell, nextFilledCells));
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processExchangeableCells(table, nextFilledCells, Cell::isFromThisRow);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processExchangeableCells(table, nextFilledCells, Cell::isFromThisColumn);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processExchangeableCells(table, nextFilledCells, Cell::isFromThisSubTable);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processUniqueCells(table, nextFilledCells, Cell::isFromThisRow);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processUniqueCells(table, nextFilledCells, Cell::isFromThisColumn);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processUniqueCells(table, nextFilledCells, Cell::isFromThisSubTable);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processIntersectionCells(table, nextFilledCells, Cell::isFromThisRow, Cell::isFromThisSubTable);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processIntersectionCells(table, nextFilledCells, Cell::isFromThisColumn, Cell::isFromThisSubTable);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processIntersectionCells(table, nextFilledCells, Cell::isFromThisSubTable, Cell::isFromThisRow);
        }
        if (nextFilledCells.isEmpty() && allFilledCells.size() < CELLS_NUMBER) {
            processIntersectionCells(table, nextFilledCells, Cell::isFromThisSubTable, Cell::isFromThisColumn);
        }
        allFilledCells.addAll(nextFilledCells);
        if (nextFilledCells.isEmpty()) {
            return;
        }
        processCells(nextFilledCells, allFilledCells, table);
    }

    private void processConnectedCells(Cell cell, List<Cell> filledCells) {
        cell.getActualEmptyConnectedCells()
                .filter(connectedCell -> connectedCell.tryExcludeVariantAndSetValue(cell.getValue()))
                .forEach(filledCells::add);
    }

    private void processExchangeableCells(Table table, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> cells = table.getEmptyCellStream();
        cells.forEach(cell -> processExchangeableCells(cell, filledCells, condition));
    }

    private void processExchangeableCells(Cell cell, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> cells = cell.getActualEmptyConnectedCellsAndThis();
        processExchangeableCells(cells.filter(c -> condition.test(c, cell)), filledCells);
    }

    private void processExchangeableCells(Stream<Cell> cellStream, List<Cell> filledCells) {
        List<Cell> cells = cellStream.collect(Collectors.toList());
        for (Cell cell : cells) {
            List<Cell> sameVariantsConnectedCells = cells
                    .stream()
                    .filter(c -> c.getRemainingVariants().size() == cell.getRemainingVariants().size())
                    .filter(c -> c.getRemainingVariants().containsAll(cell.getRemainingVariants()))
                    .collect(Collectors.toList());
            if (sameVariantsConnectedCells.size() == cell.getRemainingVariants().size() &&
                    cell.getRemainingVariants().size() < Table.ROW_NUMBER) {
                cells.stream()
                        .filter(c -> !sameVariantsConnectedCells.contains(c))
                        .filter(c -> c.tryExcludeVariantsAndSetValue(cell.getRemainingVariants()))
                        .forEach(filledCells::add);
            }
        }
    }

    private void processUniqueCells(Table table, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> cells = table.getEmptyCellStream();
        cells.forEach(cell -> processUniqueCells(cell, filledCells, condition));
    }

    private void processUniqueCells(Cell cell, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> cells = cell.getActualEmptyConnectedCellsAndThis();
        processUniqueCells(cells.filter(c -> condition.test(c, cell)), filledCells);
    }

    private void processUniqueCells(Stream<Cell> cellStream, List<Cell> filledCells) {
        Map<Integer, List<Cell>> cells = getCellsByVariant(cellStream);
        cells.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .filter(entry -> entry.getValue().get(0).trySetValue(entry.getKey()))
                .map(entry -> entry.getValue().get(0))
                .forEach(filledCells::add);
    }

    private void processIntersectionCells(Table table, List<Cell> filledCells,
                                       BiPredicate<Cell, Cell> condition, BiPredicate<Cell, Cell> groupCondition) {
        Stream<Cell> cells = table.getEmptyCellStream();
        cells.forEach(cell -> processIntersectionCells(cell, filledCells, condition, groupCondition));
    }

    private void processIntersectionCells(Cell cell, List<Cell> filledCells,
                                       BiPredicate<Cell, Cell> condition, BiPredicate<Cell, Cell> groupCondition) {
        Stream<Cell> cells = cell.getActualEmptyConnectedCellsAndThis();
        processIntersectionCells(cell, cells.filter(c ->
                condition.test(c, cell)), filledCells, condition, groupCondition);
    }

    private void processIntersectionCells(Cell cell, Stream<Cell> cellStream, List<Cell> filledCells,
                                                      BiPredicate<Cell, Cell> condition,
                                                      BiPredicate<Cell, Cell> groupCondition) {
        Map<Integer, List<Cell>> cells = getCellsByVariant(cellStream);
        List<Integer> variants = cells.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1 && entry.getValue().size() <= Cell.SUB_TABLE_ROW_NUMBER)
                .filter(entry -> entry.getValue().stream().allMatch(c -> groupCondition.test(c, cell)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        cell.getActualEmptyConnectedCells()
                .filter(c -> !condition.test(c, cell))
                .filter(c -> groupCondition.test(c, cell))
                .filter(c -> c.tryExcludeVariantsAndSetValue(variants))
                .forEach(filledCells::add);
    }

}