package org.myshelfie.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.commonGoal.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonGoalCardTest {

    /*
        BOOKSHELFS
     */
    Bookshelf bksEmpty;
    Bookshelf bksComplete, bksOneTile, bksSemiComplete;
    Bookshelf bksCats;
    Bookshelf bksCorners;
    Bookshelf bksCross;
    Bookshelf bksDiagonal;
    Bookshelf bksSquare;
    Bookshelf bksStair;
    Bookshelf bksDiagonalReverse;
    Bookshelf bksHollowCorner;
    Bookshelf bksHalfFilledSquares;

    /*
        COMMON GOAL CARDS
     */
    CommonGoalCard eightEqualsTilesCard;
    CommonGoalCard equalCornersCard;
    CommonGoalCard crossTilesCard;
    CommonGoalCard diagonalTilesCard;
    CommonGoalCard squareTilesCard;
    CommonGoalCard stairTilesCard;
    CommonGoalCard sameTypeGroupings_4x5;
    CommonGoalCard sameTypeGroupings_2x3;
    CommonGoalCard sameTypeGroupings_1x10;
    CommonGoalCard slightlyDifferentLines_3row_2to3;
    CommonGoalCard slightlyDifferentLines_4row_1to3;
    CommonGoalCard slightlyDifferentLines_2col_6to6;


    // Utility method to fill a bookshelf
    private void fillBookshelf(Bookshelf b, int[][] mat) {
        for (int r = Bookshelf.NUMROWS - 1; r >= 0; r--) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (mat[r][c] != -1) {
                        b.insertTile(new Tile(ItemType.values()[mat[r][c]]), c);
                    }
                } catch (WrongArgumentException e) {
                    // do nothing
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize all the possible common goal cards
        eightEqualsTilesCard = new EqualEight("EqualEightCard");
        equalCornersCard = new EqualCorners("EqualCornersCard");
        crossTilesCard = new CrossTiles("CrossTilesCard");
        diagonalTilesCard = new DiagonalTiles("DiagonalTilesCard");
        squareTilesCard = new SquareTiles("SquareTilesCard");
        stairTilesCard = new StairTiles("StairTilesCard");
        sameTypeGroupings_4x5 = new SameTypeGroupings("SameTypeGroupings_4x5",4, 5);     // requires 5 groups of at least 3 cards of same type -> TRUE
        sameTypeGroupings_2x3 = new SameTypeGroupings("SameTypeGroupings_2x3", 2, 3);     // requires 4 groups of at least 3 cards of same type -> TRUE
        sameTypeGroupings_1x10 = new SameTypeGroupings("SameTypeGroupings_1x10", 1, 10);  // requires one group of at least 7 cards of same type -> FALSE
        slightlyDifferentLines_3row_2to3 = new SlightlyDifferentLines("slightlyDifferentLines_3row_2to3", false, 3, 2, 3);
        slightlyDifferentLines_4row_1to3 = new SlightlyDifferentLines("slightlyDifferentLines_4row_1to3",false, 3, 1, 4);
        slightlyDifferentLines_2col_6to6 = new SlightlyDifferentLines("slightlyDifferentLines_2col_6to6",  true, 6, 6, 2);

        // Create a bunch of different bookshelves        
        int[][] complete = {
                {5,  5,  0,  0,  3},
                {5,  5,  5,  4,  2},
                {1,  1,  1,  5,  2},
                {1,  1,  4,  1,  2},
                {1,  4,  1,  1,  2},
                {3,  1,  1,  2,  2}
        };

        int[][] oneTile = {
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1,  0, -1, -1, -1}
        };

        int[][] semiComplete = {
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1,  5},
                {-1,  4, -1, -1,  2},
                { 3,  3, -1,  0,  5},
                { 3,  1,  3,  4,  0}
        };

        int[][] cats = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] corners = {
                {3, 5, 0, -1, 3},
                {5, 5, 5, 2, 2},
                {1, 1, 5, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 0, 1, 5, 2},
                {3, 1, 1, 2, 3}
        };

        int[][] cross = {
                {3, 5, 0, -1, 3},
                {5, 5, 5, 2, 2},
                {4, 1, 4, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 0, 4, 5, 2},
                {3, 1, 1, 2, 3}
        };

        int[][] diagonal = {
                {3, 5, 0, -1, 3},
                {5, 3, 5, 2, 2},
                {4, 1, 3, 5, 0},
                {1, 4, 2, 3, 0},
                {4, 0, 4, 5, 3},
                {0, 1, 1, 2, 3}
        };

        int[][] diagonalReverse = {
                {3, 5, -1, -1, 2},
                {5, 3, -1,  2, 2},
                {4, 1, -1,  2, 0},
                {1, 2,  2,  3, 0},
                {2, 2,  4,  5, 3},
                {2, 1,  1,  2, 3}
        };

        int[][] square = {
                {1, 2, 0, -1, 3},
                {2, 2, 5, 2, 2},
                {4, 1, 3, 2, 2},
                {1, 4, 5, 5, 0},
                {4, 0, 5, 5, 3},
                {0, 1, 1, 2, 3}
        };

        int[][] stair = {
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1,  2},
                {-1, -1, -1,  2,  2},
                {-1, -1,  5,  5,  0},
                {-1,  0,  5,  5,  3},
                {0,   1,  1,  2,  3}
        };


        int[][] hollowCorner = {
                {3, -1, -1, -1, -1},
                {2, -1, -1, -1,  2},
                {1, -1, -1,  2,  2},
                {5, -1,  5,  5,  0},
                {1,  0,  5,  5,  3},
                {0,  1,  1,  2,  3}
        };

        int[][] halfFilledSquares = {
                {-1, -1,  2, -1,  -1},
                {-1, -1,  2,  4,  -1},
                {-1,  2,  2,  3,  -1},
                {-1,  3,  3,  2,  -1},
                {-1,  2,  2,  5,  -1},
                {-1,  2,  2,  4,  -1}
        };

        bksComplete = new Bookshelf();
        fillBookshelf(bksComplete, complete);

        bksOneTile = new Bookshelf();
        fillBookshelf(bksOneTile, oneTile);

        bksSemiComplete = new Bookshelf();
        fillBookshelf(bksSemiComplete, semiComplete);

        bksCats = new Bookshelf();
        fillBookshelf(bksCats, cats);

        bksCorners = new Bookshelf();
        fillBookshelf(bksCorners, corners);

        bksCross = new Bookshelf();
        fillBookshelf(bksCross, cross);

        bksDiagonal = new Bookshelf();
        fillBookshelf(bksDiagonal, diagonal);

        bksSquare = new Bookshelf();
        fillBookshelf(bksSquare, square);

        bksStair = new Bookshelf();
        fillBookshelf(bksStair, stair);

        bksDiagonalReverse = new Bookshelf();
        fillBookshelf(bksDiagonalReverse, diagonalReverse);

        bksHollowCorner = new Bookshelf();
        fillBookshelf(bksHollowCorner, hollowCorner);

        bksHalfFilledSquares = new Bookshelf();
        fillBookshelf(bksHalfFilledSquares, halfFilledSquares);

        bksEmpty = new Bookshelf();
    }

    // Test all the common goals using the different bookshelves
    
    @Test
    void testEightEquals() throws WrongArgumentException {
        assertEquals(Boolean.TRUE, eightEqualsTilesCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, eightEqualsTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, eightEqualsTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, eightEqualsTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, eightEqualsTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.TRUE, eightEqualsTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, eightEqualsTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, eightEqualsTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void testEqualCorners() throws WrongArgumentException {
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksHollowCorner));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void testTilesCross() throws WrongArgumentException {
        assertEquals(Boolean.TRUE, crossTilesCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, crossTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.TRUE, crossTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void testSameTypeDiagonal() throws WrongArgumentException {
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, diagonalTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.TRUE, diagonalTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksEmpty));
        assertEquals(Boolean.TRUE, diagonalTilesCard.checkGoalSatisfied(bksDiagonalReverse));
    }

    @Test
    void testTwoSquares() throws WrongArgumentException {
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.TRUE, squareTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksEmpty));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksHalfFilledSquares));
    }

    @Test
    void testStairTiles() throws WrongArgumentException {
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.TRUE, stairTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void testSameTypeGroupings() throws WrongArgumentException {
        assertEquals(Boolean.TRUE, sameTypeGroupings_4x5.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, sameTypeGroupings_2x3.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.FALSE, sameTypeGroupings_1x10.checkGoalSatisfied(bksComplete));

        assertEquals(Boolean.FALSE, sameTypeGroupings_4x5.checkGoalSatisfied(bksOneTile));
        assertEquals(Boolean.FALSE, sameTypeGroupings_2x3.checkGoalSatisfied(bksOneTile));
        assertEquals(Boolean.FALSE, sameTypeGroupings_1x10.checkGoalSatisfied(bksOneTile));

        assertEquals(Boolean.FALSE, sameTypeGroupings_4x5.checkGoalSatisfied(bksSemiComplete));
        assertEquals(Boolean.FALSE, sameTypeGroupings_2x3.checkGoalSatisfied(bksSemiComplete));
        assertEquals(Boolean.FALSE, sameTypeGroupings_1x10.checkGoalSatisfied(bksSemiComplete));

        assertEquals(Boolean.FALSE, sameTypeGroupings_4x5.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, sameTypeGroupings_2x3.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.TRUE, sameTypeGroupings_1x10.checkGoalSatisfied(bksCats));
    }

    @Test
    void testSlightlyDifferentLines() throws WrongArgumentException {
        assertEquals(Boolean.TRUE, slightlyDifferentLines_4row_1to3.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.FALSE, slightlyDifferentLines_2col_6to6.checkGoalSatisfied(bksComplete));
        assertEquals(Boolean.TRUE, slightlyDifferentLines_3row_2to3.checkGoalSatisfied(bksComplete));

        assertEquals(Boolean.FALSE, slightlyDifferentLines_4row_1to3.checkGoalSatisfied(bksOneTile));
        assertEquals(Boolean.FALSE, slightlyDifferentLines_2col_6to6.checkGoalSatisfied(bksOneTile));
        assertEquals(Boolean.FALSE, slightlyDifferentLines_3row_2to3.checkGoalSatisfied(bksOneTile));

        assertEquals(Boolean.FALSE, slightlyDifferentLines_4row_1to3.checkGoalSatisfied(bksSemiComplete));
        assertEquals(Boolean.FALSE, slightlyDifferentLines_2col_6to6.checkGoalSatisfied(bksSemiComplete));
        assertEquals(Boolean.FALSE, slightlyDifferentLines_3row_2to3.checkGoalSatisfied(bksSemiComplete));
    }
}