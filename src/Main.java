import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Main {
    private List<User> users;
    private Map<Integer, Game> gamesMap;
    private User currentUser;
    private final String ACCOUNTS_FILE = "../input/accounts.json";
    private final String GAMES_FILE = "../input/games.json";
    private Scanner scanner;

    public Main() {
        this.users = new ArrayList<>();
        this.gamesMap = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.read();
        app.run();
    }

    public void run() {
        boolean running = true;
        System.out.println("=== CHESS GAME ===");

        while (running) {
            if (currentUser == null) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Login");
                System.out.println("2. New Account");
                System.out.println("3. Exit");
                System.out.print("Option: ");

                if (scanner.hasNextLine()) {
                    String option = scanner.nextLine();
                    if (option.equals("1")) {
                        performLogin();
                    } else if (option.equals("2")) {
                        performRegister();
                    } else if (option.equals("3")) {
                        running = false;
                        write();
                    } else {
                        System.out.println("Invalid command. Please enter 1, 2, or 3.");
                    }
                } else {
                    running = false;
                }
            } else {
                System.out.println("\nWelcome, " + currentUser.getEmail() + " | Points: " + currentUser.getPoints());
                System.out.println("1. Start New Game (Player vs Computer)");
                System.out.println("2. View/Resume Games");
                System.out.println("3. Logout");
                System.out.print("Option: ");

                if (scanner.hasNextLine()) {
                    String option = scanner.nextLine();
                    if (option.equals("1")) {
                        startNewGame();
                    } else if (option.equals("2")) {
                        resumeExistingGame();
                    } else if (option.equals("3")) {
                        currentUser = null;
                    } else {
                        System.out.println("Invalid command. Please enter 1, 2, or 3.");
                    }
                } else {
                    running = false;
                }
            }
        }
    }

    private void performLogin() {
        boolean ok = false;

        while (ok == false) {
            System.out.println("\n--- LOGIN (Type 'exit' to go back) ---");
            System.out.print("Email: ");
            String email = scanner.nextLine();

            if (email.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.print("Password: ");
            String pass = scanner.nextLine();

            User userGasit = login(email, pass);

            if (userGasit != null) {
                currentUser = userGasit;
                ok = true;
                System.out.println("Login successful.");
            } else {
                System.out.println("Login failed. Check email/password and try again.");
            }
        }
    }

    private void performRegister() {
        System.out.println("\n--- NEW ACCOUNT ---");
        System.out.print("Email: ");
        String adresaEmail = scanner.nextLine();
        System.out.print("Password: ");
        String parola = scanner.nextLine();

        User userNou = newAccount(adresaEmail, parola);
        if (userNou != null) {
            currentUser = userNou;
            System.out.println("Account created successfully. You are now logged in.");
        } else {
            System.out.println("Registration failed. User likely already exists.");
        }
    }

    private void startNewGame() {
        System.out.println("\n--- START NEW GAME ---");
        System.out.print("Player Alias: ");

        String numeJucator = scanner.nextLine();

        String culoareAleasaText = "";
        boolean amGasitCuloare = false;

        while (amGasitCuloare == false) {
            System.out.print("Choose Color (WHITE/BLACK): ");

            if (scanner.hasNextLine()) {
                String textCitit = scanner.nextLine();
                culoareAleasaText = textCitit.toUpperCase();

                if (culoareAleasaText.equals("WHITE")) {
                    amGasitCuloare = true;
                } else {
                    if (culoareAleasaText.equals("BLACK")) {
                        amGasitCuloare = true;
                    } else {
                        System.out.println("Invalid color. Please type 'WHITE' or 'BLACK'.");
                    }
                }
            } else {
                return;
            }
        }

        Colors culoareJucator;
        Colors culoareCalculator;

        if (culoareAleasaText.equals("BLACK")) {
            culoareJucator = Colors.BLACK;
            culoareCalculator = Colors.WHITE;
        } else {
            culoareJucator = Colors.WHITE;
            culoareCalculator = Colors.BLACK;
        }

        Player jucatorUman = new Player(numeJucator, culoareJucator);
        Player jucatorCalculator = new Player("Computer", culoareCalculator);

        int idJocNou = gamesMap.size() + 1;
        Game jocNou = new Game(idJocNou, jucatorUman, jucatorCalculator);

        jocNou.getBoard().initialize();

        gamesMap.put(idJocNou, jocNou);
        currentUser.addGame(jocNou);

        playGame(jocNou);
    }

    private void resumeExistingGame() {
        if (currentUser.getActiveGames().isEmpty()) {
            System.out.println("No active games found.");
            return;
        }

        System.out.println("\n--- YOUR GAMES ---");
        for (Game g : currentUser.getActiveGames()) {
            System.out.println("ID: " + g.getId() + " | VS Computer");
        }

        System.out.print("Enter Game ID to select: ");

        try {
            String input = scanner.nextLine();
            int id = Integer.parseInt(input);
            Game g = gamesMap.get(id);

            if (g != null && currentUser.getActiveGames().contains(g)) {
                boolean managingGame = true;
                while (managingGame) {
                    System.out.println("\n--- GAME OPTIONS (ID: " + id + ") ---");
                    System.out.println("1. View Details");
                    System.out.println("2. Continue Game (Resume)");
                    System.out.println("3. Delete Game");
                    System.out.println("4. Back to Menu");
                    System.out.print("Choose: ");

                    if (scanner.hasNextLine()) {
                        String choice = scanner.nextLine();
                        if (choice.equals("1")) {
                            viewGameDetails(g);
                        } else if (choice.equals("2")) {
                            playGame(g);
                            managingGame = false;
                        } else if (choice.equals("3")) {
                            currentUser.removeGame(g);
                            gamesMap.remove(id);
                            System.out.println("Game deleted successfully.");
                            managingGame = false;
                        } else if (choice.equals("4")) {
                            managingGame = false;
                        } else {
                            System.out.println("Invalid option.");
                        }
                    } else {
                        managingGame = false;
                    }
                }
            } else {
                System.out.println("Game not found or not yours.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    private void viewGameDetails(Game jocDeVizualizat) {
        System.out.println("\n=== GAME DETAILS ===");

        Player primulJucator = jocDeVizualizat.getCurrentPlayer();
        jocDeVizualizat.switchPlayer();
        Player alDoileaJucator = jocDeVizualizat.getCurrentPlayer();
        jocDeVizualizat.switchPlayer();

        System.out.println("Match: " + primulJucator.getName() + " vs " + alDoileaJucator.getName());
        System.out.println("Current Turn: " + jocDeVizualizat.getCurrentPlayer().getName() + " (" + jocDeVizualizat.getCurrentPlayer().getColor() + ")");

        System.out.println("\n--- Current Board Position ---");
        printBoard(jocDeVizualizat.getBoard());

        System.out.println("\n--- Move History ---");
        List<Move> istoricMutari = jocDeVizualizat.getMoves();
        boolean esteListaGoala = istoricMutari.isEmpty();

        if (esteListaGoala == true) {
            System.out.println("No moves played yet.");
        } else {
            int numarCurent = 1;
            for (Move mutare : istoricMutari) {
                String textDespreCaptura = "";
                Piece piesaCapturata = mutare.getCapturedPiece();

                if (piesaCapturata != null) {
                    textDespreCaptura = " (Captured " + piesaCapturata.type() + ")";
                } else {
                    textDespreCaptura = "";
                }

                System.out.println(numarCurent + ". " + mutare.getPlayerColor() + ": " + mutare.getStartPosition() + " -> " + mutare.getEndPosition() + textDespreCaptura);
                numarCurent = numarCurent + 1;
            }
        }

        System.out.println("\nPress Enter to return...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private void playGame(Game game) {
        boolean active = true;
        System.out.println("\n--- GAME STARTED/RESUMED ---");
        System.out.println("Commands: 'exit' (Save), 'resign' (Give Up), 'E2-E4' (Move) or 'E2' (Check moves)");

        while (active) {
            printBoard(game.getBoard());
            Player current = game.getCurrentPlayer();
            System.out.println("Turn: " + current.getName() + " (" + current.getColor() + ")");

            if (game.checkDraw()) {
                System.out.println("DRAW DETECTED! Computer resigns.");

                int X = currentUser.getPoints();
                int Y;

                if (!current.getName().equalsIgnoreCase("Computer")) {
                    Y = current.getPoints();
                } else {
                    game.switchPlayer();
                    Y = game.getCurrentPlayer().getPoints();
                    game.switchPlayer();
                }

                int X_nou = X + Y + 150;
                currentUser.setPoints(X_nou);

                System.out.println("Game Over. Formula: X(" + X + ") + Y(" + Y + ") + 150 = " + X_nou);

                currentUser.removeGame(game);
                gamesMap.remove(game.getId());
                active = false;
                break;
            }

            if (game.checkForCheckMate()) {
                System.out.println("CHECKMATE! Game Over.");

                int X = currentUser.getPoints();
                int Y;

                boolean humanLost = !current.getName().equalsIgnoreCase("Computer");

                if (humanLost) {
                    Y = current.getPoints();
                } else {
                    game.switchPlayer();
                    Y = game.getCurrentPlayer().getPoints();
                    game.switchPlayer();
                }

                if (humanLost) {
                    int X_nou = X + Y - 300;
                    if (X_nou < 0) X_nou = 0;
                    currentUser.setPoints(X_nou);
                    System.out.println("You LOST! Formula: X(" + X + ") + Y(" + Y + ") - 300 = " + X_nou);
                } else {
                    int X_nou = X + Y + 300;
                    currentUser.setPoints(X_nou);
                    System.out.println("You WON! Formula: X(" + X + ") + Y(" + Y + ") + 300 = " + X_nou);
                }

                System.out.println("Total Points: " + currentUser.getPoints());
                System.out.println("Press Enter to finish...");
                if (scanner.hasNextLine()) scanner.nextLine();

                currentUser.removeGame(game);
                gamesMap.remove(game.getId());
                active = false;
                break;
            }

            if (!current.getName().equalsIgnoreCase("Computer")) {
                System.out.print("Action: ");
                if (!scanner.hasNextLine()) {
                    active = false;
                    break;
                }
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Game state saved. Returning to menu.");
                    active = false;
                    break;
                }

                if (input.equalsIgnoreCase("resign")) {
                    int puncteVechi = currentUser.getPoints();
                    int puncteMeci = current.getPoints();

                    int scorCalculat = puncteVechi + puncteMeci - 150;
                    if (scorCalculat < 0) {
                        scorCalculat = 0;
                    }

                    currentUser.setPoints(scorCalculat);

                    System.out.println("You resigned.");
                    System.out.println("Points update: X(" + puncteVechi + ") + Y(" + puncteMeci + ") - 150 = " + scorCalculat);

                    currentUser.removeGame(game);
                    gamesMap.remove(game.getId());
                    active = false;
                    break;
                }

                if (input.contains("-")) {
                    try {
                        String[] parts = input.split("-");
                        if (parts.length != 2) throw new Exception("Invalid format.");
                        Position from = parsePosition(parts[0]);
                        Position to = parsePosition(parts[1]);

                        Piece captured = game.getBoard().getPieceAt(to);
                        current.makeMove(from, to, game.getBoard());
                        game.addMove(current, from, to, captured);

                        Colors enemyColor = (current.getColor() == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
                        if (game.getBoard().isKingInCheck(enemyColor)) {
                            System.out.println("CHECK!");
                        }

                        game.switchPlayer();

                    } catch (Exception e) {
                        System.out.println("Invalid move: " + e.getMessage());
                    }
                } else {
                    try {
                        Position pos = parsePosition(input);
                        Piece p = game.getBoard().getPieceAt(pos);

                        if (p == null) {
                            System.out.println("No piece at " + pos);
                        } else if (p.getColor() != current.getColor()) {
                            System.out.println("That is not your piece!");
                        } else {
                            List<Position> moves = p.getPossibleMoves(game.getBoard());
                            System.out.print("Possible moves for " + p.type() + " at " + pos + ": ");

                            boolean found = false;
                            for (Position dest : moves) {
                                if (game.getBoard().isValidMove(pos, dest)) {
                                    System.out.print(dest + " ");
                                    found = true;
                                }
                            }
                            if (!found) System.out.print("None");
                            System.out.println();
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                        System.out.println("Usage examples: 'E2-E4' to move, 'E2' to see options, 'exit' or 'resign'.");
                    }
                }
            } else {
                System.out.println("Computer is thinking...");
                try {
                    Thread.sleep(800);
                    makeComputerMove(current, game);

                    Colors enemyColor = (current.getColor() == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
                    if (game.getBoard().isKingInCheck(enemyColor)) {
                        System.out.println("CHECK!");
                    }

                    game.switchPlayer();
                } catch (Exception e) {
                    System.out.println("Computer error: " + e.getMessage());
                    active = false;
                }
            }
        }
    }

    private void makeComputerMove(Player computerMove, Game game) throws Exception {
        computerMove.updateOwnedPieces(game.getBoard());
        List<ChessPair<Position, Piece>> listaPieseCalculator = computerMove.getOwnedPieces();
        Collections.shuffle(listaPieseCalculator);

        for (ChessPair<Position, Piece> pereche : listaPieseCalculator) {
            Piece piesaCurenta = pereche.getValue();
            List<Position> listaMutariPosibile = piesaCurenta.getPossibleMoves(game.getBoard());
            Collections.shuffle(listaMutariPosibile);

            for (Position pozitieDestinatie : listaMutariPosibile) {
                Position pozitieStart = piesaCurenta.getPosition();
                boolean esteMutareValida = game.getBoard().isValidMove(pozitieStart, pozitieDestinatie);

                if (esteMutareValida == true) {
                    Piece piesaCapturata = game.getBoard().getPieceAt(pozitieDestinatie);

                    computerMove.makeMove(pozitieStart, pozitieDestinatie, game.getBoard());
                    game.addMove(computerMove, pozitieStart, pozitieDestinatie, piesaCapturata);

                    System.out.println("Computer moved: " + pozitieStart + "-" + pozitieDestinatie);
                    return;
                }
            }
        }
    }

    public static void testScenariuRemiza() {
        System.out.println("\n--- TESTARE REMIZA (SIMULARE START) ---");
        Player p1 = new Player("AlbTest", Colors.WHITE);
        Player p2 = new Player("NegruTest", Colors.BLACK);
        Game game = new Game(999, p1, p2);
        game.getBoard().initialize();

        Position albStart = new Position('A', 2);
        Position albEnd   = new Position('A', 3);
        Position negruStart = new Position('H', 7);
        Position negruEnd   = new Position('H', 6);

        for (int i = 1; i <= 3; i++) {
            game.addMove(p1, albStart, albEnd, null);
            game.addMove(p2, negruStart, negruEnd, null);
            game.addMove(p1, albEnd, albStart, null);
            game.addMove(p2, negruEnd, negruStart, null);
        }

        if (game.checkDraw()) {
            System.out.println("Remiza detectata corect.");
        } else {
            System.out.println("Nu a detectat remiza.");
        }
    }

    public void read() {
        JSONParser parser = new JSONParser();

        try {
            File gFile = new File(GAMES_FILE);
            if (gFile.exists()) {
                FileReader reader = new FileReader(gFile);
                JSONArray gamesArray = (JSONArray) parser.parse(reader);

                for (Object g : gamesArray) {
                    JSONObject gameJson = (JSONObject) g;
                    int id = ((Long) gameJson.get("id")).intValue();

                    JSONArray playersJson = (JSONArray) gameJson.get("players");
                    JSONObject p1Json = (JSONObject) playersJson.get(0);
                    JSONObject p2Json = (JSONObject) playersJson.get(1);

                    Player p1 = new Player((String) p1Json.get("email"), Colors.valueOf((String) p1Json.get("color")));
                    Player p2 = new Player((String) p2Json.get("email"), Colors.valueOf((String) p2Json.get("color")));

                    Game game = new Game(id, p1, p2);
                    game.getBoard().getPieces().clear();

                    JSONArray boardJson = (JSONArray) gameJson.get("board");
                    for (Object pieceObj : boardJson) {
                        JSONObject pieceJson = (JSONObject) pieceObj;
                        Piece p = createPieceFromJSON(pieceJson);
                        if (p != null) game.getBoard().addPiece(p);
                    }

                    if (gameJson.get("moves") != null) {
                        JSONArray movesJson = (JSONArray) gameJson.get("moves");
                        for(Object m : movesJson) {
                            JSONObject mo = (JSONObject) m;
                            Position f = parsePosition((String)mo.get("from"));
                            Position t = parsePosition((String)mo.get("to"));
                            String pc = (String)mo.get("playerColor");
                            Piece cap = null;
                            if(mo.containsKey("captured")) cap = createPieceFromJSON((JSONObject)mo.get("captured"));

                            Player mover = (p1.getColor().toString().equals(pc)) ? p1 : p2;
                            game.addMove(mover, f, t, cap);
                        }
                    }

                    String currentHex = (String) gameJson.get("currentPlayerColor");
                    if (!game.getCurrentPlayer().getColor().toString().equals(currentHex)) {
                        game.switchPlayer();
                    }

                    gamesMap.put(id, game);
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("Note: No saved games loaded (" + e.getMessage() + ")");
        }

        try {
            File aFile = new File(ACCOUNTS_FILE);
            if (aFile.exists()) {
                FileReader reader = new FileReader(aFile);
                JSONArray accountsArray = (JSONArray) parser.parse(reader);

                for (Object u : accountsArray) {
                    JSONObject userJson = (JSONObject) u;
                    String email = (String) userJson.get("email");
                    String pass = (String) userJson.get("password");
                    int points = ((Long) userJson.get("points")).intValue();

                    User user = new User(email, pass);
                    user.setPoints(points);

                    JSONArray gamesIds = (JSONArray) userJson.get("games");
                    for (Object idObj : gamesIds) {
                        int gameId = ((Long) idObj).intValue();
                        Game g = gamesMap.get(gameId);
                        if (g != null) user.addGame(g);
                    }
                    users.add(user);
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("Note: No accounts loaded (" + e.getMessage() + ")");
        }
    }

    public void write() {
        JSONArray gamesList = new JSONArray();
        for (Game g : gamesMap.values()) {
            JSONObject gameObj = new JSONObject();
            gameObj.put("id", g.getId());

            Player p1 = g.getCurrentPlayer();
            g.switchPlayer();
            Player p2 = g.getCurrentPlayer();
            g.switchPlayer();

            JSONArray playersArr = new JSONArray();
            playersArr.add(playerToJSON(p1));
            playersArr.add(playerToJSON(p2));
            gameObj.put("players", playersArr);

            gameObj.put("currentPlayerColor", g.getCurrentPlayer().getColor().toString());

            JSONArray boardArr = new JSONArray();
            for (ChessPair<Position, Piece> pair : g.getBoard().getPieces()) {
                boardArr.add(pieceToJSON(pair.getValue()));
            }
            gameObj.put("board", boardArr);

            JSONArray movesArr = new JSONArray();
            for(Move m : g.getMoves()) {
                JSONObject mo = new JSONObject();
                mo.put("playerColor", m.getPlayerColor().toString());
                mo.put("from", m.getStartPosition().toString());
                mo.put("to", m.getEndPosition().toString());
                if(m.getCapturedPiece() != null) mo.put("captured", pieceToJSON(m.getCapturedPiece()));
                movesArr.add(mo);
            }
            gameObj.put("moves", movesArr);

            gamesList.add(gameObj);
        }

        try (FileWriter file = new FileWriter(GAMES_FILE)) {
            file.write(gamesList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray usersList = new JSONArray();
        for (User u : users) {
            JSONObject userObj = new JSONObject();
            userObj.put("email", u.getEmail());
            userObj.put("password", u.getPassword());
            userObj.put("points", u.getPoints());

            JSONArray gIds = new JSONArray();
            for (Game g : u.getActiveGames()) {
                gIds.add(g.getId());
            }
            userObj.put("games", gIds);
            usersList.add(userObj);
        }

        try (FileWriter file = new FileWriter(ACCOUNTS_FILE)) {
            file.write(usersList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User newAccount(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equals(email)) return null;
        }
        User newUser = new User(email, password);
        users.add(newUser);
        return newUser;
    }

    private Piece createPieceFromJSON(JSONObject json) {
        if (json == null) return null;
        String type = (String) json.get("type");
        String colorStr = (String) json.get("color");
        Colors color = Colors.valueOf(colorStr);
        Position pos = null;
        if (json.containsKey("position")) pos = parsePosition((String) json.get("position"));

        if (type.equals("P")) {
            return new Pawn(color, pos);
        } else if (type.equals("R")) {
            return new Rook(color, pos);
        } else if (type.equals("N")) {
            return new Knight(color, pos);
        } else if (type.equals("B")) {
            return new Bishop(color, pos);
        } else if (type.equals("Q")) {
            return new Queen(color, pos);
        } else if (type.equals("K")) {
            return new King(color, pos);
        } else {
            return null;
        }
    }

    private JSONObject pieceToJSON(Piece p) {
        JSONObject obj = new JSONObject();
        obj.put("type", String.valueOf(p.type()));
        obj.put("color", p.getColor().toString());
        if (p.getPosition() != null) obj.put("position", p.getPosition().toString());
        return obj;
    }

    private JSONObject playerToJSON(Player p) {
        JSONObject obj = new JSONObject();
        obj.put("email", p.getName());
        obj.put("color", p.getColor().toString());
        return obj;
    }

    private Position parsePosition(String s) throws IllegalArgumentException {
        if (s == null || s.length() != 2) {
            throw new IllegalArgumentException("Invalid coordinate format. Expected 2 chars (e.g. 'E2').");
        }
        char col = s.toUpperCase().charAt(0);
        char rowChar = s.charAt(1);

        if (col < 'A' || col > 'H') {
            throw new IllegalArgumentException("Column must be between A and H.");
        }
        if (rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Row must be between 1 and 8.");
        }

        return new Position(col, Integer.parseInt(String.valueOf(rowChar)));
    }

    public static void printBoard(Board board) {
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
}