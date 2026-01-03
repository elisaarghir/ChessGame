import java.util.*;

public class Game {
    private int idPlayer;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int currentPlayerIndex;

    public Game(int id, Player p1, Player p2) {
        this.idPlayer = id;
        this.player1 = p1;
        this.player2 = p2;
        this.board = new Board();
        this.moves = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }

    public void start() {
        board.initialize();
        moves.clear();

        if (player1.getColor() == Colors.WHITE) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex = 1;
        }
    }

    public void resume() {

    }

    public void switchPlayer() {
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = 1;
        } else {
            currentPlayerIndex = 0;
        }
    }

    //caut printre toate piesele jucatorului (de aceeasi culoare),
    //directiile in care pot merge
    //daca miscarea nu imi pune regele in pozitie de sah, atunci e valida
    public boolean checkForCheckMate() {
        Player currentPlayer = getCurrentPlayer();
        Colors myColor = currentPlayer.getColor();
        boolean hasLegalMove = false;

        List<ChessPair<Position, Piece>> allPieces = new ArrayList<>(board.getPieces());

        for (ChessPair<Position, Piece> pair : allPieces) {
            if (hasLegalMove == false) {
                Piece piece = pair.getValue();
                if (piece.getColor() == myColor) {
                    List<Position> moves = piece.getPossibleMoves(board);
                    for (Position dest : moves) {
                        // Aceasta metoda modifica tabla temporar, de aceea avem nevoie de copie pentru iteratie
                        if (board.isValidMove(piece.getPosition(), dest)) {
                            hasLegalMove = true;
                            break;
                        }
                    }
                }
            }
        }

        //nu e sah mat
        if (hasLegalMove == true) {
            return false;
        }

        //daca nu exista mutari si regele e atacat este sah mat
        if (board.isKingInCheck(myColor)) {
            return true;
        } else {
            return false;
        }
    }

    public void addMove(Player p, Position from, Position to, Piece captured) {
        moves.add(new Move(p.getColor(), from, to, captured));
    }

    public Player getCurrentPlayer() {
        if (currentPlayerIndex == 0) {
            return player1;
        }
        return player2;
    }

    public Board getBoard() {
        return board;
    }

    public int getId() {
        return idPlayer;
    }

    public List<Move> getMoves() {
        return moves;
    }

    //verific partea de egalitate
    //trebuie sa se fi realizat minim 12 mutari ca sa se repete
    public boolean checkDraw() {
        if (moves.size() < 12) {
            return false;
        }

        int index = moves.size() - 1;
        boolean jucatorCurentRepeta = false;
        boolean adversarRepeta = false;

        //iau ultimele 3 mutari ale jucatorului curent
        //mutarea de acum, de acum 1 ciclu si de acum 2 cicluri
        Move m1 = moves.get(index);
        Move m2 = moves.get(index - 4);
        Move m3 = moves.get(index - 8);

        //daca pozitiile mutarilor sunt identice intre ele, inseamna
        //ca jucatorul a repetat miscarile
        if (m1.getStartPosition().equals(m2.getStartPosition()) &&
                m1.getEndPosition().equals(m2.getEndPosition())) {

            if (m1.getStartPosition().equals(m3.getStartPosition()) &&
                    m1.getEndPosition().equals(m3.getEndPosition())) {

                jucatorCurentRepeta = true;
            }
        }

        //analog pentru adversar
        Move o1 = moves.get(index - 1);
        Move o2 = moves.get(index - 5);
        Move o3 = moves.get(index - 9);

        if (o1.getStartPosition().equals(o2.getStartPosition()) &&
                o1.getEndPosition().equals(o2.getEndPosition())) {

            if (o1.getStartPosition().equals(o3.getStartPosition()) &&
                    o1.getEndPosition().equals(o3.getEndPosition())) {

                adversarRepeta = true;
            }
        }

        //daca amandoi au repetat miscarile, atunci e caz de egalitate
        if (jucatorCurentRepeta == true && adversarRepeta == true) {
            return true;
        } else {
            return false;
        }
    }
}