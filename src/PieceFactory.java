import java.util.*;

public class PieceFactory {

    //clasa care ma ajuta in constructia Factory Pattern
    //primeste un type si decide ce pion sa construiasca
    //daca vreau sa adaug o noua piesa in viitor trebuie sa schimb doar
    //logica de aici sa includa noul element
    public static Piece createPiece(String type, Colors color, Position position) {
        if (type == null) return null;

        switch (type.toUpperCase()) {
            case "P":
                return new Pawn(color, position);
            case "R":
                return new Rook(color, position);
            case "N":
                return new Knight(color, position);
            case "B":
                return new Bishop(color, position);
            case "Q":
                return new Queen(color, position);
            case "K":
                return new King(color, position);
            default:
                return null;
        }
    }
}