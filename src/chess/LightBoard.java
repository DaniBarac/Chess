package chess;

import java.util.ArrayList;

/**
 * Class meant as a more efficient alternative to {@link Board}. 
 * Implemented as an 8x8 char[][] matrix that stores chars representing pieces, instead of having piece
 * objects like {@link Board} does. This implementation aims to reduce the memory load and the time complexity
 * of {@link Board} by stripping down all the interconnected instances of various classes and replacing them
 * with simple methods, although readability took a hit.
 * <p>
 *     Lowercase characters represent white pieces, while uppercase characters
 *     represent black pieces. the values are as follows:
 *     <ul>
 * 	   	   <li>' ' - empty square</li>
 * 	  	   <li>'p' & 'P' - pawn</li>
 * 	  	   <li>'n' & 'N' - knight</li>
 * 	  	   <li>'b' & 'B' - bishop</li>
 * 	  	   <li>'r' & 'R' - rook</li>
 * 	  	   <li>'q' & 'Q' - queen</li>
 * 	       <li>'k' & 'K' - king</li>
 * 	   </ul>
 * </p>
 *
 * @author Daniel Barac
 */
public class LightBoard {
    private char[][] matrix;
    boolean enPassant;
    boolean whiteCanCastle;
    boolean blackCanCastle;
    boolean whiteRightRookMoved;
    boolean whiteLeftRookMoved;
    boolean blackRightRookMoved;
    boolean blackLeftRookMoved;

    PieceColour turn;

    public LightBoard(Board board) {
        matrix = board.getBoardAsMatrix();
        enPassant = false;
        whiteCanCastle = true;
        blackCanCastle = true;
        blackRightRookMoved = false;
        blackLeftRookMoved = false;
        whiteRightRookMoved = false;
        whiteLeftRookMoved = false;
        turn = PieceColour.WHITE;
    }

    public LightBoard() {
        this(new Board());
    }

    private LightBoard(char[][] matrix) {
        this();
        this.matrix = matrix;
    }

    /**
     * Returns all moves allowed by the rules of the game of chess.
     * <p>
     *     A move is represented as an array of int with 4 elements, with source(int[0], int[1]) and dest(int[2], int[3]).
     * </p>
     * @return  an ArrayList of int[4].
     * @author Daniel Barac
     */
    public ArrayList<int[]> getAllLegalMoves() {
        ArrayList<int[]> result = new ArrayList<>();
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(turn.equals(PieceColour.WHITE)) {
                    switch (matrix[i][j]) {
                        case 'p' -> result.addAll(getAllPawnMoves(i, j, PieceColour.WHITE));
                        case 'b' -> result.addAll(getAllBishopMoves(i, j, PieceColour.WHITE));
                        case 'n' -> result.addAll(getAllKnightMoves(i, j, PieceColour.WHITE));
                        case 'r' -> result.addAll(getAllRookMoves(i, j, PieceColour.WHITE));
                        case 'q' -> {
                            result.addAll(getAllBishopMoves(i, j, PieceColour.WHITE));
                            result.addAll(getAllRookMoves(i, j, PieceColour.WHITE));
                        }
                        case 'k' -> result.addAll(getAllKingMoves(i, j, PieceColour.WHITE));
                    }
                }
                else {
                    switch (matrix[i][j]) {
                        case 'P' -> result.addAll(getAllPawnMoves(i, j, PieceColour.BLACK));
                        case 'B' -> result.addAll(getAllBishopMoves(i, j, PieceColour.BLACK));
                        case 'N' -> result.addAll(getAllKnightMoves(i, j, PieceColour.BLACK));
                        case 'R' -> result.addAll(getAllRookMoves(i, j, PieceColour.BLACK));
                        case 'Q' -> {
                            result.addAll(getAllBishopMoves(i, j, PieceColour.BLACK));
                            result.addAll(getAllRookMoves(i, j, PieceColour.BLACK));
                        }
                        case 'K' -> result.addAll(getAllKingMoves(i, j, PieceColour.BLACK));
                    }
                }
        return result;
    }

    /**
     * Executes the specified move. This method <mark>DOES NOT</mark> check if the move is correct
     * for the sake of efficiency. It is meant to be used after already knowing if the move is good or not.
     * @param i0 piece row
     * @param j0 piece column
     * @param i1 destination row
     * @param j1 destination column
     * @author Daniel Barac
     */
    public void makeMove(int i0, int j0, int i1, int j1) {
        enPassant = false;
        char c = matrix[i0][j0];

        if(c == 'k' && whiteCanCastle && !whiteRightRookMoved && i1 == i0 && j1 == 6) {
            matrix[0][4] = ' ';
            matrix[0][7] = ' ';
            matrix[0][5] = 'r';
            matrix[0][6] = 'k';
            whiteCanCastle = false;
            return;
        }
        if(c == 'k' && whiteCanCastle && !whiteLeftRookMoved && i1 == i0 && j1 == 2) {
            matrix[0][4] = ' ';
            matrix[0][0] = ' ';
            matrix[0][3] = 'r';
            matrix[0][2] = 'k';
            whiteCanCastle = false;
            return;
        }
        if(c == 'K' && blackCanCastle && !blackRightRookMoved && i1 == i0 && j1 == 6) {
            matrix[7][4] = ' ';
            matrix[7][7] = ' ';
            matrix[7][5] = 'R';
            matrix[7][6] = 'K';
            blackCanCastle = false;
            return;
        }
        if(c == 'K' && blackCanCastle && !blackLeftRookMoved && i1 == i0 && j1 == 2) {
            matrix[7][4] = ' ';
            matrix[7][0] = ' ';
            matrix[7][3] = 'R';
            matrix[7][2] = 'K';
            blackCanCastle = false;
            return;
        }

        if(c == 'r' && i0 == 0 && j0 == 0)
            whiteLeftRookMoved = true;
        if(c == 'r' && i0 == 0 && j0 == 7)
            whiteRightRookMoved = true;
        if(c == 'R' && i0 == 7 && j0 == 0)
            blackLeftRookMoved = true;
        if(c == 'R' && i0 == 7 && j0 == 7)
            blackRightRookMoved = true;

        if(c == 'p') {
            if(i1 - i0 == 2) {
                enPassant = true;
                matrix[i0][j0] = ' ';
                matrix[i1][j1] = 'p';
                return;
            }
            if(i1 - i0 == 1 && Math.abs(j1 - j0) == 1) {
                matrix[i0][j0] = ' ';
                // en passant
                if(matrix[i1][j1] == ' ')
                    matrix[i1 - 1][j1] = ' ';
                matrix[i1][j1] = 'p';
                return;
            }
        }

        if(c == 'P') {
            if(i0 - i1 == 2) {
                enPassant = true;
                matrix[i0][j0] = ' ';
                matrix[i1][j1] = 'P';
                return;
            }
            if(i0 - i1 == 1 && Math.abs(j1 - j0) == 1) {
                matrix[i0][j0] = ' ';
                // en passant
                if(matrix[i1][j1] == ' ')
                    matrix[i1 + 1][j1] = ' ';
                matrix[i1][j1] = 'P';
                return;
            }
        }

        matrix[i0][j0] = ' ';
        matrix[i1][j1] = c;
    }

    public LightBoard copy() {
        char[][] result = new char[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        return new LightBoard(result);
    }

    public char getPiece(int i, int j) {
        return matrix[i][j];
    }

    public void printBoard() {
        System.out.print("\n   a b c d e f g h \n");
        System.out.print("  -----------------\n");

        for (int i = 7; i >= 0; i--){
            int row = i + 1;
            System.out.print(row + " ");
            for (int j = 0; j < 8; j++){
                System.out.print("|" + matrix[i][j]);
            }
            System.out.print("| "+row+"\n");
        }
        System.out.print("  -----------------");
        System.out.print("\n   a b c d e f g h \n");
    }

    public String getAllMovesReadable() {
        ArrayList<int[]> moves = getAllLegalMoves();
        StringBuilder out = new StringBuilder();
        for(int[] move : moves) {
            out.append("<piece=");
            switch (matrix[move[0]][move[1]]) {
                case 'p' -> out.append("Pawn color=WHITE");
                case 'b' -> out.append("Bishop color=WHITE");
                case 'n' -> out.append("Knight color=WHITE");
                case 'r' -> out.append("Rook color=WHITE");
                case 'q' -> out.append("Queen color=WHITE");
                case 'k' -> out.append("King color=WHITE");
                case 'P' -> out.append("Pawn color=BLACK");
                case 'B' -> out.append("Bishop color=BLACK");
                case 'N' -> out.append("Knight color=BLACK");
                case 'R' -> out.append("Rook color=BLACK");
                case 'Q' -> out.append("Queen color=BLACK");
                case 'K' -> out.append("King color=BLACK");
            }
            out.append(" i=").append(move[0]).append(" j=").append(move[1]);
            out.append(" di=").append(move[2] - move[0]);
            out.append(" dj=").append(move[3] - move[1]);
            out.append(">\n");
        }

        return out.toString();
    }

    private boolean isLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    private char toLower(char c) {
        if(isUpper(c))
            return (char) (c - 32);
        return c;
    }

    private boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private ArrayList<int[]> getAllBishopMoves(int i, int j, PieceColour colour) {
        // North-east, south-east etc
        boolean ne = true, se = true, nw = true, sw = true;
        ArrayList<int[]> result = new ArrayList<>();
        for(int k = 0; k < 8; k++) {
            if(i + k < 8 && j + k < 8 && ne) {
                char c = matrix[i + k][j + k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i + k, j + k});
                    ne = (c == ' ');
                }
                else
                    ne = false;
            }
            if(i - k >= 0 && j + k < 8 && se) {
                char c = matrix[i - k][j + k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i - k, j + k});
                    se = (c == ' ');
                }
                else
                    se = false;
            }
            if(i + k < 8 && j - k >= 0 && nw) {
                char c = matrix[i + k][j - k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i + k, j - k});
                    nw = (c == ' ');
                }
                else
                    nw = false;
            }
            if(i - k >= 0 && j - k >= 0 && sw) {
                char c = matrix[i - k][j - k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i - k, j - k});
                    sw = (c == ' ');
                }
                else
                    sw = false;
            }
        }

        return result;
    }

    private ArrayList<int[]> getAllRookMoves(int i, int j, PieceColour colour) {
        // North, east, south, west
        boolean n = true, e = true, s = true, w = true;
        ArrayList<int[]> result = new ArrayList<>();
        for(int k = 0; k < 8; k++) {
            if(i + k < 8 && n) {
                char c = matrix[i + k][j];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i + k, j});
                    n = (c == ' ');
                }
                else
                    n = false;
            }
            if(i - k >= 0 && s) {
                char c = matrix[i - k][j];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i - k, j});
                    s = (c == ' ');
                }
                else
                    s = false;
            }
            if(j - k >= 0 && w) {
                char c = matrix[i][j - k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i, j - k});
                    w = (c == ' ');
                }
                else
                    w = false;
            }
            if(j + k < 8 && e) {
                char c = matrix[i][j + k];
                if(isLandableSquare(colour, c)) {
                    result.add(new int[] {i, j, i, j + k});
                    e = (c == ' ');
                }
                else
                    e = false;
            }
        }

        return result;
    }

    private ArrayList<int[]> getAllKnightMoves(int i, int j, PieceColour colour) {
        ArrayList<int[]> result = new ArrayList<>();

        if(i + 2 < 8 && j + 1 < 8) {
            char c = matrix[i + 2][j + 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 2, j + 1});
            }
        }
        if(i + 2 < 8 && j - 1 >= 0) {
            char c = matrix[i + 2][j - 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 2, j - 1});
            }
        }
        if(i - 2 >= 0 && j + 1 < 8) {
            char c = matrix[i - 2][j + 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 2, j + 1});
            }
        }
        if(i - 2 >= 0 && j - 1 >= 0) {
            char c = matrix[i - 2][j - 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 2, j - 1});
            }
        }
        if(i + 1 < 8 && j + 2 < 8) {
            char c = matrix[i + 1][j + 2];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 1, j + 2});
            }
        }
        if(i + 1 < 8 && j - 2 >= 0) {
            char c = matrix[i + 1][j - 2];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 1, j - 2});
            }
        }
        if(i - 1 >= 0 && j + 2 < 8) {
            char c = matrix[i - 1][j + 2];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 1, j + 2});
            }
        }
        if(i - 1 >= 0 && j - 2 >= 0) {
            char c = matrix[i - 1][j - 2];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 1, j - 2});
            }
        }

        return result;
    }

    private ArrayList<int[]> getAllPawnMoves(int i, int j, PieceColour colour) {
        ArrayList<int[]> result = new ArrayList<>();
        int step = colour.equals(PieceColour.WHITE) ? 1 : -1;
        if(i + step < 8 && i + step >= 0) {
            char c = matrix[i + step][j];
            if(isLandableSquare(colour, c))
                result.add(new int[] {i, j, i + step, j});
            if(j + 1 < 8) {
                c = matrix[i + step][j + 1];
                if(isDiffColor(colour, c) ||
                        isDiffColor(colour, matrix[i][j + 1]) && toLower(matrix[i][j + 1]) == 'p' && enPassant) {
                    result.add(new int[] {i, j, i + step, j + 1});
                }
            }
            if(j - 1 >= 0) {
                c = matrix[i + step][j - 1];
                if(isDiffColor(colour, c) ||
                        isDiffColor(colour, matrix[i][j - 1]) && toLower(matrix[i][j - 1]) == 'p' && enPassant) {
                    result.add(new int[] {i, j, i + step, j - 1});
                }
            }
            if(i == 1 && colour.equals(PieceColour.WHITE) || i == 6 && colour.equals(PieceColour.BLACK)) {
                c = matrix[i + 2 * step][j];
                if(isLandableSquare(colour, c) && matrix[i + step][j] == ' ')
                    result.add(new int[] {i, j, i + 2 * step, j});
            }
        }
        return result;
    }

    private ArrayList<int[]> getAllKingMoves(int i, int j, PieceColour colour) {
        ArrayList<int[]> result = new ArrayList<>();
        if(i + 1 < 8) {
            char c = matrix[i + 1][j];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 1, j});
            }
        }
        if(i - 1 >= 0) {
            char c = matrix[i - 1][j];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 1, j});
            }
        }
        if(j + 1 < 8) {
            char c = matrix[i][j + 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i, j + 1});
            }
        }
        if(j - 1 >= 0) {
            char c = matrix[i][j - 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i, j - 1});
            }
        }
        if(i + 1 < 8 && j + 1 < 8) {
            char c = matrix[i + 1][j + 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 1, j + 1});
            }
        }
        if(i + 1 < 8 && j - 1 >= 0) {
            char c = matrix[i + 1][j - 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i + 1, j - 1});
            }
        }
        if(i - 1 >= 0 && j + 1 < 8) {
            char c = matrix[i - 1][j + 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 1, j + 1});
            }
        }
        if(i - 1 >= 0 && j - 1 >= 0) {
            char c = matrix[i - 1][j - 1];
            if (isLandableSquare(colour, c)) {
                result.add(new int[]{i, j, i - 1, j - 1});
            }
        }

        if(whiteCanCastle && matrix[0][4] == 'k' && i == 0 && j == 4)
            if(matrix[0][7] == 'r' && matrix[0][6] == ' ' && matrix[0][5] == ' ')
                result.add(new int[] {i, j, i, j + 2});
        if(matrix[0][0] == 'r' && matrix[0][1] == ' ' && matrix[0][2] == ' ' && matrix[0][3] == ' ')
            result.add(new int[] {i, j, i, j - 2});

        if(blackCanCastle && matrix[7][4] == 'K' && i == 7 && j == 4)
            if(matrix[7][7] == 'R' && matrix[7][6] == ' ' && matrix[7][5] == ' ')
                result.add(new int[] {i, j, i, j + 2});
        if(matrix[7][0] == 'R' && matrix[7][1] == ' ' && matrix[7][2] == ' ' && matrix[7][3] == ' ')
            result.add(new int[] {i, j, i, j - 2});

        return result;
    }

    private boolean isLandableSquare(PieceColour colour, char c) {
        return c == ' ' || isDiffColor(colour, c);
    }

    private boolean isDiffColor(PieceColour colour, char c) {
        return isLower(c) && colour.equals(PieceColour.BLACK) || isUpper(c) && colour.equals(PieceColour.WHITE);
    }
}
