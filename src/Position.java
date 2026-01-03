import java.util.*;

public class Position implements Comparable<Position>{
    private char x;
    private int y;

    public Position(char x, int y){
        this.x = x;
        this.y = y;
    }

    public  char getX() {
        return x;
    }

    public void setX(char x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Position)){
            return false;
        }

        Position pos = (Position) o;
        return this.x == pos.x && this.y == pos.y;
    }

    public int compareTo(Position p) {
        if(this.y != p.y) {
            return this.y - p.y;
        }

        return this.x - p.x;
    }

    public String toString() {
        return "" + x + y;
    }
}