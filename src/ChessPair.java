import java.util.*;

public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    private K key;
    private V value;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int compareTo(ChessPair<K, V> otherPair) {
        return this.key.compareTo(otherPair.key);
    }

    public String toString() {
        return key.toString() + " - " + value.toString();
    }
}
