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

    private void processCells(Collection<Cell> prevFilledCells, Collection<Cell> allFilledCells, Table table) {
        List<Cell> nextFilledCells = new ArrayList<>();
        prevFilledCells.forEach(cell -> processConnectedCells(cell, nextFilledCells));
        processExchangeableCells(table, nextFilledCells);
        processUniqueCells(table, nextFilledCells);
        processIntersectionCells(table, nextFilledCells);
        allFilledCells.addAll(nextFilledCells);
        if (nextFilledCells.isEmpty() || allFilledCells.size() == CELLS_NUMBER) {
            return;
        }
        processCells(nextFilledCells, allFilledCells, table);
    }

    private void processConnectedCells(Cell cell, List<Cell> filledCells) {
        cell.getActualEmptyConnectedCells()
                .filter(connectedCell -> connectedCell.tryExcludeVariantAndSetValue(cell.getValue()))
                .forEach(filledCells::add);
    }

    private void processExchangeableCells(Table table, List<Cell> filledCells) {
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processExchangeableCells(cell, filledCells, Cell::isFromThisRow));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processExchangeableCells(cell, filledCells, Cell::isFromThisColumn));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processExchangeableCells(cell, filledCells, Cell::isFromThisSubTable));
        }
    }

    private void processExchangeableCells(Cell cell, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> emptyCells = cell.getActualEmptyConnectedCellsAndThis();
        List<Cell> cells = emptyCells.filter(c -> condition.test(c, cell)).collect(Collectors.toList());
        getCellsByVariants(cells.stream())
                .filter(list -> list.size() > 1)
                .filter(list -> list.get(0).getRemainingVariants().size() == list.size())
                .forEach(list -> processExchangeableCells(list, cells, filledCells));
    }

    private void processExchangeableCells(List<Cell> exchangeableCells, List<Cell> cells, List<Cell> filledCells) {
        cells.stream()
                .filter(cell -> !exchangeableCells.contains(cell))
                .filter(cell -> cell.tryExcludeVariantsAndSetValue(exchangeableCells.get(0).getRemainingVariants()))
                .forEach(filledCells::add);
    }

    private void processUniqueCells(Table table, List<Cell> filledCells) {
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processUniqueCells(cell, filledCells, Cell::isFromThisRow));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processUniqueCells(cell, filledCells, Cell::isFromThisColumn));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processUniqueCells(cell, filledCells, Cell::isFromThisSubTable));
        }
    }

    private void processUniqueCells(Cell cell, List<Cell> filledCells, BiPredicate<Cell, Cell> condition) {
        Stream<Cell> emptyCells = cell.getActualEmptyConnectedCellsAndThis();
        List<Cell> cells = emptyCells.filter(c -> condition.test(c, cell)).collect(Collectors.toList());
        getCellsByVariant(cells.stream())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1)
                .filter(entry -> entry.getValue().get(0).trySetValue(entry.getKey()))
                .map(entry -> entry.getValue().get(0))
                .forEach(filledCells::add);
    }

    private void processIntersectionCells(Table table, List<Cell> filledCells) {
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processIntersectionCells(cell, filledCells,
                    Cell::isFromThisRow, Cell::isFromThisSubTable));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processIntersectionCells(cell, filledCells,
                    Cell::isFromThisColumn, Cell::isFromThisSubTable));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processIntersectionCells(cell, filledCells,
                    Cell::isFromThisSubTable, Cell::isFromThisRow));
        }
        if (filledCells.isEmpty()) {
            table.getEmptyCellStream().forEach(cell -> processIntersectionCells(cell, filledCells,
                    Cell::isFromThisSubTable, Cell::isFromThisColumn));
        }
    }

    private void processIntersectionCells(Cell cell, List<Cell> filledCells,
                                          BiPredicate<Cell, Cell> condition, BiPredicate<Cell, Cell> groupCondition) {
        Stream<Cell> emptyCells = cell.getActualEmptyConnectedCellsAndThis();
        List<Cell> cells = emptyCells.filter(c -> condition.test(c, cell)).collect(Collectors.toList());
        List<Cell> groupCells = emptyCells.filter(c -> groupCondition.test(c, cell)).collect(Collectors.toList());
        getCellsByVariant(cells.stream())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1 && entry.getValue().size() <= Cell.SUB_TABLE_ROW_NUMBER)
                .filter(entry -> entry.getValue().stream().allMatch(c -> groupCondition.test(c, cell)))
                .forEach(entry -> processIntersectionCells(entry.getValue(), groupCells, filledCells, entry.getKey()));
    }

    private void processIntersectionCells(List<Cell> intersectionCells, List<Cell> groupCells, List<Cell> filledCells,
                                          Integer variant) {
        groupCells.stream()
                .filter(cell -> !intersectionCells.contains(cell))
                .filter(c -> c.tryExcludeVariantAndSetValue(variant))
                .forEach(filledCells::add);
    }

    private Map<Integer, List<Cell>> getCellsByVariant(Stream<Cell> cellStream) {
        Map<Integer, List<Cell>> cells = new TreeMap<>();
        cellStream.forEach(cell ->
                cell.getRemainingVariants().forEach(variant ->
                        cells.computeIfAbsent(variant, v -> new ArrayList<>()).add(cell)));
        return cells;
    }

    private Stream<List<Cell>> getCellsByVariants(Stream<Cell> cellStream) {
        return cellStream
                .collect(Collectors.groupingBy(Cell::getVariantsAsKey))
                .values()
                .stream();
    }

}