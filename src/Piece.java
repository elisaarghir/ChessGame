import java.util.*;

public abstract class Piece implements ChessPiece {
    protected Colors color;
    protected Position position;
    protected MoveStrategy moveStrategy;

    public Piece(Colors color, Position position) {
        this.color = color;
        this.position = position;
    }

    public List<Position> getPossibleMoves(Board board) {
        if (moveStrategy != null) {
            return moveStrategy.getPossibleMoves(board, this.position);
        }
        return new ArrayList<>();
    }

    public abstract boolean checkForCheck(Board board, Position kingPos);
    public abstract char type();

    public Colors getColor() {
        return color; }
    public Position getPosition() {
        return position; }
    public void setPosition(Position position) {
        this.position = position; }
}