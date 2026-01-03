import java.util.*;

public class Pawn extends Piece {

    public Pawn(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'P';
    }

    //pionul se miscă înainte un pătrat (sau două pătrate la prima mis,care), dar capturează
    //piesele adversarului doar diagonal. Nu poate sări peste alte piese.
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int moveDirection = -1;
        int startRow = 7;
        char currentCol = position.getX();
        int currentRow = position.getY();

        // AICI ERA PROBLEMA: Trebuie sa verific culoarea INAINTE sa calculez forwardRow
        if (this.color == Colors.WHITE) {
            moveDirection = 1;
            startRow = 2;
        }

        // Acum calculam randul urmator cu directia corecta
        int forwardRow = currentRow + moveDirection;

        if (forwardRow >= 1 && forwardRow <= 8) {
            Position forwardPos = new Position(currentCol, forwardRow);

            if (board.getPieceAt(forwardPos) == null) {
                moves.add(forwardPos);

                //daca e pe randul de start, se poate misca 2 patratele
                //(verificam acest lucru doar daca primul patratel a fost liber)
                if (currentRow == startRow) {
                    int nextRow = currentRow + (moveDirection * 2);
                    // Verificare suplimentara sa nu iesim de pe tabla (desi e rar cazul aici)
                    if (nextRow >= 1 && nextRow <= 8) {
                        Position nextPos = new Position(currentCol, nextRow);
                        if (board.getPieceAt(nextPos) == null) {
                            moves.add(nextPos);
                        }
                    }
                }
            }
        }

        checkCapture(moves, board, -1, moveDirection);
        checkCapture(moves, board, 1, moveDirection);

        return moves;
    }

    //verific diagonalele, unde poate captura
    private void checkCapture(List<Position> moves, Board board, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();

        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;

        if (targetCol >= 'A' && targetCol <= 'H' && targetRow >= 1 && targetRow <= 8) {
            Position target = new Position(targetCol, targetRow);
            Piece pieceFound = board.getPieceAt(target);

            if (pieceFound != null && pieceFound.getColor() != this.color) {
                moves.add(target);
            }
        }
    }

    //verific daca regele e pe diagonala
    public boolean checkForCheck(Board board, Position kingPos) {
        int moveDirection = -1;
        if (this.color == Colors.WHITE) {
            moveDirection = 1;
        }

        if (checkThreat(kingPos, -1, moveDirection)) {
            return true;
        }
        if (checkThreat(kingPos, 1, moveDirection)) {
            return true;
        }

        return false;
    }

    //pionul da sah doar pe diagonala
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