package com.satvik.sysd.interviewclaude;

class File extends Node {

    private final StringBuilder content = new StringBuilder();

    File(String name) {
        super(name);
    }

    void write(String text) {
        content.setLength(0);
        content.append(text);
    }

    void append(String text) {
        content.append(text);
    }

    String read() {
        return content.toString();
    }

    @Override
    long size() {
        return content.length();
    }
}
