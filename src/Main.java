import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import javax.swing.SwingUtilities;

public class Main {
    //SINGLETON - pastreaza singura copie a obiectului main
    private static Main instance = null;
    private List<User> users;
    private Map<Integer, Game> gamesMap;
    private User currentUser;
    private final String ACCOUNTS_FILE = "../input/accounts.json";
    private final String GAMES_FILE = "../input/games.json";
    private Scanner scanner;

    //SINGLETON - constructorul este privat, deci alte clase nu mai pot
    //crea alte obiecte main
    private Main() {
        this.users = new ArrayList<>();
        this.gamesMap = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    //metoda prin care restul programului interactioneaza cu main-ul
    //tine tot de SINGLETON
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public static void main(String[] args) {
        Main app = Main.getInstance();
        app.read();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WindowManager.getInstance().setVisible(true);
            }
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
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
                        String type = (String) pieceJson.get("type");
                        Colors color = Colors.valueOf((String) pieceJson.get("color"));
                        Position pos = null;
                        if (pieceJson.containsKey("position")) {
                            pos = parsePosition((String) pieceJson.get("position"));
                        }
                        //FACTORY
                        //programul citeste tipul piesei din fisierele json
                        //leaga responsabilitatea crearii obiectului catre fabrica
                        //altfel s-ar fi folosit multiple if-else-uri
                        Piece p = PieceFactory.createPiece(type, color, pos);
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
                            if(mo.containsKey("captured")) {
                                JSONObject capJson = (JSONObject)mo.get("captured");
                                String cType = (String) capJson.get("type");
                                Colors cColor = Colors.valueOf((String) capJson.get("color"));
                                Position cPos = null;
                                if (capJson.containsKey("position")) {
                                    cPos = parsePosition((String) capJson.get("position"));
                                }
                                //FACTORY
                                //cand se incarca istoricul mutarilor programul trebuie
                                //sa refaca obiectele capturate
                                //fabrica este apelata din nou pentru a recrea piesa respectiva
                                cap = PieceFactory.createPiece(cType, cColor, cPos);
                            }
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
        } catch (Exception e) { }
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
        } catch (Exception e) { }
    }

    public void write() {
        JSONArray gamesList = new JSONArray();
        for (Game g : gamesMap.values()) {
            JSONObject gameObj = new JSONObject();
            gameObj.put("id", g.getId());
            Player p1 = g.getPlayer1();
            Player p2 = g.getPlayer2();
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
        try (FileWriter file = new FileWriter(GAMES_FILE)) { file.write(gamesList.toJSONString()); } catch (IOException e) { }
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
        try (FileWriter file = new FileWriter(ACCOUNTS_FILE)) { file.write(usersList.toJSONString()); } catch (IOException e) { }
    }

    public void addGame(Game game) {
        if (game != null) gamesMap.put(game.getId(), game);
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

    private Position parsePosition(String s) {
        char col = s.toUpperCase().charAt(0);
        int row = Integer.parseInt(s.substring(1));
        return new Position(col, row);
    }

    public Map<Integer, Game> getGamesMap() { return this.gamesMap; }
}