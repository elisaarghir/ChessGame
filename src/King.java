import java.util.*;

public class King extends Piece {

    public King(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'K';
    }

    //king se misca un patrat in orice directie (8 cazuri)
    //verific toate pozitiile respective sa vad daca sunt valide
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        char x = position.getX();
        int y = position.getY();

        checkValidity(moves, board, (char)(x - 1), y + 1);
        checkValidity(moves, board, x, y + 1);
        checkValidity(moves, board, (char)(x + 1), y + 1);
        checkValidity(moves, board, (char)(x - 1), y);
        checkValidity(moves, board, (char)(x + 1), y);
        checkValidity(moves, board, (char)(x - 1), y - 1);
        checkValidity(moves, board, x, y - 1);
        checkValidity(moves, board, (char)(x + 1), y - 1);

        return moves;
    }

    //functie pentru a verifica daca o pozitie se afla pe tabla
    //daca casuta e goala si nu e niciun inamic acolo, o trece in lista de mutari posibile
    private void checkValidity(List<Position> moves, Board board, char x, int y) {
        if (x >= 'A' && x <= 'H' && y >= 1 && y <= 8) {
            Position pos = new Position(x, y);
            Piece p = board.getPieceAt(pos);

            if (p == null || p.getColor() != this.color) {
                moves.add(pos);
            }
        }
    }

    //daca in casuta vecina se afla celalalt rege, returnez true
    public boolean checkForCheck(Board board, Position kingPos) {
        char x = position.getX();
        int y = position.getY();

        if (checkOtherKing((char)(x - 1), y + 1, kingPos)) {
            return true;
        }
        if (checkOtherKing(x, y + 1, kingPos)) {
            return true;
        }
        if (checkOtherKing((char)(x + 1), y + 1, kingPos)) {
            return true;
        }
        if (checkOtherKing((char)(x - 1), y, kingPos)) {
            return true;
        }
        if (checkOtherKing((char)(x + 1), y, kingPos)) {
            return true;
        }
        if (checkOtherKing((char)(x - 1), y - 1, kingPos)) {
            return true;
        }
        if (checkOtherKing(x, y - 1, kingPos)) {
            return true;
        }
        if (checkOtherKing((char)(x + 1), y - 1, kingPos)) {
            return true;
        }

        return false;
    }

    //verific daca vecinului actualului king este chiar celalalt king
    private boolean checkOtherKing(char x, int y, Position kingPos) {
        if (x == kingPos.getX() && y == kingPos.getY()) {
            return true;
        }
        return false;
    }
}