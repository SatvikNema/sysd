package com.satvik.sysd.entity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Directory extends Node {

    private final Map<String, Node> children;

    public Directory(String name) {
        super(name);
        children = new ConcurrentHashMap<>();
    }

    public void add(Node node) {
        children.put(node.getName(), node);
        node.setParent(this);
    }

    public Node remove(String name) {
        return children.remove(name);
    }

    public Node get(String name) {
        return children.get(name);
    }

    public Collection<Node> list() {
        return children.values();
    }

    @Override
    public int size() {
        int sum = 0;

        for(Node child : children.values())
            sum += child.size();

        return sum;
    }
}
