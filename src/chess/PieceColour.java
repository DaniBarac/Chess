package chess;

public enum PieceColour {
    WHITE, BLACK;

    public PieceColour invert() {
        return (this == PieceColour.BLACK ? PieceColour.WHITE : PieceColour.BLACK);
    }
}
