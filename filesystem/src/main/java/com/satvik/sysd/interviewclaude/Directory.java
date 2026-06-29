package com.satvik.sysd.interviewclaude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Directory extends Node {

    private final Map<String, Node> children = new HashMap<>();

    Directory(String name) {
        super(name);
    }

    void add(Node node) {
        children.put(node.getName(), node);
        node.setParent(this);
    }

    Node remove(String name) {
        Node removed = children.remove(name);
        if (removed != null) removed.setParent(null);
        return removed;
    }

    Node get(String name) { return children.get(name); }
    boolean contains(String name) { return children.containsKey(name); }
    boolean isEmpty() { return children.isEmpty(); }
    List<Node> list() { return new ArrayList<>(children.values()); }

    @Override
    long size() {
        return children.values().stream().mapToLong(Node::size).sum();
    }
}
