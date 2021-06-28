package chess;

import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Queen;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testGetBoardAsMatrix () {
        Board board = new Board(Board.BoardState.EMPTY);
        board.setPiece(3, 4, new Queen(PieceColour.WHITE, board));
        board.setPiece(2, 7, new Knight(PieceColour.BLACK, board));
        board.setPiece(0, 0, new King(PieceColour.WHITE, board));
        char[][] matrix = board.getBoardAsMatrix();
        assertEquals(matrix[3][4], 'q');
        assertEquals(matrix[2][7], 'N');
        assertEquals(matrix[0][0], 'k');
    }

}
