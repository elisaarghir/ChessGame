import java.util.*;

public class KingMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        char x = position.getX();
        int y = position.getY();
        Colors myColor = board.getPieceAt(position).getColor();

        checkValidity(moves, board, (char)(x - 1), y + 1, myColor);
        checkValidity(moves, board, x, y + 1, myColor);
        checkValidity(moves, board, (char)(x + 1), y + 1, myColor);
        checkValidity(moves, board, (char)(x - 1), y, myColor);
        checkValidity(moves, board, (char)(x + 1), y, myColor);
        checkValidity(moves, board, (char)(x - 1), y - 1, myColor);
        checkValidity(moves, board, x, y - 1, myColor);
        checkValidity(moves, board, (char)(x + 1), y - 1, myColor);

        return moves;
    }

    private void checkValidity(List<Position> moves, Board board, char x, int y, Colors myColor) {
        if (x >= 'A' && x <= 'H' && y >= 1 && y <= 8) {
            Position pos = new Position(x, y);
            Piece p = board.getPieceAt(pos);

            if (p == null || p.getColor() != myColor) {
                moves.add(pos);
            }
        }
    }
}
