package com.satvik.sysd.entity;

public class File extends Node {

    private final StringBuilder content = new StringBuilder();

    public File(String name) {
        super(name);
    }

    public void write(String text) {
        content.append(text);
    }

    public String read() {
        return content.toString();
    }

    @Override
    public int size() {
        return content.length();
    }

    @Override
    public String toString() {
        return "File{" + "name='" + name + "'}";
    }
}