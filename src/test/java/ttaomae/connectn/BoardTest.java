package ttaomae.connectn;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BoardTest
{
    private Board board;

    @Before
    public void init()
    {
        this.board = new Board();
    }

    @Test
    public void testNewBoard()
    {
        assertEquals("failure - new board height", 6, board.getHeight());
        assertEquals("failure - new board width", 7, board.getWidth());
        assertEquals("failure - new board win condition", 4, board.getWinCondition());

        assertEquals("failure - first piece is black", Piece.BLACK, board.getNextPiece());

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                assertEquals("failure - new board is empty", Piece.NONE, board.getPieceAt(col, row));
            }
        }

        assertEquals("failure - no winner for new board", Piece.NONE, board.getWinner());
    }

    @Test
    public void testOnePlay()
    {
        for (int col = 0; col < board.getWidth(); col++) {
            board = new Board();
            board.play(col);
            assertEquals("failure - first play is black", Piece.BLACK, board.getPieceAt(col, 0));
            assertEquals("failure - second piece is red", Piece.RED, board.getNextPiece());
        }
    }

    @Test(expected = IllegalMoveException.class)
    public void testPlaySingleColumn()
    {
        for (int row = 0; row < this.board.getHeight(); row++) {
            Piece lastPiece = this.board.getNextPiece();
            board.play(0);
            assertEquals("failure - last piece is opposite of next piece", lastPiece, board
                    .getNextPiece().opposite());
            assertEquals("failure - play on a single column", lastPiece, board.getPieceAt(0, row));
        }

        // illegal move
        board.play(0);
    }

    @Test
    public void testIllegalPlay()
    {
        try {
            board.play(-1);
        } catch (IllegalMoveException e) {
            assertEquals("failure - illegal move: -1", "Illegal column: -1", e.getMessage());
        }

        try {
            board.play(board.getWidth());
        } catch (IllegalMoveException e) {
            assertEquals("failure - illegal move: 7", "Illegal column: 7", e.getMessage());
        }
    }

    @Test
    public void testIllegalPieceAt()
    {
        try {
            board.getPieceAt(-1, 0);
        } catch (IndexOutOfBoundsException e) {
            assertEquals("failure - get piece at (-1, 0)", "Column: -1, Width: 7", e.getMessage());
        }

        try {
            board.getPieceAt(board.getWidth(), 0);
        } catch (IndexOutOfBoundsException e) {
            assertEquals("failure - get piece at (7, 0)", "Column: 7, Width: 7", e.getMessage());
        }

        try {
            board.getPieceAt(0, -1);
        } catch (IndexOutOfBoundsException e) {
            assertEquals("failure - get piece at (0, -1)", "Row: -1, Height: 6", e.getMessage());
        }

        try {
            board.getPieceAt(0, board.getHeight());
        } catch (IndexOutOfBoundsException e) {
            assertEquals("failure - get piece at (0, 6)", "Row: 6, Height: 6", e.getMessage());
        }
    }

    @Test
    public void testBlackHorizinalWin()
    {
        board.play(0); // black
        board.play(0);
        board.play(1); // black
        board.play(1);
        board.play(2); // black
        board.play(2);
        board.play(3); // black
        assertEquals("failure - black horizontal winner, bottom left", Piece.BLACK,
                board.getWinner());

        board = new Board();
        board.play(3); // black
        board.play(3);
        board.play(4); // black
        board.play(4);
        board.play(5); // black
        board.play(5);
        board.play(6); // black
        assertEquals("failure - black horizontal winner, bottom right", Piece.BLACK,
                board.getWinner());

        board = new Board();
        // fill left side of board without a winner
        for (int i = 0; i < 3; i++) {
            board.play(0);
            board.play(1);
            board.play(2);
            board.play(3);
        }
        for (int i = 0; i < 2; i++) {
            board.play(1);
            board.play(0);
            board.play(3);
            board.play(2);
        }
        // black plays 0, 1, 2, 3
        board.play(0); // black
        board.play(6);
        board.play(1); // black
        board.play(6);
        board.play(2); // black
        board.play(5);
        board.play(3); // black
        assertEquals("failure - black horizontal winner, top left", Piece.BLACK, board.getWinner());


        board = new Board();
        // fill right side of board without a winner
        for (int i = 0; i < 3; i++) {
            board.play(3);
            board.play(4);
            board.play(5);
            board.play(6);
        }
        for (int i = 0; i < 2; i++) {
            board.play(4);
            board.play(3);
            board.play(6);
            board.play(5);
        }
        // black plays 3, 4, 5, 6
        board.play(3); // black
        board.play(0);
        board.play(4); // black
        board.play(0);
        board.play(5); // black
        board.play(1);
        board.play(6); // black
        assertEquals("failure - black horizontal winner, top right", Piece.BLACK, board.getWinner());
    }

    @Test
    public void testBlackVerticalWin()
    {
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        assertEquals("failure - black vertical winner, bottom left", Piece.BLACK, board.getWinner());

        board = new Board();
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        assertEquals("failure - black vertical winner, bottom right", Piece.BLACK,
                board.getWinner());

        board = new Board();
        // fill left side of board without a winner
        for (int i = 0; i < 2; i++) {
            board.play(1);
            board.play(0);
        }
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        board.play(1);
        board.play(0); // black
        assertEquals("failure - black vertical winner, top left", Piece.BLACK, board.getWinner());

        board = new Board();
        // fill right side of board without a winner
        for (int i = 0; i < 2; i++) {
            board.play(5);
            board.play(6);
        }
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        board.play(0);
        board.play(6); // black
        assertEquals("failure - black vertical winner, top left", Piece.BLACK,
                board.getWinner());
    }

    @Test
    public void testBlackDiagonalWin()
    {
        board.play(0); // black
        board.play(1);
        board.play(1); // black
        board.play(2);
        board.play(2);
        board.play(3);
        board.play(2); // black
        board.play(3);
        board.play(4);
        board.play(3);
        board.play(3); // black
        assertEquals("failure - black diagonal winner, bottom left", Piece.BLACK, board.getWinner());

        board = new Board();
        board.play(6); // black
        board.play(5);
        board.play(5); // black
        board.play(4);
        board.play(4);
        board.play(3);
        board.play(4); // black
        board.play(3);
        board.play(2);
        board.play(3);
        board.play(3); // black
        assertEquals("failure - black diagonal winner, bottom right", Piece.BLACK,
                board.getWinner());

        board = new Board();
        for (int i = 0; i < 5; i++) {
            board.play(0);
        }
        board.play(1);
        board.play(0); // black
        for (int i = 0; i < 3; i++) {
            board.play(1);
        }
        board.play(1); // black
        for (int i = 0; i < 3; i++) {
            board.play(2);
        }
        board.play(2); // black
        board.play(3);
        board.play(4);
        board.play(3);
        board.play(3); // black
        assertEquals("failure - black diagonal winner, top left", Piece.BLACK, board.getWinner());


        board = new Board();
        for (int i = 0; i < 5; i++) {
            board.play(6);
        }
        board.play(5);
        board.play(6); // black
        for (int i = 0; i < 3; i++) {
            board.play(5);
        }
        board.play(5); // black
        for (int i = 0; i < 3; i++) {
            board.play(4);
        }
        board.play(4); // black
        board.play(3);
        board.play(2);
        board.play(3);
        board.play(3); // black
        assertEquals("failure - black diagonal winner, top right", Piece.BLACK, board.getWinner());
    }

    @Test
    public void testRedkHorizinalWin()
    {
        board.play(6);
        board.play(0); // red
        board.play(5);
        board.play(1); // red
        board.play(6);
        board.play(2); // red
        board.play(5);
        board.play(3); // red
        assertEquals("failure - red horizontal winner, bottom left", Piece.RED, board.getWinner());

        board = new Board();
        board.play(0);
        board.play(3); // red
        board.play(1);
        board.play(4); // red
        board.play(0);
        board.play(5); // red
        board.play(1);
        board.play(6); // red

        assertEquals("failure - red horizontal winner, bottom right", Piece.RED,
                board.getWinner());

        board = new Board();
        // fill left side of board without a winner
        for (int i = 0; i < 3; i++) {
            board.play(0);
            board.play(1);
            board.play(2);
            board.play(3);
        }
        for (int i = 0; i < 2; i++) {
            board.play(1);
            board.play(0);
            board.play(3);
            board.play(2);
        }
        // red plays 0, 1, 2, 3
        board.play(6);
        board.play(0); // red
        board.play(6);
        board.play(1); // red
        board.play(5);
        board.play(2); // red
        board.play(5);
        board.play(3); // red
        assertEquals("failure - red horizontal winner, top left", Piece.RED, board.getWinner());

        board = new Board();
        // fill right side of board without a winner
        for (int i = 0; i < 3; i++) {
            board.play(3);
            board.play(4);
            board.play(5);
            board.play(6);
        }
        for (int i = 0; i < 2; i++) {
            board.play(4);
            board.play(3);
            board.play(6);
            board.play(5);
        }
        // red plays 3, 4, 5, 6
        board.play(0);
        board.play(3); // red
        board.play(0);
        board.play(4); // red
        board.play(1);
        board.play(5); // red
        board.play(1);
        board.play(6); // red
        assertEquals("failure - red horizontal winner, top right", Piece.RED, board.getWinner());
    }

    @Test
    public void testRedVerticalWin()
    {
        board.play(1);
        board.play(0); // red
        board.play(1);
        board.play(0); // red
        board.play(1);
        board.play(0); // red
        board.play(2);
        board.play(0); // red
        assertEquals("failure - red vertical winner, bottom left", Piece.RED, board.getWinner());

        board = new Board();
        board.play(0);
        board.play(6); // red
        board.play(0);
        board.play(6); // red
        board.play(0);
        board.play(6); // red
        board.play(1);
        board.play(6); // red
        assertEquals("failure - red vertical winner, bottom right", Piece.RED, board.getWinner());

        board = new Board();
        // fill left side of board without a winner
        for (int i = 0; i < 2; i++) {
            board.play(0);
            board.play(1);
        }
        board.play(1);
        board.play(0); // red
        board.play(1);
        board.play(0); // red
        board.play(1);
        board.play(0); // red
        board.play(2);
        board.play(0); // red
        assertEquals("failure - red vertical winner, top left", Piece.RED, board.getWinner());

        board = new Board();
        // fill right side of board without a winner
        for (int i = 0; i < 2; i++) {
            board.play(6);
            board.play(5);
        }
        board.play(0);
        board.play(6); // red
        board.play(0);
        board.play(6); // red
        board.play(0);
        board.play(6); // red
        board.play(1);
        board.play(6); // red
        assertEquals("failure - red vertical winner, top left", Piece.RED, board.getWinner());
    }

    @Test
    public void testRedDiagonalWin()
    {
        board.play(1);
        board.play(0); // red
        board.play(2);
        board.play(1); // red
        board.play(2);
        board.play(2); // red
        board.play(3);
        board.play(3);
        board.play(3);
        board.play(3); // red
        assertEquals("failure - red diagonal winner, bottom left", Piece.RED, board.getWinner());

        board = new Board();
        board.play(5);
        board.play(6); // red
        board.play(4);
        board.play(5); // red
        board.play(4);
        board.play(4); // red
        board.play(3);
        board.play(3);
        board.play(3);
        board.play(3); // red
        assertEquals("failure - red diagonal winner, bottom right", Piece.RED, board.getWinner());

        board = new Board();
        for (int i = 0; i < 5; i++) {
            board.play(0);
        }
        board.play(0); // red
        for (int i = 0; i < 4; i++) {
            board.play(1);
        }
        board.play(2);
        board.play(1); // red
        board.play(2);
        board.play(3);
        board.play(2);
        board.play(2); // red
        board.play(3);
        board.play(3); // red
        assertEquals("failure - red diagonal winner, top right", Piece.RED, board.getWinner());

        board = new Board();
        for (int i = 0; i < 5; i++) {
            board.play(6);
        }
        board.play(6); // red
        for (int i = 0; i < 4; i++) {
            board.play(5);
        }
        board.play(4);
        board.play(5); // red
        board.play(4);
        board.play(3);
        board.play(4);
        board.play(4); // red
        board.play(3);
        board.play(3); // red
        assertEquals("failure - red diagonal winner, top right", Piece.RED, board.getWinner());
    }
}
