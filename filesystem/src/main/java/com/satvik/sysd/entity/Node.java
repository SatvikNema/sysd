package com.satvik.sysd.entity;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public abstract class Node {

    private final UUID id = UUID.randomUUID();
    private final Instant createdAt = Instant.now();
    private String name;
    private Directory parent;
    private Instant modifiedAt = createdAt;

    protected Node(String name) {
        this.name = requireValidName(name);
    }

    public final UUID getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final Directory getParent() {
        return parent;
    }

    public final Instant getCreatedAt() {
        return createdAt;
    }

    public final Instant getModifiedAt() {
        return modifiedAt;
    }

    public final String path() {
        if (parent == null) {
            return "/";
        }

        Deque<String> components = new ArrayDeque<>();
        Node current = this;
        while (current.parent != null) {
            components.addFirst(current.name);
            current = current.parent;
        }
        return "/" + String.join("/", components);
    }

    public final void renameTo(String newName) {
        this.name = requireValidName(newName);
        markModified();
    }

    final void setParent(Directory parent) {
        this.parent = parent;
        markModified();
    }

    protected final void markModified() {
        modifiedAt = Instant.now();
    }

    public static String requireValidName(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Invalid node name: " + name);
        }
        return name;
    }

    public abstract long size();
}
