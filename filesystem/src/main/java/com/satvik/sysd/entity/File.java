package com.satvik.sysd.entity;

import java.nio.charset.StandardCharsets;

public class File extends Node {

    private final StringBuilder content = new StringBuilder();

    public File(String name) {
        super(name);
    }

    public void append(String text) {
        content.append(text);
        markModified();
    }

    public void overwrite(String text) {
        content.setLength(0);
        content.append(text);
        markModified();
    }

    public String read() {
        return content.toString();
    }

    @Override
    public long size() {
        return content.toString().getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public String toString() {
        return "File{" + "name='" + getName() + "'}";
    }
}
