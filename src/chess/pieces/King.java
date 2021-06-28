package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;

import java.util.ArrayList;

public class King extends Piece {

    public boolean canCastle;

    public King(PieceColour colour, Board board) {
        super();
        this.setBoard(board);
        board.pieces.add(this);
        this.colour = colour;
        canCastle = true;
        if(this.colour == PieceColour.BLACK) {
            this.setSymbol("K");
        }
        else {
            this.setSymbol("K");
        }

        this.board.kings[(colour == PieceColour.WHITE ? 0 : 1)] = this;
    }

    @Override
    public void updateCoordinates(int i, int j) {
        super.updateCoordinates(i, j);
        canCastle = false;
    }

    @Override
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {

        int rookPos = (j1 - j2) > 0 ? 0 : 7;
        boolean castle = false;
        if(board.hasPiece(i1, rookPos) && canCastle) {
            try {
                Rook r = (Rook) board.getPiece(i1, rookPos);
                castle = board.isCheck() == null && this.canCastle && r.canCastle && i1 == i2 && Math.abs(j1 - j2) == 2 &&
                        r.isLegitStraightMove(i1, rookPos, i1, j1 + (rookPos == 0 ? -1 : 1));
           } catch(ClassCastException ignored) {}
        }

        return super.isLegitMove(i1, j1, i2, j2) &&
                (Math.abs(i1 - i2) <= 1 && Math.abs(j1 - j2) <= 1 || castle);
    }

    @Override
    public King deepcopy(Board b) {
        Piece temp2 = super.deepcopy(b);
        King temp = new King(this.colour, b);
        temp.colour = colour;
        temp.updateCoordinates(temp2.getRow(), temp2.getColumn());
        temp.setSymbol(temp2.getSymbol());
        temp.board = b;
        temp.canCastle = canCastle;
        return temp;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> result = new ArrayList<>();
        int k = 0;
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++) {
                if(i != 0 || j != 0) {
                    if(row + i < 8 && row + i >= 0 && column + j < 8 && column + j >= 0)
                    if(this.isLegitMove(getRow(), getColumn(), row + i, column + j)) {
                        result.add(new Move(i, j, this));
                    }
                }
            }
        return result;
    }
}
