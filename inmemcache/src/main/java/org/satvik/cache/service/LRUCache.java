package org.satvik.cache.service;

import org.satvik.cache.model.DLinkedList;
import org.satvik.cache.model.Node;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private final Map<K, Node<K, V>> map;
    private final DLinkedList<K, V> list;
    private final int size;

    public LRUCache(int size){
        map = new HashMap<>();
        list = new DLinkedList<>();
        this.size = size;
    }

    public void put(K key, V value){
        if(map.containsKey(key)){
            Node<K, V> node = map.get(key);
            node.setVal(value);
            list.moveToHead(node);
        } else {
            if(map.size() == size){
                Node<K, V> removed = list.removeTail();
                map.remove(removed.getKey());
            }
            Node<K, V> node = list.add(key, value);
            map.put(key, node);
        }
    }

    public V get(K key){
        V result = null;
        if(map.containsKey(key)){
            Node<K, V> node = map.get(key);
            result = node.getVal();
            list.moveToHead(node);
        }
        return result;
    }
}
