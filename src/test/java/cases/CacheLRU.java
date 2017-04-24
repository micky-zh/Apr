package cases;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengfan on 2017/1/23 0023.
 * https://leetcode.com/problems/lru-cache/
 */
public class CacheLRU<K, V> {

    private int capacity;

    private Map<K, Node<K, V>> cache = new LinkedHashMap<>();

    private class Node<K, V> {
        public K key;
        public V value;

        private Node prv;
        private Node next;
    }

    public CacheLRU(int capacity) {
        this.capacity = this.capacity;

    }

    public int get(int key) {
        Node current = cache.get(key);
        if (current == null) {
            return -1;
        }


        return (int) current.value;
    }

    public void put(int key, int value) {

    }
}


