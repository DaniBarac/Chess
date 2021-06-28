package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Random;

public class Board {
	private Square [][] board = new Square[8][8];
	public ArrayList<Piece> pieces = new ArrayList<>();
	public King[] kings;
	public int[] enPassant = new int[2];
	public PieceColour turn;

	protected ChessDriver driver;

	public enum BoardState {
		EMPTY, STANDARD, PROMO, RANDOM
	}

	public Board(BoardState state) {
		// Initializing the attribute board
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++)
				board[i][j] = new Square(i, j);
		}
		kings = new King[2];
		turn = PieceColour.WHITE;
		driver = ChessDriver.DUMMY_CHESS_DRIVER;
		switch (state) {
			case STANDARD -> initialisePieces();
			case PROMO -> initPromoBoard();
			case RANDOM -> initRandomBoard();
		}
	}

	private void initRandomBoard() {
		Random r = new Random();
		for(int k = 0; k < 10; k++) {
			int i, j;
			do {
				i = r.nextInt(8);
				j = r.nextInt(8);
			} while(board[i][j].hasPiece());
			int p = r.nextInt(5);
			switch (p) {
//				case 0 -> board[i][j].setPiece(
//						new Pawn(k % 2 == 0 ? PieceColour.WHITE : PieceColour.BLACK, this));
				case 1 -> board[i][j].setPiece(
						new Bishop(k % 2 == 0 ? PieceColour.WHITE : PieceColour.BLACK, this));
				case 2 -> board[i][j].setPiece(
						new Knight(k % 2 == 0 ? PieceColour.WHITE : PieceColour.BLACK, this));
				case 3 -> board[i][j].setPiece(
						new Rook(k % 2 == 0 ? PieceColour.WHITE : PieceColour.BLACK, this));
				case 4 -> board[i][j].setPiece(
						new Queen(k % 2 == 0 ? PieceColour.WHITE : PieceColour.BLACK, this));
			}
		}
	}

	public Board() {
		this(BoardState.STANDARD);
	}

	public Square[][] getBoard(){
		return board;
	}
	
	public void initialisePieces() {
		int i = 0;

		// Initializing all pieces except the pawns
		for(PieceColour colour : PieceColour.values()) {
			Rook r = new Rook(colour, this);
			this.setPiece(i, 0, r);
			r.canCastle = true;
			r = new Rook(colour, this);
			this.setPiece(i, 7, r);
			r.canCastle = true;

			this.setPiece(i, 1, new Knight(colour, this));
			this.setPiece(i, 6, new Knight(colour, this));

			this.setPiece(i, 2, new Bishop(colour, this));
			this.setPiece(i, 5, new Bishop(colour, this));

			this.setPiece(i, 3, new Queen(colour, this));
			this.setPiece(i, 4, new King(colour, this));

			i = 7;
		}

		// Initializing the pawns
		for(int j = 0; j < 8; j++) {
			this.setPiece(1, j, new Pawn(PieceColour.WHITE, this));
			this.setPiece(6, j, new Pawn(PieceColour.BLACK, this));
		}

		for(King king : kings)
			king.canCastle = true;
	}

	public void initPromoBoard() {
		this.setPiece(1, 3, new Pawn(PieceColour.WHITE, this));
		this.setPiece(1, 4, new King(PieceColour.WHITE, this));
		this.setPiece(1, 6, new King(PieceColour.BLACK, this));
	}
	
	public void printBoard() {
		System.out.print("\n   a b c d e f g h \n");
		System.out.print("  -----------------\n");
		
		for (int i = board[0].length - 1; i >= 0; i--){
			int row = i + 1;
			System.out.print(row + " ");
			for (int j = 0; j < board[1].length; j++){
				if (board[i][j].hasPiece())
					System.out.print("|" + board[i][j].getPiece().getSymbol());
				else
					System.out.print("| ");

			}
			System.out.print("| "+row+"\n");
		}
		System.out.print("  -----------------");
		System.out.print("\n   a b c d e f g h \n");
	
	}

	public boolean movePiece(int i0, int j0, int i1, int j1, Piece p) {
		if(this.hasPiece(i1, j1))
			pieces.remove(this.getPiece(i1, j1));

		if(p instanceof Pawn) {
			if(((Pawn) p).enPassant) {
				this.setPiece(i1 - (p.getColour() == PieceColour.WHITE ? 1 : -1), j1, null);
				driver.setSpecialSituation("ENPASSANT");
			} else if(i1 == 7 && p.getColour() == PieceColour.WHITE ||
					i1 == 0 && p.getColour() == PieceColour.BLACK) {
				driver.setSpecialSituation("PROMOTION");
				driver.setPromotingSquare(new int[]{i1, j1});
			}
			else
				driver.setSpecialSituation("NONE");
		}
		else if(p instanceof King && ((King) p).canCastle && i0 == i1 && Math.abs(j1 - j0) == 2) {
			int rookPos = (j0 - j1) > 0 ? 0 : 7;
			Rook r = (Rook) this.getPiece(i0, rookPos);
			this.setPiece(i0, rookPos, null);
			this.setPiece(i0, j0 + (rookPos == 0 ? -1 : 1), r);
			driver.setSpecialSituation("CASTLE");
		}
		else
			driver.setSpecialSituation("NONE");

		// Removing the piece from the origin
		this.setPiece(i0, j0, null);
		// Setting the piece on the destination
		this.setPiece(i1, j1, p);

		turn = turn.invert();

		return isCheckMate();
	}

	public boolean movePiece(Move move) {
		Piece piece = move.getPiece();
		int i1 = piece.getRow();
		int j1 = piece.getColumn();
		piece = getPiece(i1, j1);
		int i2 = move.direction[0] + i1;
		int j2 = move.direction[1] + j1;
		return movePiece(i1, j1, i2, j2, piece);
	}

	public boolean isCheckMate() {
		return isCheck() != null && getLegalMoves().isEmpty();
	}

	/**
	 * Method that executes a move without checking if it follows the rules of the game.
	 * Used for quickly checking if a move results in check, on a copy of the board. It
	 * will alter the state of the board, so it is recommended that you use this on a copy.
	 * @param i0 piece row
	 * @param j0 piece column
	 * @param i1 destination row
	 * @param j1 destination column
	 * @param p piece to be moved
	 */
	public void forceMove(int i0, int j0, int i1, int j1, Piece p) {
		if(this.hasPiece(i1, j1)) {
			pieces.remove(this.getPiece(i1, j1));
		}
		// Removing the piece from the origin
		this.setPiece(i0, j0, null);
		// Setting the piece on the destination
		this.setPiece(i1, j1, p);
		turn = turn.invert();
	}

	public void setPiece(int iIn, int jIn, Piece p){
		// If p is null, then any piece that was on (iIn, jIn) is removed
		if(p == null) {
			board[iIn][jIn].removePiece();
		}
		else {
			board[iIn][jIn].setPiece(p);
			p.updateCoordinates(iIn, jIn);
		}
	}

	public ArrayList<Move> getLegalMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		for(Piece piece : pieces) {
			if(piece.getColour() == turn) {
				moves.addAll(piece.getLegalMoves());
			}
		}
		return moves;
	}

	public String getLegalMovesReadable() {
		StringBuilder out = new StringBuilder();
		out.append("LEGAL MOVES - ").append(turn.toString()).append(" TO MOVE\n");
		for(Move move : getLegalMoves()) {
			out.append("	").append(move.toString()).append("\n");
		}
		return out.toString();
	}

	public boolean hasMoves() {
		for(Piece piece : pieces) {
			if(piece.getColour() == turn && piece.hasMoves())
				return true;
		}
		return false;
	}

	public King isCheck() {

		int i = turn.equals(PieceColour.WHITE) ? 1 : 0;
		while(i >= 0 && i <= 1) {
			for(Piece piece : pieces) {
				int ik = kings[i].getRow();
				int jk = kings[i].getColumn();
				if (piece instanceof Bishop) {
					int ib = piece.getRow();
					int jb = piece.getColumn();
					if(piece.isLegitDiagonalMove(ib, jb, ik, jk) && piece.getColour() != kings[i].getColour()) {
						return kings[i];
					}
				}
				if (piece instanceof Rook) {
					int ir = piece.getRow();
					int jr = piece.getColumn();
					if(piece.isLegitStraightMove(ir, jr, ik, jk) && piece.getColour() != kings[i].getColour()) {
						return kings[i];
					}
				}
				if (piece instanceof Queen) {
					int iq = piece.getRow();
					int jq = piece.getColumn();
					if ((piece.isLegitStraightMove(iq, jq, ik, jk) || piece.isLegitDiagonalMove(iq, jq, ik, jk)) &&
							piece.getColour() != kings[i].getColour()){
						return kings[i];
					}
				}
				if (piece instanceof Knight) {
					int in = piece.getRow();
					int jn = piece.getColumn();
					if (((Math.abs(in - ik) == 2 && Math.abs(jn - jk) == 1) ||
							(Math.abs(in - ik) == 1 && Math.abs(jn - jk) == 2)) &&
							piece.getColour() != kings[i].getColour()) {
						return kings[i];
					}
				}
				if (piece instanceof Pawn) {
					Pawn p = (Pawn) piece;
					int ip = piece.getRow();
					int jp = piece.getColumn();
					if(p.canCapture(ip, jp, ik, jk) && piece.getColour() != kings[i].getColour()) {
						return kings[i];
					}
				}
				if (piece instanceof King) {
					int iu = piece.getRow();
					int ju = piece.getColumn();
					if(Math.abs(ik - iu) <= 1 && Math.abs(jk - ju) <= 1 && piece.getColour() != kings[i].getColour()) {
						return new King(null, this);
					}
				}
			}
			i += turn.equals(PieceColour.WHITE) ? -1 : 1;
		}
		return null;
	}

	/**
	 * Makes copies of the board and any elements associated with it (with the exception of the driver).
	 * @return Board object
	 */
	// NOTE: Pieces are added to this.pieces in their constructor, so when you call piece.deepcopy()
	// the piece is added to the pieces list, SO DO NOT RE ADD IT.
	public Board deepcopy() {
		Board result = new Board();

		result.enPassant = new int[] {this.enPassant[0], this.enPassant[1]};
		result.board = new Square[8][8];
		result.driver = ChessDriver.DUMMY_CHESS_DRIVER;
		result.turn = turn;
		int k = 0;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Square t = this.board[i][j].deepcopy();
				if(t.hasPiece()) {
					Piece p = t.getPiece().deepcopy(result);
					t.setPiece(p);
					if(p instanceof King)
						result.kings[k++] = (King) p;
				}
				result.board[i][j] = t;
			}
		}
		return result;
	}
	
	public Piece getPiece(int iIn, int jIn){
		return board[iIn][jIn].getPiece();
	}
	
	public boolean hasPiece(int i, int j){
		return board[i][j].hasPiece();
	}

	/**
	 * Returns the board as a matrix of characters.
	 * <p>
	 * Lowercase pieces are white, uppercase are black. The values are as follows:
	 * </p>
	 * <ul>
	 * 	<li>' ' - empty square</li>
	 * 	<li>'p' & 'P' - pawn</li>
	 * 	<li>'n' & 'N' - knight</li>
	 * 	<li>'b' & 'B' - bishop</li>
	 * 	<li>'r' & 'R' - rook</li>
	 * 	<li>'q' & 'Q' - queen</li>
	 * 	<li>'k' & 'K' - king</li>
	 * </ul>
	 * @return Returns a char[8][8] that represents the board
	 */
	public char[][] getBoardAsMatrix() {
		char[][] matrix = new char[8][8];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j].hasPiece()) {
					String piece = board[i][j].getPiece().getClass().getName().toString();
					String col = board[i][j].getPiece().getColour().toString();
					switch (piece) {
						case "chess.pieces.Pawn" -> matrix[i][j] = 'p';
						case "chess.pieces.Knight" -> matrix[i][j] = 'n';
						case "chess.pieces.Bishop" -> matrix[i][j] = 'b';
						case "chess.pieces.Rook" -> matrix[i][j] = 'r';
						case "chess.pieces.Queen" -> matrix[i][j] = 'q';
						case "chess.pieces.King" -> matrix[i][j] = 'k';
					}
					if(col.equals("BLACK"))
						matrix[i][j] = (char) (matrix[i][j] - 32);
				} else
					matrix[i][j] = ' ';
			}
		}

		return matrix;
	}
}
