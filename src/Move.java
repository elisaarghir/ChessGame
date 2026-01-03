public class Move {
    private Colors playerColor;
    private Position startPosition;
    private Position endPosition;
    private Piece capturedPiece;

    public Move(Colors color, Position start, Position end, Piece captured) {
        this.playerColor = color;
        this.startPosition = start;
        this.endPosition = end;
        this.capturedPiece = captured;
    }

    public Colors getPlayerColor() {
        return playerColor;
    }
    public Position getStartPosition() {
        return startPosition;
    }
    public Position getEndPosition() {
        return endPosition;
    }
    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}