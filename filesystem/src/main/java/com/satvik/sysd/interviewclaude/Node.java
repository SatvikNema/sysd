package com.satvik.sysd.interviewclaude;

abstract class Node {

    private String name;
    private Directory parent;

    Node(String name) {
        this.name = name;
    }

    String getName() { return name; }
    Directory getParent() { return parent; }
    void setParent(Directory parent) { this.parent = parent; }

    abstract long size();
}
