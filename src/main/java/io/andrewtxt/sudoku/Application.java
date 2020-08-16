package io.andrewtxt.sudoku;

import io.andrewtxt.sudoku.component.DataReader;
import io.andrewtxt.sudoku.component.SudokuResolver;

public class Application {

    public static void main(String[] args) {
        int[][] data = new DataReader().readData();
        new SudokuResolver().resolve(data);
        System.out.println("Application started");
    }

}