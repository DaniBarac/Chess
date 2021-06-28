package chess;

import chess.pieces.Piece;

public class Move {
    public static final Move NULL_MOVE = new Move(0, 0, null);

    // direction[0] - row
    // direction[1] - column
    int[] direction;
    private final Piece piece;

    public Move(int di, int dj, Piece p) {
        direction = new int[2];
        direction[0] = di;
        direction[1] = dj;
        this.piece = p;
    }

    public int[] toIntArray() {
        return new int[] {
                piece.getRow(),
                piece.getColumn(),
                piece.getRow() + direction[0],
                piece.getColumn() + direction[1]
        };
    }

    @Override
    public String toString() {
        return  this.equals(NULL_MOVE) ? "<NULL_MOVE>" :
                "<piece=" + piece.getClass().getName().substring(13) +
                " colour=" + piece.getColour().toString() +
                " i=" + piece.getRow() +
                " j=" + piece.getColumn() +
                " di=" + direction[0] +
                " dj=" + direction[1] +
                ">";
    }

    public Piece getPiece() {
        return piece;
    }
}
