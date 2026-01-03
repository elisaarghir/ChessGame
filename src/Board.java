import java.util.*;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> pieces;

    public Board() {
        pieces = new TreeSet<>();
    }

    public void initialize() {
        pieces.clear();
        char c;

        for (c = 'A'; c <= 'H'; c++) {
            addPiece(new Pawn(Colors.WHITE, new Position(c, 2)));
            addPiece(new Pawn(Colors.BLACK, new Position(c, 7)));
        }

        addPiece(new Rook(Colors.WHITE, new Position('A', 1)));
        addPiece(new Knight(Colors.WHITE, new Position('B', 1)));
        addPiece(new Bishop(Colors.WHITE, new Position('C', 1)));
        addPiece(new Queen(Colors.WHITE, new Position('D', 1)));
        addPiece(new King(Colors.WHITE, new Position('E', 1)));
        addPiece(new Bishop(Colors.WHITE, new Position('F', 1)));
        addPiece(new Knight(Colors.WHITE, new Position('G', 1)));
        addPiece(new Rook(Colors.WHITE, new Position('H', 1)));

        addPiece(new Rook(Colors.BLACK, new Position('A', 8)));
        addPiece(new Knight(Colors.BLACK, new Position('B', 8)));
        addPiece(new Bishop(Colors.BLACK, new Position('C', 8)));
        addPiece(new Queen(Colors.BLACK, new Position('D', 8)));
        addPiece(new King(Colors.BLACK, new Position('E', 8)));
        addPiece(new Bishop(Colors.BLACK, new Position('F', 8)));
        addPiece(new Knight(Colors.BLACK, new Position('G', 8)));
        addPiece(new Rook(Colors.BLACK, new Position('H', 8)));
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

    //sterg din ChessPair perechea de pe pozitia de la from, in cazul in care e vorba de
    //o miscare valida, pentru a elimina patratelul in care se afla
    //de asemenea, daca urmeaza sa ma duc pe o pozitie valida si acolo se afla o piesa,
    //o capturez si o elimin de pe tabla de sah
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

    //dacă piesa mutată este un pion care, în urma mutării, ajunge pe ultima linie a tablei,
    //acesta trebuie promovat într-o altă piesă
    private Piece checkPawn(Piece p) {
        if (p instanceof Pawn) {
            if (p.getColor() == Colors.WHITE) {
                if (p.getPosition().getY() == 8) {
                    return new Queen(Colors.WHITE, p.getPosition());
                }
            }
            else if (p.getColor() == Colors.BLACK) {
                if (p.getPosition().getY() == 1) {
                    return new Queen(Colors.BLACK, p.getPosition());
                }
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

                //daca exista vreo piesa unde trebuie sa ajung, o elimin
                //eliberez si casuta de unde plec
                removePieceAt(from);
                if (target != null) {
                    removePieceAt(to);
                }
                p.setPosition(to);
                addPiece(p);

                //verific ca regele sa nu fie expus in cazul mutarii si sa nu se afle in sah
                valid = !isKingInCheck(p.getColor());

                //pun inapoi piesa pe tabla
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

    //verific cu aceasta functie daca regele se afla in pozitie de sah
    //initial, caut regele de culoarea din argument, printre toate piesele
    //cu o variabila isCheck retin daca regele se afla in pozitie de sah,
    //adica daca vreunul din adversarii sai il poate ataca
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
