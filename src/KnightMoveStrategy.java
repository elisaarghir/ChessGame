import java.util.*;

public class KnightMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        checkDirection(moves, board, position, 1, 2);
        checkDirection(moves, board, position, 2, 1);
        checkDirection(moves, board, position, 2, -1);
        checkDirection(moves, board, position, 1, -2);
        checkDirection(moves, board, position, -1, -2);
        checkDirection(moves, board, position, -2, -1);
        checkDirection(moves, board, position, -2, 1);
        checkDirection(moves, board, position, -1, 2);
        return moves;
    }

    private void checkDirection(List<Position> moves, Board board, Position currentPos, int colOffset, int rowOffset) {
        char currentCol = currentPos.getX();
        int currentRow = currentPos.getY();
        Colors myColor = board.getPieceAt(currentPos).getColor();

        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;

        if (targetCol >= 'A' && targetCol <= 'H' && targetRow >= 1 && targetRow <= 8) {
            Position target = new Position(targetCol, targetRow);
            Piece pieceFound = board.getPieceAt(target);

            if (pieceFound == null) {
                moves.add(target);
            } else {
                if (pieceFound.getColor() != myColor) {
                    moves.add(target);
                }
            }
        }
    }
}