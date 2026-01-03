import java.util.*;

public class User {
    private String email;
    private String password;
    private List<Game> games;
    private int totalPoints;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.games = new ArrayList<>();
        this.totalPoints = 0;
    }

    public void addGame(Game game) {
        games.add(game);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    public List<Game> getActiveGames() {
        return games;
    }

    public int getPoints() {
        return totalPoints;
    }

    public void setPoints(int points) {
        this.totalPoints = points;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}