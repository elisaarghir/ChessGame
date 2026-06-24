import java.util.*;

public class QueenMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        checkDirection(moves, board, position, 0, 1);
        checkDirection(moves, board, position, 0, -1);
        checkDirection(moves, board, position, -1, 0);
        checkDirection(moves, board, position, 1, 0);
        checkDirection(moves, board, position, 1, 1);
        checkDirection(moves, board, position, -1, 1);
        checkDirection(moves, board, position, 1, -1);
        checkDirection(moves, board, position, -1, -1);
        return moves;
    }

    private void checkDirection(List<Position> moves, Board board, Position currentPos, int colOffset, int rowOffset) {
        char currentCol = currentPos.getX();
        int currentRow = currentPos.getY();
        Colors myColor = board.getPieceAt(currentPos).getColor();
        boolean ok = true;

        while (ok) {
            currentCol = (char) (currentCol + colOffset);
            currentRow = currentRow + rowOffset;

            if (currentCol < 'A' || currentCol > 'H' || currentRow < 1 || currentRow > 8) {
                ok = false;
            } else {
                Position target = new Position(currentCol, currentRow);
                Piece pieceFound = board.getPieceAt(target);

                if (pieceFound == null) {
                    moves.add(target);
                } else {
                    if (pieceFound.getColor() != myColor) {
                        moves.add(target);
                    }
                    ok = false;
                }
            }
        }
    }
}