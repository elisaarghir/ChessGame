import java.util.*;

public class Bishop extends Piece {

    public Bishop(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'B';
    }

    //Nebunul se poate muta pe diagonala, oricate patrate
    //verific casutele de pe diagonala
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        checkDirection(moves, board, 1, 1);
        checkDirection(moves, board, -1, 1);
        checkDirection(moves, board, 1, -1);
        checkDirection(moves, board, -1, -1);

        return moves;
    }

    //functie pentru a verifica daca directia in care merge nebunul e valida
    //daca patratelul urmator iese din limite, ok devine false si nebunul nu mai poate inainta
    //daca in patratelul urmator se afla deja o piesa, la fel, nu mai poate inainta
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

    //daca am ajuns pe casuta in care e regele, kingFound devine true si e sah mat
    //bucla continua fie pana cand gasim regele, iesim din limitele board-ului sau
    //gasim o casuta ocupata
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