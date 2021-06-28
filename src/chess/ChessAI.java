package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Random;

public class ChessAI {

    private final String color;
    private Board board;
    private ChessDriver driver;
    private final int depth = 4;
    public int movesChecked = 0;
    Random r = new Random();

    public ChessAI(String color) {
        this.color = color;
    }

    private float staticEvaluation(Board board) {
        float evaluation = 0;

        for(Piece piece : board.pieces) {
            int turn = piece.getColour().toString().equals("WHITE") ? 1 : -1;
            if(piece instanceof Queen) {
                evaluation += turn * 9;
            }
            else if(piece instanceof Rook) {
                evaluation += turn * 5;
            }
            else if(piece instanceof Knight || piece instanceof Bishop) {
                evaluation += turn * 3;
            }
            else if(piece instanceof Pawn) {
                evaluation += turn;
            }
        }
        return evaluation;
    }
    private float staticEvaluation(LightBoard board) {
        float evaluation = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                switch (board.getPiece(i, j)) {
                    case 'p' -> evaluation += 1;
                    case 'b', 'n' -> evaluation += 3;
                    case 'r' -> evaluation += 5;
                    case 'q' -> evaluation += 9;
                    case 'P' -> evaluation -= 1;
                    case 'B', 'N' -> evaluation -= 3;
                    case 'R' -> evaluation -= 5;
                    case 'Q' -> evaluation -= 9;
                }
            }
        }
        return evaluation + r.nextFloat() - 0.5f;
    }

    private float minMax(LightBoard board, int depth, float alpha, float beta, String turn) {
        movesChecked++;
        if(depth == 0)
            return staticEvaluation(board);

        ArrayList<int[]> moves = board.getAllLegalMoves();
        float evaluation;
        if(turn.equals("WHITE")) {
            evaluation = Float.MIN_VALUE;
            for(int[] move : moves) {
//                Board daughter = board.deepcopy();
                LightBoard daughter = board.copy();
                daughter.makeMove(move[0], move[1], move[2], move[3]);
                evaluation = Float.max(evaluation, minMax(daughter, depth - 1, alpha, beta, "BLACK"));
                alpha = Float.max(evaluation, alpha);
                if(alpha >= beta)
                    break;
            }
        }
        else {
            evaluation = Float.MAX_VALUE;
            for(int[] move : moves) {
//                Board daughter = board.deepcopy();
                LightBoard daughter = board.copy();
                daughter.makeMove(move[0], move[1], move[2], move[3]);
                evaluation = Float.min(evaluation, minMax(daughter, depth - 1, alpha, beta, "WHITE"));
                beta = Float.min(evaluation, beta);
                if(alpha >= beta)
                    break;
            }
        }
//        System.out.printf("[ChessAI] At depth %d there are %d moves\n", this.depth - depth, movesChecked);
        return evaluation;
    }

    private float minMax(Board board, int depth, float alpha, float beta, String turn) {
        movesChecked++;
        if(depth == 0)
            return staticEvaluation(board);

        ArrayList<Move> moves = board.getLegalMoves();
        float evaluation;
        if(turn.equals("WHITE")) {
            evaluation = Float.MIN_VALUE;
            for(Move move : moves) {
                Board daughter = board.deepcopy();
                daughter.movePiece(move);
                evaluation = Float.max(evaluation, minMax(daughter, depth - 1, alpha, beta, "BLACK"));
                alpha = Float.max(evaluation, alpha);
                if(alpha >= beta)
                    break;
            }
        }
        else {
            evaluation = Float.MAX_VALUE;
            for(Move move : moves) {
                Board daughter = board.deepcopy();
                daughter.movePiece(move);
                evaluation = Float.min(evaluation, minMax(daughter, depth - 1, alpha, beta, "WHITE"));
                beta = Float.min(evaluation, beta);
                if(alpha >= beta)
                    break;
            }
        }
//        System.out.printf("[ChessAI] At depth %d there are %d moves\n", this.depth - depth, movesChecked);
        return evaluation;
    }

    public Move findMove() {
        movesChecked = 0;
        ArrayList<Move> moves = board.getLegalMoves();
        float best;
        Move bestMove = Move.NULL_MOVE;
        Board daughter;
        if(color.equals("WHITE")) {
            best = Float.MIN_VALUE;
            for (Move move : moves) {
                daughter = board.deepcopy();
                //int[] mv = move.toIntArray();
                daughter.movePiece(move);
                float eval = minMax(daughter, depth, Float.MIN_VALUE, Float.MAX_VALUE, "BLACK");
                if(eval >= best) {
                    best = eval;
                    bestMove = move;
                }
            }
        } else {
            best = Float.MAX_VALUE;
            for (Move move : moves) {
                daughter = board.deepcopy();
                //int[] mv = move.toIntArray();
                daughter.movePiece(move);
                float eval = minMax(daughter, depth, Float.MIN_VALUE, Float.MAX_VALUE, "WHITE");
                if(eval <= best) {
                    best = eval;
                    bestMove = move;
                }
            }
        }
        System.out.println("[ChessAI] Moves checked:" + movesChecked);
        System.out.println("[ChessAI] Evaluation:" + best);
        return bestMove;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setDriver(ChessDriver driver) {
        this.driver = driver;
    }

    public String getColor() {
        return color;
    }
}
