package com.satvik.sysd.entity;

import lombok.Getter;

@Getter
abstract class Node {

    protected String name;
    protected Directory parent;

    public Node(String name) {
        this.name = name;
    }

    void setParent(Directory parent) {
        this.parent = parent;
    }

    public abstract int size();
}
