import java.util.*;

public class PawnMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        int moveDirection = -1;
        int startRow = 7;
        char currentCol = position.getX();
        int currentRow = position.getY();
        Colors color = board.getPieceAt(position).getColor();

        if (color == Colors.WHITE) {
            moveDirection = 1;
            startRow = 2;
        }

        int forwardRow = currentRow + moveDirection;

        if (forwardRow >= 1 && forwardRow <= 8) {
            Position forwardPos = new Position(currentCol, forwardRow);

            if (board.getPieceAt(forwardPos) == null) {
                moves.add(forwardPos);

                if (currentRow == startRow) {
                    int nextRow = currentRow + (moveDirection * 2);
                    if (nextRow >= 1 && nextRow <= 8) {
                        Position nextPos = new Position(currentCol, nextRow);
                        if (board.getPieceAt(nextPos) == null) {
                            moves.add(nextPos);
                        }
                    }
                }
            }
        }

        checkCapture(moves, board, position, -1, moveDirection, color);
        checkCapture(moves, board, position, 1, moveDirection, color);

        return moves;
    }

    private void checkCapture(List<Position> moves, Board board, Position position, int colOffset, int rowOffset, Colors color) {
        char currentCol = position.getX();
        int currentRow = position.getY();

        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;

        if (targetCol >= 'A' && targetCol <= 'H' && targetRow >= 1 && targetRow <= 8) {
            Position target = new Position(targetCol, targetRow);
            Piece pieceFound = board.getPieceAt(target);

            if (pieceFound != null && pieceFound.getColor() != color) {
                moves.add(target);
            }
        }
    }
}