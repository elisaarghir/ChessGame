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
                        System.out.println("Invalid command.");
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
                        System.out.println("Invalid command.");
                    }
                } else {
                    running = false;
                }
            }
        }
    }

    private void performLogin() {
        boolean ok = false;
        while (!ok) {
            System.out.println("\n--- LOGIN (Type 'exit' to go back) ---");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (email.equalsIgnoreCase("exit")) break;

            System.out.print("Password: ");
            String pass = scanner.nextLine();

            User userGasit = login(email, pass);
            if (userGasit != null) {
                currentUser = userGasit;
                ok = true;
                System.out.println("Login successful.");
            } else {
                System.out.println("Login failed.");
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
            System.out.println("Account created successfully.");
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
        while (!amGasitCuloare) {
            System.out.print("Choose Color (WHITE/BLACK): ");
            if (scanner.hasNextLine()) {
                culoareAleasaText = scanner.nextLine().toUpperCase();
                if (culoareAleasaText.equals("WHITE") || culoareAleasaText.equals("BLACK")) {
                    amGasitCuloare = true;
                } else {
                    System.out.println("Invalid color.");
                }
            } else return;
        }

        Colors culoareJucator = (culoareAleasaText.equals("BLACK")) ? Colors.BLACK : Colors.WHITE;
        Colors culoareCalculator = (culoareJucator == Colors.BLACK) ? Colors.WHITE : Colors.BLACK;

        Player jucatorUman = new Player(numeJucator, culoareJucator);
        Player jucatorCalculator = new Player("Computer", culoareCalculator);

        int maxId = 0;
        for (Integer id : gamesMap.keySet()) {
            if (id > maxId) maxId = id;
        }
        int idJocNou = maxId + 1;

        Game jocNou = new Game(idJocNou, jucatorUman, jucatorCalculator);
        gamesMap.put(idJocNou, jocNou);
        currentUser.addGame(jocNou);

        jocNou.start(currentUser, gamesMap);
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
                            g.resume(currentUser, gamesMap);
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
                    } else managingGame = false;
                }
            } else {
                System.out.println("Game not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void viewGameDetails(Game jocDeVizualizat) {
        System.out.println("\n=== GAME DETAILS ===");

        Player curent = jocDeVizualizat.getCurrentPlayer();
        jocDeVizualizat.switchPlayer();
        Player adversar = jocDeVizualizat.getCurrentPlayer();
        jocDeVizualizat.switchPlayer();

        System.out.println("Match: " + curent.getName() + " vs " + adversar.getName());
        System.out.println("Current Turn: " + curent.getName());

        System.out.println("\n--- Current Board Position ---");
        jocDeVizualizat.printBoard();

        System.out.println("\n--- Move History ---");
        List<Move> istoricMutari = jocDeVizualizat.getMoves();
        if (istoricMutari.isEmpty()) {
            System.out.println("No moves played yet.");
        } else {
            int i = 1;
            for (Move m : istoricMutari) {
                String cap = (m.getCapturedPiece() != null) ? " (Captured " + m.getCapturedPiece().type() + ")" : "";
                System.out.println(i++ + ". " + m.getPlayerColor() + ": " + m.getStartPosition() + " -> " + m.getEndPosition() + cap);
            }
        }
        System.out.println("\nPress Enter to return...");
        scanner.nextLine();
    }

    private Position parsePosition(String s) {
        if (s == null || s.length() != 2) throw new IllegalArgumentException("Invalid format");
        char col = s.toUpperCase().charAt(0);
        int row = Integer.parseInt(s.substring(1));
        return new Position(col, row);
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
        } catch (Exception e) { System.out.println("No saved games loaded."); }
        try {
            File aFile = new File(ACCOUNTS_FILE);
            if (aFile.exists()) {
                FileReader reader = new FileReader(aFile);
                JSONArray accountsArray = (JSONArray) parser.parse(reader);
                for (Object u : accountsArray) {
                    JSONObject userJson = (JSONObject) u;
                    User user = new User((String)userJson.get("email"), (String)userJson.get("password"));
                    user.setPoints(((Long) userJson.get("points")).intValue());
                    JSONArray gamesIds = (JSONArray) userJson.get("games");
                    for (Object idObj : gamesIds) {
                        Game g = gamesMap.get(((Long) idObj).intValue());
                        if (g != null) user.addGame(g);
                    }
                    users.add(user);
                }
                reader.close();
            }
        } catch (Exception e) { System.out.println("No accounts loaded."); }
    }

    public void write() {
        JSONArray gamesList = new JSONArray();
        for (Game g : gamesMap.values()) {
            JSONObject gameObj = new JSONObject();
            gameObj.put("id", g.getId());
            Player p1 = g.getCurrentPlayer(); g.switchPlayer();
            Player p2 = g.getCurrentPlayer(); g.switchPlayer();
            JSONArray playersArr = new JSONArray();
            playersArr.add(playerToJSON(p1));
            playersArr.add(playerToJSON(p2));
            gameObj.put("players", playersArr);
            gameObj.put("currentPlayerColor", g.getCurrentPlayer().getColor().toString());
            JSONArray boardArr = new JSONArray();
            for (ChessPair<Position, Piece> pair : g.getBoard().getPieces()) boardArr.add(pieceToJSON(pair.getValue()));
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
        try (FileWriter file = new FileWriter(GAMES_FILE)) { file.write(gamesList.toJSONString()); } catch (IOException e) { e.printStackTrace(); }
        JSONArray usersList = new JSONArray();
        for (User u : users) {
            JSONObject userObj = new JSONObject();
            userObj.put("email", u.getEmail());
            userObj.put("password", u.getPassword());
            userObj.put("points", u.getPoints());
            JSONArray gIds = new JSONArray();
            for (Game g : u.getActiveGames()) gIds.add(g.getId());
            userObj.put("games", gIds);
            usersList.add(userObj);
        }
        try (FileWriter file = new FileWriter(ACCOUNTS_FILE)) { file.write(usersList.toJSONString()); } catch (IOException e) { e.printStackTrace(); }
    }

    public User login(String email, String password) {
        for (User u : users) if (u.getEmail().equals(email) && u.getPassword().equals(password)) return u;
        return null;
    }
    public User newAccount(String email, String password) {
        for (User u : users) if (u.getEmail().equals(email)) return null;
        User newUser = new User(email, password);
        users.add(newUser);
        return newUser;
    }
    private Piece createPieceFromJSON(JSONObject json) {
        if (json == null) return null;
        String type = (String) json.get("type");
        Colors color = Colors.valueOf((String) json.get("color"));
        Position pos = (json.containsKey("position")) ? parsePosition((String) json.get("position")) : null;
        if (type.equals("P")) return new Pawn(color, pos);
        if (type.equals("R")) return new Rook(color, pos);
        if (type.equals("N")) return new Knight(color, pos);
        if (type.equals("B")) return new Bishop(color, pos);
        if (type.equals("Q")) return new Queen(color, pos);
        if (type.equals("K")) return new King(color, pos);
        return null;
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
}