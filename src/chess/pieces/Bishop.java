package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(PieceColour colour, Board board) {
        super();
        this.setBoard(board);
        board.pieces.add(this);
        this.colour = colour;
        if(this.colour == PieceColour.BLACK) {
            this.setSymbol("B");
        }
        else {
            this.setSymbol("b");
        }
    }

    @Override
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {
        return super.isLegitMove(i1, j1, i2, j2) &&
                this.isLegitDiagonalMove(i1, j1, i2, j2);
    }

    @Override
    public Bishop deepcopy(Board b) {
        Piece temp2 = super.deepcopy(b);
        Bishop temp = new Bishop(this.colour, b);
        temp.colour = colour;
        temp.updateCoordinates(temp2.getRow(), temp2.getColumn());
        temp.setSymbol(temp2.getSymbol());
        temp.board = b;
        return temp;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> result = new ArrayList<>();
        for(int i = 0; i <= 8; i++) {
            if(i != 0) {
                if(row + i < 8 && column + i < 8)
                if(this.isLegitMove(getRow(), getColumn(), row + i, column + i)) {
                    result.add(new Move(i, i, this));
                }
                if(row - i >= 0 && column - i >= 0)
                if(this.isLegitMove(getRow(), getColumn(), row - i, column - i)) {
                    result.add(new Move(-i, -i, this));
                }
                if(row + i < 8 && column - i >= 0)
                if(this.isLegitMove(getRow(), getColumn(), row + i, column - i)) {
                    result.add(new Move(i, -i, this));
                }
                if(row - i >= 0 && column + i < 8)
                if(this.isLegitMove(getRow(), getColumn(), row - i, column + i)) {
                    result.add(new Move(-i, +i, this));
                }
            }
        }
        return result;
    }
}
