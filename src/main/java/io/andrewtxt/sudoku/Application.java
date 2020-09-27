package io.andrewtxt.sudoku;

import io.andrewtxt.sudoku.component.DataReader;
import io.andrewtxt.sudoku.component.SudokuResolver;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is started");
        Byte[][] data = new DataReader().readData();
        new SudokuResolver().resolve(data);
        System.out.println("Application is stopped");
    }

}