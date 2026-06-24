import java.util.*;

public class Game {
    private int idPlayer;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int currentPlayerIndex;
    private Scanner scanner;
    private ScoringStrategy scoringStrategy;
    private GameObserver observer;

    public Game(int id, Player p1, Player p2) {
        this.idPlayer = id;
        this.player1 = p1;
        this.player2 = p2;
        this.board = new Board();
        this.moves = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
        this.scoringStrategy = new StandardScoringStrategy();
    }

    //strategia e apelata pentru a calcula punctele de resign
    public void resign(User user) {
        int bonus = scoringStrategy.getEndGameBonus("LOSS_RESIGN");
        handleGameEnd(user, bonus, "DEFEAT (Resigned)");
    }

    public void handleGameEnd(User user, int bonusPoints, String resultMsg) {
        int pointsFromPiecesY = 0;
        Colors userColor = this.player1.getColor();

        //se foloseste STRATEGY pentru a obtine punctajul pentru piesele capturate
        for (Move m : this.moves) {
            if (m.getPlayerColor() == userColor && m.getCapturedPiece() != null) {
                pointsFromPiecesY += scoringStrategy.getPieceValue(m.getCapturedPiece());
            }
        }

        int roundTotal = pointsFromPiecesY + bonusPoints;
        int oldX = user.getPoints();
        int newX = oldX + roundTotal;

        user.setPoints(newX);
        user.removeGame(this);

        Main.getInstance().getGamesMap().remove(this.idPlayer);
        Main.getInstance().write();

        if (observer != null) {
            observer.onGameEnd(resultMsg, pointsFromPiecesY, bonusPoints, newX);
        }
    }

    public int getPlayerScore(Player p) {
        int points = 0;
        for (Move m : moves) {
            if (m.getPlayerColor() == p.getColor() && m.getCapturedPiece() != null) {
                points += scoringStrategy.getPieceValue(m.getCapturedPiece());
            }
        }
        return points;
    }

    public void start(User currentUser, Map<Integer, Game> gamesMap) {
        board.initialize();
        moves.clear();
        if (player1.getColor() == Colors.WHITE) currentPlayerIndex = 0;
        else currentPlayerIndex = 1;
    }

    //primeste un observator pe care il salveaza in memoria sa
    public void setObserver(GameObserver observer) {
        this.observer = observer;
    }

    public void addMove(Player p, Position from, Position to, Piece captured) {
        Move m = new Move(p.getColor(), from, to, captured);
        moves.add(m);
        //se notifica observatorul
        if (observer != null) observer.onMoveMade(m);
    }

    public boolean checkForCheckMate() {
        Player currentPlayer = getCurrentPlayer();
        Colors myColor = currentPlayer.getColor();
        boolean hasLegalMove = false;
        List<ChessPair<Position, Piece>> allPieces = new ArrayList<>(board.getPieces());

        for (ChessPair<Position, Piece> pair : allPieces) {
            if (!hasLegalMove) {
                Piece piece = pair.getValue();
                if (piece.getColor() == myColor) {
                    List<Position> moves = piece.getPossibleMoves(board);
                    for (Position dest : moves) {
                        if (board.isValidMove(piece.getPosition(), dest)) {
                            hasLegalMove = true;
                            break;
                        }
                    }
                }
            }
        }
        if (hasLegalMove) return false;
        return board.isKingInCheck(myColor);
    }

    public boolean checkDraw() {
        if (moves.size() < 12) return false;
        int index = moves.size() - 1;
        boolean jucatorCurentRepeta = false;
        boolean adversarRepeta = false;

        Move m1 = moves.get(index);
        Move m2 = moves.get(index - 4);
        Move m3 = moves.get(index - 8);

        if (m1.getStartPosition().equals(m2.getStartPosition()) &&
                m1.getEndPosition().equals(m2.getEndPosition()) &&
                m1.getStartPosition().equals(m3.getStartPosition()) &&
                m1.getEndPosition().equals(m3.getEndPosition())) {
            jucatorCurentRepeta = true;
        }

        Move o1 = moves.get(index - 1);
        Move o2 = moves.get(index - 5);
        Move o3 = moves.get(index - 9);

        if (o1.getStartPosition().equals(o2.getStartPosition()) &&
                o1.getEndPosition().equals(o2.getEndPosition()) &&
                o1.getStartPosition().equals(o3.getStartPosition()) &&
                o1.getEndPosition().equals(o3.getEndPosition())) {
            adversarRepeta = true;
        }

        return jucatorCurentRepeta && adversarRepeta;
    }

    public Player getCurrentPlayer() { return (currentPlayerIndex == 0) ? player1 : player2; }
    public void switchPlayer() { currentPlayerIndex = (currentPlayerIndex == 0) ? 1 : 0; }
    public Board getBoard() { return board; }
    public List<Move> getMoves() { return moves; }
    public int getId() { return idPlayer; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public ScoringStrategy getScoringStrategy() { return scoringStrategy; }
}