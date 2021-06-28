package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(PieceColour colour, Board board) {
        super();
        this.setBoard(board);
        board.pieces.add(this);
        this.colour = colour;
        if(this.colour == PieceColour.BLACK) {
            this.setSymbol("N");
        }
        else {
            this.setSymbol("N");
        }
    }

    @Override
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {
        // For a knight's move to be legal, the distance in one direction should be 1 and the distance in the other
        // should be 2
        return super.isLegitMove(i1, j1, i2, j2) && ((
                        Math.abs(i1 - i2) == 2 && Math.abs(j1 - j2) == 1) ||
                        (Math.abs(i1 - i2) == 1 && Math.abs(j1 - j2) == 2));
    }

    @Override
    public Knight deepcopy(Board b) {
        Piece temp2 = super.deepcopy(b);
        Knight temp = new Knight(this.colour, b);
        temp.colour = colour;
        temp.updateCoordinates(temp2.getRow(), temp2.getColumn());
        temp.setSymbol(temp2.getSymbol());
        temp.board = b;
        return temp;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> result = new ArrayList<>();
        for(int i = -2; i <= 2; i++)
            for(int j = -2; j <= 2; j++) {
                if(Math.abs(i) + Math.abs(j) == 3 && row + i >= 0 && row + i < 8 && column + j >= 0 && column + j < 8) {
                    if(this.isLegitMove(getRow(), getColumn(), row + i, column + j)) {
                        result.add(new Move(i, j, this));
                    }
                }
            }
        return result;
    }
}
