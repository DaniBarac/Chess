package chess;

import chess.pieces.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LightBoardTest {
    Board board;
    LightBoard lightBoard;

    @Before
    public void setup() {
        board = new Board(Board.BoardState.EMPTY);
        lightBoard = new LightBoard(board);
    }

    @Test
    public void testGetAllLegalMoves() {
        board.setPiece(0, 0, new Queen(PieceColour.BLACK, board));
        board.setPiece(1, 4, new Queen(PieceColour.BLACK, board));
        board.setPiece(4, 4, new Bishop(PieceColour.WHITE, board));
        board.setPiece(3, 4, new Rook(PieceColour.BLACK, board));
        board.setPiece(1, 5, new Knight(PieceColour.WHITE, board));
        board.setPiece(1, 1, new Pawn(PieceColour.WHITE, board));
        board.setPiece(6, 1, new Pawn(PieceColour.BLACK, board));
        board.setPiece(6, 2, new Pawn(PieceColour.WHITE, board));
        ArrayList<Move> boardMoves = board.getLegalMoves();
        ArrayList<int[]> lBoardMoves = lightBoard.getAllLegalMoves();
        assertEquals(boardMoves.size(), lBoardMoves.size());
    }

}
