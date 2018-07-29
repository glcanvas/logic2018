package utils;

public class Pair<T, U> {
    private T key;
    private U value;
    public Pair(T key,U value) {
        this.key =key;
        this.value = value;
    }
    public Pair() {
        this.key = null;
        this.value = null;
    }

    public T getKey() {
        return key;
    }

    public U getValue() {
        return value;
    }
}