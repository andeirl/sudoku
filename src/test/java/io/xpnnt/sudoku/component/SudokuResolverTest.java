package io.xpnnt.sudoku.component;

import io.xpnnt.sudoku.model.Table;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SudokuResolverTest {

    @Test
    public void testSuccessSimple() {
        Byte[][] values = new Byte[][] {
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
        Assertions.assertThat(table.hasEmptyCells()).isFalse();
        Assertions.assertThat(table.hasCollisions()).isFalse();
    }

    @Test
    public void testSuccessComplex() {
        Byte[][] values = new Byte[][] {
                {0, 0, 2, 0, 0, 0, 0, 4, 1},
                {0, 0, 0, 0, 8, 2, 0, 7, 0},
                {0, 0, 0, 0, 4, 0, 0, 0, 9},
                {2, 0, 0, 0, 7, 9, 3, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 8, 0},
                {0, 0, 6, 8, 1, 0, 0, 0, 4},
                {1, 0, 0, 0, 9, 0, 0, 0, 0},
                {0, 6, 0, 4, 3, 0, 0, 0, 0},
                {8, 5, 0, 0, 0, 0, 4, 0, 0}
        };
        Table table = new SudokuResolver().resolve(values);
        Assertions.assertThat(table.hasEmptyCells()).isFalse();
        Assertions.assertThat(table.hasCollisions()).isFalse();
    }

}