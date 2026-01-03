import java.util.*;

public class Queen extends Piece {

    public Queen(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'Q';
    }

    //regina poate merge in orice directie, oricate patrate
    //verific atat pe orizontala si verticala, cat si pe diagonala
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        checkDirection(moves, board, 0, 1);
        checkDirection(moves, board, 0, -1);
        checkDirection(moves, board, -1, 0);
        checkDirection(moves, board, 1, 0);
        checkDirection(moves, board, 1, 1);
        checkDirection(moves, board, -1, 1);
        checkDirection(moves, board, 1, -1);
        checkDirection(moves, board, -1, -1);

        return moves;
    }

    //cu bucla while verific patratel cu patratel pana cand ajung la unul invalid
    //asa nu sar peste piese
    private void checkDirection(List<Position> moves, Board board, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();
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
                    if (pieceFound.getColor() != this.color) {
                        moves.add(target);
                    }
                    ok = false;
                }
            }
        }
    }

    public boolean checkForCheck(Board board, Position kingPos) {
        if (checkThreat(board, kingPos, 0, 1)) {
            return true;
        }
        if (checkThreat(board, kingPos, 0, -1)) {
            return true;
        }
        if (checkThreat(board, kingPos, -1, 0)) {
            return true;
        }
        if (checkThreat(board, kingPos, 1, 0)) {
            return true;
        }
        if (checkThreat(board, kingPos, 1, 1)) {
            return true;
        }
        if (checkThreat(board, kingPos, -1, 1)) {
            return true;
        }
        if (checkThreat(board, kingPos, 1, -1)) {
            return true;
        }
        if (checkThreat(board, kingPos, -1, -1)) {
            return true;
        }

        return false;
    }

    private boolean checkThreat(Board board, Position kingPos, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();
        boolean ok = true;
        boolean kingFound = false;

        while (ok) {
            currentCol = (char) (currentCol + colOffset);
            currentRow = currentRow + rowOffset;

            if (currentCol < 'A' || currentCol > 'H' || currentRow < 1 || currentRow > 8) {
                ok = false;
            } else {
                if (currentCol == kingPos.getX() && currentRow == kingPos.getY()) {
                    kingFound = true;
                    ok = false;
                } else if (board.getPieceAt(new Position(currentCol, currentRow)) != null) {
                    ok = false;
                }
            }
        }
        return kingFound;
    }
}