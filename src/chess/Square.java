package chess;

import chess.pieces.Piece;

public class Square {
	private final int i;
	private final int j;
	private boolean hasPiece;
	private Piece p;
	
	public Square(int iIn, int jIn) {
		i = iIn;
		j = jIn;
		hasPiece = false;
		p = null;
	}

	public Piece getPiece() {
		return p;
	}

	public void setPiece(Piece p) {
		this.p = p;
		this.hasPiece = true;
	}

	public Square deepcopy() {
		Square temp = new Square(i, j);
		temp.hasPiece = hasPiece;
		temp.p = p;
		return temp;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public void removePiece() {
		this.p = null;
		this.hasPiece = false;
	}

	public boolean hasPiece(){
		return hasPiece;
	}
	
}
