import java.util.*;
import javax.swing.JOptionPane;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> pieces;

    public Board() {
        pieces = new TreeSet<>();
    }

    public void initialize() {
        pieces.clear();
        char c;

        for (c = 'A'; c <= 'H'; c++) {
            addPiece(PieceFactory.createPiece("P", Colors.WHITE, new Position(c, 2)));
            addPiece(PieceFactory.createPiece("P", Colors.BLACK, new Position(c, 7)));
        }

        //utilizez factory-ul creat pentru a aranja piesele pe tabla
        //util deoarece clasa board nu trebuie sa cunoasca toate tipurile de piese
        //ci le ia doar dupa initiale
        addPiece(PieceFactory.createPiece("R", Colors.WHITE, new Position('A', 1)));
        addPiece(PieceFactory.createPiece("N", Colors.WHITE, new Position('B', 1)));
        addPiece(PieceFactory.createPiece("B", Colors.WHITE, new Position('C', 1)));
        addPiece(PieceFactory.createPiece("Q", Colors.WHITE, new Position('D', 1)));
        addPiece(PieceFactory.createPiece("K", Colors.WHITE, new Position('E', 1)));
        addPiece(PieceFactory.createPiece("B", Colors.WHITE, new Position('F', 1)));
        addPiece(PieceFactory.createPiece("N", Colors.WHITE, new Position('G', 1)));
        addPiece(PieceFactory.createPiece("R", Colors.WHITE, new Position('H', 1)));

        addPiece(PieceFactory.createPiece("R", Colors.BLACK, new Position('A', 8)));
        addPiece(PieceFactory.createPiece("N", Colors.BLACK, new Position('B', 8)));
        addPiece(PieceFactory.createPiece("B", Colors.BLACK, new Position('C', 8)));
        addPiece(PieceFactory.createPiece("Q", Colors.BLACK, new Position('D', 8)));
        addPiece(PieceFactory.createPiece("K", Colors.BLACK, new Position('E', 8)));
        addPiece(PieceFactory.createPiece("B", Colors.BLACK, new Position('F', 8)));
        addPiece(PieceFactory.createPiece("N", Colors.BLACK, new Position('G', 8)));
        addPiece(PieceFactory.createPiece("R", Colors.BLACK, new Position('H', 8)));
    }

    public void addPiece(Piece p) {
        pieces.add(new ChessPair<>(p.getPosition(), p));
    }

    public Piece getPieceAt(Position position) {
        for (ChessPair<Position, Piece> chessPair : pieces) {
            if (chessPair.getKey().equals(position)) {
                return chessPair.getValue();
            }
        }
        return null;
    }

    public void removePieceAt(Position position) {
        Iterator<ChessPair<Position, Piece>> it = pieces.iterator();
        while (it.hasNext()) {
            ChessPair<Position, Piece> pair = it.next();
            if (pair.getKey().equals(position)) {
                it.remove();
                return;
            }
        }
    }

    public void movePiece(Position from, Position to) throws InvalidMoveException {
        Piece p = getPieceAt(from);
        Piece nextP = getPieceAt(to);

        if (isValidMove(from, to) == false) {
            throw new InvalidMoveException("Invalid move");
        }

        removePieceAt(from);
        if (nextP != null) {
            removePieceAt(to);
        }
        p.setPosition(to);
        p = checkPawn(p);

        addPiece(p);
    }

    private Piece checkPawn(Piece p) {
        if (p instanceof Pawn) {
            boolean isWhiteAtEnd = (p.getColor() == Colors.WHITE && p.getPosition().getY() == 8);
            boolean isBlackAtEnd = (p.getColor() == Colors.BLACK && p.getPosition().getY() == 1);

            if (isWhiteAtEnd || isBlackAtEnd) {
                String promotionType = "Q";

                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Choose piece for promotion:",
                        "Pawn Promotion",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                if (choice == 1) promotionType = "R";
                else if (choice == 2) promotionType = "B";
                else if (choice == 3) promotionType = "N";
                else promotionType = "Q";

                //preia tipul ales de la utilizator si il transforma intr-o piesa
                //utilizand factory-ul
                return PieceFactory.createPiece(promotionType, p.getColor(), p.getPosition());
            }
        }
        return p;
    }

    public boolean isValidMove(Position from, Position to) {
        Piece p = getPieceAt(from);
        List<Position> moves;
        boolean valid;

        if(from == to || p == null) {
            return false;
        }

        if (p != null) {
            moves = p.getPossibleMoves(this);
            if (moves.contains(to)) {
                Piece target = getPieceAt(to);
                Position startPos = p.getPosition();

                removePieceAt(from);
                if (target != null) {
                    removePieceAt(to);
                }
                p.setPosition(to);
                addPiece(p);

                valid = !isKingInCheck(p.getColor());
                removePieceAt(to);
                p.setPosition(startPos);
                addPiece(p);
                if (target != null) {
                    addPiece(target);
                }
                return valid;
            }
        }

        return false;
    }

    public boolean isKingInCheck(Colors color) {
        Position kingPos = null;
        boolean foundKing = false;
        boolean isCheck = false;

        for (ChessPair<Position, Piece> pair : pieces) {
            if (foundKing == false) {
                Piece currentPiece = pair.getValue();

                if (currentPiece instanceof King) {
                    if (currentPiece.getColor() == color) {
                        kingPos = pair.getKey();
                        foundKing = true;
                    }
                }
            }
        }

        if (kingPos == null) {
            return true;
        }

        for (ChessPair<Position, Piece> pair : pieces) {
            if (isCheck == false) {
                Piece enemy = pair.getValue();
                if (enemy.getColor() != color) {
                    if (enemy.checkForCheck(this, kingPos)) {
                        isCheck = true;
                    }
                }
            }
        }

        return isCheck;
    }

    public TreeSet<ChessPair<Position, Piece>> getPieces() {
        return pieces;
    }

}
