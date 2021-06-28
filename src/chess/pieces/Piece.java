package chess.pieces;

import chess.Board;
import chess.Move;
import chess.PieceColour;
import chess.Square;

import java.util.ArrayList;

public class Piece {
    protected int row;
    protected int column;
    private String symbol;
    protected PieceColour colour;
    protected Board board;

    public Piece() {
        row = 0;
        column = 0;
    }

    public void updateCoordinates(int i, int j) {
        this.row = i;
        this.column = j;

        board.enPassant = new int[] {0, 0};
    }

    // i1 j1 are origin, i2 j2 are destination
    public boolean isLegitMove(int i1, int j1, int i2, int j2) {
        if(i1 >= 0 && i1 < 8 && i2 >= 0 && i2 < 8 && j1 >= 0 && j1 < 8 && j2 >= 0 && j2 < 8) {
            Square[][] board = this.board.getBoard();

            // The origin and the destination must be different
            // The origin must match with the piece position
            if ((i1 == i2 && j1 == j2) || (i1 != row || j1 != column))
                return false;

            // If the destination contains a piece, it needs to be a different colour
            if (board[i2][j2].hasPiece()) {
                Piece target = board[i2][j2].getPiece();
                if(target.getColour() == this.colour)
                    return false;
            }

            // Only legal moves are ones that don't result in check
            Board temp = this.board.deepcopy();
            temp.forceMove(i1, j1, i2, j2, temp.getPiece(i1, j1));
            Piece king = temp.isCheck();
            if (king != null)
                return king.colour != this.colour;

            return true;
        }
        return false;
    }

    // Because both the bishop and the queen can move diagonally, I created a new method that check that this move is
    // legit to avoid having the same code in multiple  places and to keep the code more compact
    public boolean isLegitDiagonalMove(int i1, int j1, int i2, int j2) {
        // Created variables that store the displacement of the origin and the destination
        int di = Math.abs(i1 - i2);
        int dj = Math.abs(j1 - j2);
        // The displacements mush be equal for the move to be diagonal
        if(di == dj) {
            // Created variables that store the orientation of movement in the i and j directions (+1 if increasing and
            // -1 if decreasing)
            int stepI, stepJ;
            try {
                stepI = di / (i2 - i1);
                stepJ = dj / (j2 - j1);
            } catch (ArithmeticException e) {
                return false;
            }
            for (int d = 1; d < di; d++) {
                // If there is any piece on the path from the origin to the destination(open interval), the move is not
                // legal
                if(board.getBoard()[i1 + d * stepI][j1 + d * stepJ].hasPiece())
                    return false;
            }
            return true;
        }
        return false;
    }

    // As with the diagonal move, there are two pieces that can move straight, the queen and the rook
    public boolean isLegitStraightMove(int i1, int j1, int i2, int j2) {
        // For the move to be straight, either the i's or the j's need to be equal
        if(i1 == i2) {
            // Getting the direction of movement in the j direction (+1 if increasing and -1 if decreasing)
            int step;
            try {
                step = Math.abs(j2 - j1) / (j2 - j1);
            } catch(ArithmeticException e) {
                return false;
            }
            for(int j = j1 + step; j != j2; j += step) {
                // If there is any piece on the path from the origin to the destination(open interval), the move is not
                // legal
                if(board.getBoard()[i1][j].hasPiece())
                    return false;
            }
            return true;
        } else if(j1 == j2) {
            // Getting the direction of movement in the i direction (+1 if increasing and -1 if decreasing)
            int step;
            try {
                step = Math.abs(i2 - i1) / (i2 - i1);
            } catch(ArithmeticException e) {
                return false;
            }
            for(int i = i1 + step; i != i2; i += step) {
                // If there is any piece on the path from the origin to the destination(open interval), the move is not
                // legal
                if(board.getBoard()[i][j1].hasPiece())
                    return false;
            }
            return true;
        }
        return false;
    }

    public Piece deepcopy(Board b) {
        Piece temp = new Piece();
        temp.colour = colour;
        temp.column = column;
        temp.row = row;
        temp.symbol = symbol;
        temp.board = b;
        return temp;
    }

    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> list = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (this.isLegitMove(row, column, i, j)) {
                    list.add(new Move(i - row, j - column, this));
                }
        return list;
    }

    public boolean hasMoves() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (this.isLegitMove(row, column, i, j)) {
                    return true;
                }
        return false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public PieceColour getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return  "<piece=" + this.getClass().getName().substring(13) +
                " i=" + row +
                " j=" + column +
                " color=" + colour.toString() +
                ">";
    }
}
