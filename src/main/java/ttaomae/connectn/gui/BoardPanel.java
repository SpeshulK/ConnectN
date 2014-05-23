package ttaomae.connectn.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ttaomae.connectn.Board;

/**
 * A panel for displaying a Connect-N board. The Board being displayed can be
 * changed and the spacing of the pieces will be adjusted to fit evenly in the
 * panel.
 * 
 * @author Todd Taomae
 */
public class BoardPanel extends GridPane implements Runnable
{
    private final int width;
    private final int height;
    private Board board;
    private int pieceRadius;

    private Thread myThread;

    /**
     * Constructs a new BoardPanel with the specified width, height, and
     * underlying Board.
     *
     * @param width the width of this panel
     * @param height the height of this panel
     * @param board the underlying Board for this panel
     */
    public BoardPanel(int width, int height, Board board)
    {
        this.setStyle("-fx-background-color: #336699;");
        this.width = width;
        this.height = height;
        this.board = board;
        this.setMaxWidth(this.width);
        this.setMaxHeight(this.height);
        this.setGapsAndPadding();

        this.myThread = new Thread(this, "Board Panel");
        this.myThread.setDaemon(true);
        myThread.start();
    }

    /**
     * Sets the gaps between spaces and padding on all edges of the board based
     * on the size of this panel and the size of the board.
     */
    private void setGapsAndPadding()
    {
        // the width and height measured in radii
        // 2*n radius for pieces + n+1 radius for gaps and padding
        int radiusWidths = 3 * this.board.getWidth() + 1;
        int radiusHeights = 3 * this.board.getHeight() + 1;

        this.pieceRadius = Math.min(this.width / radiusWidths, this.height / radiusHeights);

        // (size of the panel - total size of pieces) / (total number of gaps/padding)
        int totalPieceWidth = 2 * this.board.getWidth() * this.pieceRadius;
        int horizontalGap = (this.width - totalPieceWidth) / (this.board.getWidth() + 1);

        int totalPieceHeight = 2 * this.board.getHeight() * this.pieceRadius;
        int verticalGap = (this.height - totalPieceHeight) / (this.board.getHeight() + 1);

        int totalGapWidth = (this.board.getWidth() - 1) * horizontalGap;
        int horizontalPadding = (this.width - totalPieceWidth - totalGapWidth);
        int leftPadding = horizontalPadding / 2;
        // add padding % 2 to account for odd numbered padding
        int rightPadding = leftPadding + (horizontalPadding % 2);

        int totalGapHeight = (this.board.getHeight() - 1) * verticalGap;
        int verticalPadding = (this.height - totalPieceHeight - totalGapHeight);
        int bottomPadding = verticalPadding / 2;
        // add padding % 2 to account for odd numbered padding
        int topPadding = bottomPadding + (verticalPadding % 2);

        this.setHgap(horizontalGap);
        this.setVgap(verticalGap);
        this.setPadding(new Insets(topPadding, rightPadding, bottomPadding, leftPadding));
    }

    /**
     * Continually updates this BoardPanel each time a move is played.
     */
    @Override
    public void run()
    {
        while (true) {
            BoardPanel.this.update();

            try {
                // wait for next play
                synchronized (this.board) {
                    this.board.wait();
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    /**
     * Updates this panel to reflect the current state of the underlying Board.
     */
    public void update()
    {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                BoardPanel.this.getChildren().clear();
                for (int y = 0; y < BoardPanel.this.board.getHeight(); y++) {
                    for (int x = 0; x < BoardPanel.this.board.getWidth(); x++) {
                        Circle circle;
                        int row = BoardPanel.this.board.getHeight() - y - 1;
                        switch (BoardPanel.this.board.getPieceAt(x, row)) {
                            case BLACK:
                                circle = new Circle(BoardPanel.this.pieceRadius, Color.BLACK);
                                break;
                            case RED:
                                circle = new Circle(BoardPanel.this.pieceRadius, Color.RED);
                                break;
                            default:
                                circle = new Circle(BoardPanel.this.pieceRadius, Color.WHITE);
                        }
                        BoardPanel.this.add(circle, x, y);
                    }
                }
            }
        });
    }

    /**
     * Returns the column of the underlying Board corresponding to a horizontal
     * position on this panel.
     *
     * @param x horizontal position on this panel
     * @return the column of the underlying Board corresponding to the
     *         horizontal position
     *
     */
    public int getBoardColumn(double x)
    {
        double halfGap = this.getHgap() / 2;
        double leftPadding = this.getPadding().getLeft() - halfGap;
        double rightPadding = this.getPadding().getRight() - halfGap;

        // outside of board
        if (x < (leftPadding) || x > (this.getWidth() - rightPadding)) {
            return -1;
        }

        double boardWidth = this.getWidth() - leftPadding - rightPadding;
        double pieceWidth = boardWidth / this.board.getWidth();

        double boardX = x - leftPadding;
        int column = (int) (boardX / pieceWidth);

        return column;
    }

    /**
     * Sets the underlying Board to a new Board.
     *
     * @param board the new Board
     */
    public void setBoard(Board board)
    {
        // thread is waiting for the current board to play a move
        // however, since we are changing the board, that won't happen, so we interrupt
        synchronized (this.board) {
            this.myThread.interrupt();
            this.board = board;
        }
        this.setGapsAndPadding();
        this.update();
    }

}
