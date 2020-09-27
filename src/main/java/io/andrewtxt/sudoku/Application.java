package io.andrewtxt.sudoku;

import io.andrewtxt.sudoku.component.DataReader;
import io.andrewtxt.sudoku.component.SudokuResolver;

import java.util.logging.Logger;

public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        LOG.info("Application is started");
        Byte[][] data = new DataReader().readData();
        new SudokuResolver().resolve(data);
        LOG.info("Application is stopped");
    }

}