package com.satvik.splitwise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Graph<V> {
    private final List<Node<V>> nodes = new ArrayList<>();
    private final Map<V, Node<V>> nodesMap = new ConcurrentHashMap<>();

    public void addNode(Node<V> node){
        nodes.add(node);
        nodesMap.put(node.getValue(), node);
    }

    public void addEdge(V fromNodeValue, V toNodeValue, double value){
        Node<V> fromNode = nodesMap.get(fromNodeValue);
        Node<V> toNode = nodesMap.get(toNodeValue);
        if(fromNode == null || toNode == null){
            throw new IllegalStateException("Both nodes should be present in the graph");
        }
        fromNode.addEdge(toNode, value);
    }
}
