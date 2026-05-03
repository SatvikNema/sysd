package com.satvik.splitwise.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@EqualsAndHashCode(of = "value")
public class Node<V> {
    private V value;

    private Map<Node<V>, Double> edges;

    public Node(V value){
        this.value = value;
        edges = new ConcurrentHashMap<>();
    }

    public void addEdge(Node<V> toNode, double value){
        double existing = edges.getOrDefault(toNode, 0d);
        edges.put(toNode, existing + value);
    }

    @Override
    public String toString(){
        return value.toString();
    }
}
