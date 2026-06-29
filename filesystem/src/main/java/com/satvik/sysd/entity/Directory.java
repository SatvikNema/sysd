package com.satvik.sysd.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Directory extends Node {

    private final Map<String, Node> children;

    public Directory(String name) {
        super(name);
        children = new HashMap<>();
    }

    public void add(Node node) {
        if (node.getParent() != null) {
            throw new IllegalStateException(node.path() + " is already attached to a directory");
        }
        if (node instanceof Directory) {
            for (Directory ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
                if (ancestor == node) {
                    throw new IllegalArgumentException("A directory cannot be moved into its descendant");
                }
            }
        }
        if (children.putIfAbsent(node.getName(), node) != null) {
            throw new IllegalArgumentException("A node named " + node.getName() + " already exists in " + path());
        }
        node.setParent(this);
        markModified();
    }

    public Node remove(String name) {
        Node removed = children.remove(name);
        if (removed != null) {
            removed.setParent(null);
            markModified();
        }
        return removed;
    }

    public Node get(String name) {
        return children.get(name);
    }

    public boolean contains(String name) {
        return children.containsKey(name);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public List<Node> list() {
        return new ArrayList<>(children.values());
    }

    @Override
    public long size() {
        long sum = 0;

        for(Node child : children.values())
            sum += child.size();

        return sum;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "name='" + getName() + '\'' +
                ", path='" + path() + '\'' +
                '}';
    }
}
