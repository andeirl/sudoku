package io.andrewtxt.sudoku.component;

import io.andrewtxt.sudoku.model.Table;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SudokuResolverTest {

    @Test
    public void testSuccess() {
        int[][] values = new int[][] {
                {0, 1, 3, 8, 0, 0, 4, 0, 5},
                {0, 2, 4, 6, 0, 5, 0, 0, 0},
                {0, 8, 7, 0, 0, 0, 9, 3, 0},
                {4, 9, 0, 3, 0, 6, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 5, 0, 0},
                {0, 0, 0, 7, 0, 1, 0, 9, 3},
                {0, 6, 9, 0, 0, 0, 7, 4, 0},
                {0, 0, 0, 2, 0, 7, 6, 8, 0},
                {1, 0, 2, 0, 0, 8, 3, 5, 0}
        };
        Table table = new SudokuResolver().resolve(values);
        Assertions.assertThat(table.getCellStream().noneMatch(cell -> cell.getValue() == null));
    }

}