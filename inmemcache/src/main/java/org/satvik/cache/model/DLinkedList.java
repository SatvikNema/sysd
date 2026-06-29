package org.satvik.cache.model;

public class DLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;

    public Node<K, V> add(K key, V value){
        Node<K, V> node = new Node<>(key, value);
        if(head == null){
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.previous = node;
            head = node;
        }
        return node;
    }

    public void moveToHead(Node<K, V> node){
        if(node == head) return;
        if(node.next == null){
            Node<K, V> prev = node.previous;
            prev.next = null;
            tail = prev;

        } else {
            Node<K, V> next = node.next;
            Node<K, V> prev = node.previous;
            prev.next = next;
            next.previous = prev;
        }
        head.previous = node;
        node.next = head;
        head = node;
        node.previous = null;
    }

    public Node<K, V> removeTail(){
        Node<K, V> last = tail;
        if(tail.previous != null){
            tail = tail.previous;
            tail.next = null;
        }else {
            head = null;
            tail = null;
        }
        return last;
    }
}
