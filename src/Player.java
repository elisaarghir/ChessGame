import java.util.*;

public class Player {
    private String name;
    private Colors color;
    private List<Piece> capturedPieces;
    private TreeSet<ChessPair<Position, Piece>> ownedPieces;
    private int points;

    public Player(String name, Colors color) {
        this.name = name;
        this.color = color;
        this.capturedPieces = new ArrayList<>();
        this.ownedPieces = new TreeSet<>();
        this.points = 0;
    }

    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException {
        Piece piece = board.getPieceAt(from);
        Piece target = board.getPieceAt(to);

        if (piece == null) {
            throw new InvalidMoveException("No piece at starting position.");
        }

        if (piece.getColor() != this.color) {
            throw new InvalidMoveException("You can only move your own pieces.");
        }

        //mut piesa in pozitia to, iar daca acolo se afla deja o piesa, o
        //capturez si updatez punctele
        board.movePiece(from, to);
        if (target != null) {
            capturedPieces.add(target);
            updatePoints(target);
        }

        updateOwnedPieces(board);
    }

    //ma uit care piese de pe tabla au aceeasi culoare cu a mea si le adaug
    public void updateOwnedPieces(Board board) {
        ownedPieces.clear();
        for (ChessPair<Position, Piece> pair : board.getPieces()) {
            if (pair.getValue().getColor() == this.color) {
                ownedPieces.add(pair);
            }
        }
    }

    public List<ChessPair<Position, Piece>> getOwnedPieces() {
        return new ArrayList<>(ownedPieces);
    }

    private void updatePoints(Piece captured) {
        char type = captured.type();
        if (type == 'Q') {
            points = points + 90;
        } else if (type == 'R') {
            points = points + 50;
        } else if (type == 'B') {
            points = points + 30;
        } else if (type == 'N') {
            points = points + 30;
        } else if (type == 'P') {
            points = points + 10;
        }
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public Colors getColor() {
        return color;
    }
}