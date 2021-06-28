package chess;

import chess.pieces.*;

import java.util.ArrayList;

/**
 * Class used as an interface to the game. It contains all functionality needed to play moves and to retrieve
 * information about the chessboard. No other object from the chess package should be used to interact with the game.
 */
public class ChessDriver {
    public static final ChessDriver DUMMY_CHESS_DRIVER = new ChessDriver();

    private final Board board;
    private boolean gameOver;
    private PieceColour winner;
    private int[] promotingSquare;

    private ChessAI chessAI;
    private AIThread aiThread;
    private Move aiMove;

    private SpecialCase specialSituation;

    public ChessDriver () {
        board = new Board();
        board.driver = this;
        specialSituation = SpecialCase.NONE;
        this.chessAI = null;
    }

    public ChessDriver(String aiColour) {
        this();
        if(aiColour.toLowerCase().equals("white") || aiColour.toLowerCase().equals("black")) {
            this.chessAI = new ChessAI(aiColour);
            this.aiThread = new AIThread(chessAI);
            chessAI.setBoard(board);
            chessAI.setDriver(this);
            aiMove = Move.NULL_MOVE;
        }
    }

    public void setSpecialSituation(String specialSituation) {
        this.specialSituation = SpecialCase.valueOf(specialSituation);
    }

    public String getSituation() {
        return specialSituation.toString();
    }

    public enum SpecialCase {
        NONE, ENPASSANT, CASTLE, PROMOTION
    }

    public boolean checkMove(int i1, int j1, int i2, int j2) {
        Piece piece = board.getPiece(i1, j1);
        return piece.isLegitMove(i1, j1, i2, j2) && getTurn().equals(piece.getColour().toString());
    }

    public void promote(String piece, String color) {
        int i = promotingSquare[0];
        int j = promotingSquare[1];
        PieceColour col = color.equals("WHITE") ? PieceColour.WHITE : PieceColour.BLACK;
        switch (piece) {
            case "QUEEN"  -> board.setPiece(i, j, new Queen(col, board));
            case "ROOK"   -> board.setPiece(i, j, new Rook(col, board));
            case "BISHOP" -> board.setPiece(i, j, new Bishop(col, board));
            case "KNIGHT" -> board.setPiece(i, j, new Knight(col, board));
        }
    }

    public void makeMove(int i1, int j1, int i2, int j2) {
        Piece piece = board.getPiece(i1, j1);
        if(piece.isLegitMove(i1, j1, i2, j2)) {
            gameOver = board.movePiece(i1, j1, i2, j2, piece);
        }

        if(chessAI != null) {
            if (chessAI.getColor().equals(board.turn.toString()))
                aiThread.start();
                aiMove = aiThread.getAiMove();
        }
    }

    public void makeAIMove() {
        int[] move = getChessAIMove();
        makeMove(move[0], move[1], move[2], move[3]);
    }

    public int[] getPromotingSquare() {
        return new int[] {promotingSquare[0], promotingSquare[1]};
    }

    public void setPromotingSquare(int[] square) {
        promotingSquare = square;
    }

    public String getTurn() {
        return board.turn.toString();
    }

    public String getOtherTurn() {
        return board.turn.invert().toString();
    }

    public boolean isGameOver() {
        // This is in case of promotion checkmate
        if(specialSituation == SpecialCase.PROMOTION)
            return board.isCheckMate();
        return this.gameOver;
    }

    public String getWinner() {
        return board.isCheck().getColour().invert().toString();
    }

    public Board getBoard() {
        return board;
    }

    public String getLegalMovesReadable() {
        return board.getLegalMovesReadable();
    }

    public ArrayList<Move> getLegalMoves() {
        return board.getLegalMoves();
    }

    public boolean isComputerGame() {
        return chessAI != null;
    }

    public int[] getChessAIMove() throws NullPointerException {
        if(!aiMove.equals(Move.NULL_MOVE)) {
            int i = aiMove.getPiece().getRow();
            int j = aiMove.getPiece().getColumn();
            return new int[]{
                    i, j,
                    i + aiMove.direction[0],
                    j + aiMove.direction[1],
            };
        }
        throw new NullPointerException();
    }
}

