package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;

import java.util.ArrayList;

public class Pawn extends Piece {

    public boolean enPassant;

    public Pawn(PieceColour colour, Board board) {
        super();
        this.setBoard(board);
        board.pieces.add(this);
        this.colour = colour;
        this.enPassant = false;
        if(this.colour == PieceColour.BLACK) {
            this.setSymbol("P");
        }
        else {
            this.setSymbol("P");
        }
    }

    // This method checks whether a pawn can capture a piece at (i2, j2)
    public boolean canCapture(int i1, int j1, int i2, int j2) {
        // The displacement in the i and j directions have to be 1
        // This also accounts for the direction of forward movement, not allowing moving backwards to capture
        int step = (colour == PieceColour.WHITE ? 1 : -1);
        enPassant = board.enPassant[0] == i2 - step && board.enPassant[1] == j2 &&
                board.getPiece(i2 - step, j2) instanceof Pawn;
        return (board.getBoard()[i2][j2].hasPiece() && board.getPiece(i2, j2).colour != this.colour
                || enPassant && board.getPiece(i2 - step, j2).colour != this.colour) &&
                i2 - i1 == step &&
                Math.abs(j1 - j2) == 1;
    }

    @Override
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {
        // The conditions for the pawn to move legally are:
        //      -the displacement in the i direction should be 1, but it can be also 2 if the pawn is on its starting
        //      position (*1)
        //      -the difference between the origin and the destination is + for white pieces and - for black pieces
        //      (*2)
        //      -the piece either can capture, case in which the j destination may be different, or it moves straight,
        //      case in which it can not capture (*3)
        int di = Math.abs(i2 - i1);
        if(di == 0)
            return false;
        return super.isLegitMove(i1, j1, i2, j2) && (
                di == 1 || (di <= 2 && (i1 == 1 || i1 == 6))) && // (*1)
                di / (i2 - i1) == (this.colour == PieceColour.WHITE ? 1 : -1) && ( // (*2)
                (j1 == j2 && !board.getBoard()[i2][j2].hasPiece()) || this.canCapture(i1, j1, i2, j2)); // (*3)
    }

    @Override
    public Pawn deepcopy(Board b) {
        Piece temp2 = super.deepcopy(b);
        Pawn temp = new Pawn(this.colour, b);
        temp.colour = colour;
        temp.updateCoordinates(temp2.getRow(), temp2.getColumn());
        temp.setSymbol(temp2.getSymbol());
        temp.board = b;
        temp.enPassant = enPassant;
        return temp;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> result = new ArrayList<>();
        for(int di = -2; di <= 2; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di != 0 || dj != 0) {
                    if (row + di < 8 && row + di >= 0 && column + dj < 8 && column + dj >= 0) {
                        if (this.isLegitMove(row, column, row + di, column + dj)) {
                            result.add(new Move(di, dj, this));
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void updateCoordinates(int i, int j) {
        if(Math.abs(i - getRow()) == 2)
            board.enPassant = new int[] {i, j};
        else
            board.enPassant = new int[] {0, 0};
        this.row = i;
        this.column = j;
    }
}
