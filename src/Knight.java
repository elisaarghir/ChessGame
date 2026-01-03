import java.util.*;

public class Knight extends Piece {

    public Knight(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'N';
    }

    //calcul se misca in forma de "L"
    //doua patrate intr-o directie si un patrat perpendicular pe acesta
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        checkDirection(moves, board, 1, 2);
        checkDirection(moves, board, 2, 1);
        checkDirection(moves, board, 2, -1);
        checkDirection(moves, board, 1, -2);
        checkDirection(moves, board, -1, -2);
        checkDirection(moves, board, -2, -1);
        checkDirection(moves, board, -2, 1);
        checkDirection(moves, board, -1, 2);

        return moves;
    }

    //calculez direct target-ul final, unde vreau sa ajung, sarind peste piese
    private void checkDirection(List<Position> moves, Board board, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();

        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;

        if (targetCol >= 'A' && targetCol <= 'H' && targetRow >= 1 && targetRow <= 8) {
            Position target = new Position(targetCol, targetRow);
            Piece pieceFound = board.getPieceAt(target);

            if (pieceFound == null) {
                moves.add(target);
            } else {
                if (pieceFound.getColor() != this.color) {
                    moves.add(target);
                }
            }
        }
    }

    public boolean checkForCheck(Board board, Position kingPos) {
        if (checkThreat(kingPos, 1, 2)) {
            return true;
        }
        if (checkThreat(kingPos, 2, 1)) {
            return true;
        }
        if (checkThreat(kingPos, 2, -1)) {
            return true;
        }
        if (checkThreat(kingPos, 1, -2)) {
            return true;
        }
        if (checkThreat(kingPos, -1, -2)) {
            return true;
        }
        if (checkThreat(kingPos, -2, -1)) {
            return true;
        }
        if (checkThreat(kingPos, -2, 1)) {
            return true;
        }
        if (checkThreat(kingPos, -1, 2)) {
            return true;
        }

        return false;
    }

    //verific daca king se afla la pozitia target unde vreau sa ajung
    private boolean checkThreat(Position kingPos, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();

        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;

        if (targetCol == kingPos.getX() && targetRow == kingPos.getY()) {
            return true;
        }

        return false;
    }
}