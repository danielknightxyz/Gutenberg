package us.sourcefoundry.gutenberg.utils;

/**
 * Allows you to create a key value/pair.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
