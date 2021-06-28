package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(PieceColour colour, Board board) {
        super();
        this.setBoard(board);
        board.pieces.add(this);
        this.colour = colour;
        if(this.colour == PieceColour.BLACK) {
            this.setSymbol("Q");
        }
        else {
            this.setSymbol("Q");
        }
    }

    @Override
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {
        return super.isLegitMove(i1, j1, i2, j2) && (
                this.isLegitDiagonalMove(i1, j1, i2, j2) ||
                this.isLegitStraightMove(i1, j1, i2, j2));
    }

    @Override
    public Queen deepcopy(Board b) {
        Piece temp2 = super.deepcopy(b);
        Queen temp = new Queen(this.colour, b);
        temp.colour = colour;
        temp.updateCoordinates(temp2.getRow(), temp2.getColumn());
        temp.setSymbol(temp2.getSymbol());
        temp.board = b;
        return temp;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> result = new ArrayList<>();
        for(int di = 0; di <= 8; di++) {
            if(di != 0) {
                if(row + di < 8)
                    if (this.isLegitMove(getRow(), getColumn(), row + di, column)) {
                        result.add(new Move(di, 0, this));
                    }
                if(row - di >= 0)
                    if (this.isLegitMove(getRow(), getColumn(), row - di, column)) {
                        result.add(new Move(-di, 0, this));
                    }
                if(column + di < 8)
                    if (this.isLegitMove(getRow(), getColumn(), row, column + di)) {
                        result.add(new Move(0, di, this));
                    }
                if(column - di >= 0)
                    if (this.isLegitMove(getRow(), getColumn(), row, column - di)) {
                        result.add(new Move(0, -di, this));
                    }

                if(row + di < 8 && column + di < 8)
                    if(this.isLegitMove(getRow(), getColumn(), row + di, column + di)) {
                        result.add(new Move(di, di, this));
                    }
                if(row - di >= 0 && column - di >= 0)
                    if(this.isLegitMove(getRow(), getColumn(), row - di, column - di)) {
                        result.add(new Move(-di, -di, this));
                    }
                if(row + di < 8 && column - di >= 0)
                    if(this.isLegitMove(getRow(), getColumn(), row + di, column - di)) {
                        result.add(new Move(di, -di, this));
                    }
                if(row - di >= 0 && column + di < 8)
                    if(this.isLegitMove(getRow(), getColumn(), row - di, column + di)) {
                        result.add(new Move(-di, di, this));
                    }
            }
        }
        return result;
    }
}
