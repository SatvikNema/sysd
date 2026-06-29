package org.satvik.cache.model;


import lombok.Data;

@Data
public class Node<K, V> {
    private K key;
    private V val;
    public Node<K, V> next;
    public Node<K, V> previous;

    public Node(K k, V v){
        this.key = k;
        this.val = v;
    }

}
