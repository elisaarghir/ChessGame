import java.util.*;

public class Game {
    private int idPlayer;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int currentPlayerIndex;
    private Scanner scanner;

    public Game(int id, Player p1, Player p2) {
        this.idPlayer = id;
        this.player1 = p1;
        this.player2 = p2;
        this.board = new Board();
        this.moves = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    public void start(User currentUser, Map<Integer, Game> gamesMap) {
        board.initialize();
        moves.clear();

        if (player1.getColor() == Colors.WHITE) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex = 1;
        }

        playLoop(currentUser, gamesMap);
    }

    public void resume(User currentUser, Map<Integer, Game> gamesMap) {
        playLoop(currentUser, gamesMap);
    }

    private void playLoop(User currentUser, Map<Integer, Game> gamesMap) {
        boolean active = true;
        System.out.println("\n--- GAME STARTED/RESUMED ---");
        System.out.println("Commands: 'exit' (Save), 'resign' (Give Up), 'E2-E4' (Move) or 'E2' (Check moves)");

        while (active) {
            printBoard();
            Player current = getCurrentPlayer();
            System.out.println("Turn: " + current.getName() + " (" + current.getColor() + ")");

            if (checkDraw()) {
                System.out.println("DRAW DETECTED!");
                handleGameEnd(currentUser, gamesMap, current, 150, 0);
                active = false;
                break;
            }

            if (checkForCheckMate()) {
                System.out.println("CHECKMATE! Game Over.");
                boolean humanLost = !current.getName().equalsIgnoreCase("Computer");

                if (humanLost) {
                    System.out.println("You LOST!");
                    handleGameEnd(currentUser, gamesMap, current, -300, 0);
                } else {
                    System.out.println("You WON!");
                    handleGameEnd(currentUser, gamesMap, current, 300, 1);
                }
                active = false;
                break;
            }

            if (!current.getName().equalsIgnoreCase("Computer")) {
                System.out.print("Action: ");
                if (!scanner.hasNextLine()) { active = false; break; }
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Game state saved. Returning to menu.");
                    active = false;
                    break;
                }

                if (input.equalsIgnoreCase("resign")) {
                    System.out.println("You resigned.");
                    handleGameEnd(currentUser, gamesMap, current, -150, 0);
                    active = false;
                    break;
                }

                processHumanMove(input, current);

            } else {
                System.out.println("Computer is thinking...");
                try {
                    Thread.sleep(800);
                    makeComputerMove(current);

                    Colors enemyColor = (current.getColor() == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
                    if (board.isKingInCheck(enemyColor)) {
                        System.out.println("CHECK!");
                    }
                    switchPlayer();
                } catch (Exception e) {
                    System.out.println("Computer error: " + e.getMessage());
                    active = false;
                }
            }
        }
    }

    private void processHumanMove(String input, Player current) {
        if (input.contains("-")) {
            try {
                String[] parts = input.split("-");
                if (parts.length != 2) throw new Exception("Invalid format.");
                Position from = parsePosition(parts[0]);
                Position to = parsePosition(parts[1]);

                if (board.getPieceAt(from) == null) {
                    System.out.println("No piece at start position.");
                    return;
                }

                Piece captured = board.getPieceAt(to);
                current.makeMove(from, to, board);
                addMove(current, from, to, captured);

                Colors enemyColor = (current.getColor() == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
                if (board.isKingInCheck(enemyColor)) {
                    System.out.println("CHECK!");
                }

                switchPlayer();

            } catch (Exception e) {
                System.out.println("Invalid move: " + e.getMessage());
            }
        } else {
            try {
                Position pos = parsePosition(input);
                Piece p = board.getPieceAt(pos);

                if (p == null) {
                    System.out.println("No piece at " + pos);
                } else if (p.getColor() != current.getColor()) {
                    System.out.println("That is not your piece!");
                } else {
                    List<Position> moves = p.getPossibleMoves(board);
                    System.out.print("Possible moves for " + p.type() + " at " + pos + ": ");
                    boolean found = false;
                    for (Position dest : moves) {
                        if (board.isValidMove(pos, dest)) {
                            System.out.print(dest + " ");
                            found = true;
                        }
                    }
                    if (!found) System.out.print("None");
                    System.out.println();
                }
            } catch (Exception e) {
                System.out.println("Invalid format. Use 'E2-E4' to move or 'E2' to see moves.");
            }
        }
    }

    private void makeComputerMove(Player computerPlayer) throws Exception {
        computerPlayer.updateOwnedPieces(board);
        List<ChessPair<Position, Piece>> pieces = computerPlayer.getOwnedPieces();
        Collections.shuffle(pieces);

        for (ChessPair<Position, Piece> pair : pieces) {
            Piece p = pair.getValue();
            List<Position> moves = p.getPossibleMoves(board);
            Collections.shuffle(moves);

            for (Position dest : moves) {
                if (board.isValidMove(p.getPosition(), dest)) {
                    Position start = p.getPosition();
                    Piece captured = board.getPieceAt(dest);

                    computerPlayer.makeMove(start, dest, board);
                    addMove(computerPlayer, start, dest, captured);

                    System.out.println("Computer moved: " + start + "-" + dest);
                    return;
                }
            }
        }
    }

    private void handleGameEnd(User user, Map<Integer, Game> map, Player current, int bonus, int winFlag) {
        int X = user.getPoints();
        int Y = 0;

        if (!current.getName().equalsIgnoreCase("Computer") && winFlag == 0) {
            Y = current.getPoints();
        } else if (winFlag == 1) {
            Player human = (player1.getName().equals("Computer")) ? player2 : player1;
            Y = human.getPoints();
        } else {
            Player human = (player1.getName().equals("Computer")) ? player2 : player1;
            Y = human.getPoints();
        }

        int finalPoints = X + Y + bonus;
        if (finalPoints < 0) finalPoints = 0;

        user.setPoints(finalPoints);

        String formula = "X(" + X + ") + Y(" + Y + ")";
        if (bonus > 0) formula += " + " + bonus;
        else formula += " - " + Math.abs(bonus);

        System.out.println("Points update: " + formula + " = " + finalPoints);
        System.out.println("Press Enter to return to menu...");
        scanner.nextLine();

        user.removeGame(this);
        map.remove(this.idPlayer);
    }

    public void printBoard() {
        System.out.println("   A  B  C  D  E  F  G  H");
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + "|");
            for (char col = 'A'; col <= 'H'; col++) {
                Piece p = board.getPieceAt(new Position(col, row));
                if (p == null) System.out.print(" ..");
                else System.out.print(" " + p.type() + (p.getColor() == Colors.WHITE ? "W" : "B"));
            }
            System.out.println("|" + row);
        }
    }

    private Position parsePosition(String s) {
        if (s == null || s.length() != 2) throw new IllegalArgumentException("Invalid coord");
        char col = s.toUpperCase().charAt(0);
        int row = Integer.parseInt(s.substring(1));
        return new Position(col, row);
    }

    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex == 0) ? 1 : 0;
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

    public void addMove(Player p, Position from, Position to, Piece captured) {
        moves.add(new Move(p.getColor(), from, to, captured));
    }

    public Player getCurrentPlayer() {
        return (currentPlayerIndex == 0) ? player1 : player2;
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
}