package com.satvik.sysd.interviewclaude;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class FileSystem {

    private final Directory root = new Directory("root");
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void mkdir(String path) {
        withWriteLock(() -> {
            String[] parts = parse(path);
            Directory parent = resolveParent(parts, path);
            String name = parts[parts.length - 1];
            if (parent.contains(name)) throw new FileSystemException.AlreadyExists(path);
            parent.add(new Directory(name));
        });
    }

    public void createFile(String path) {
        withWriteLock(() -> {
            String[] parts = parse(path);
            Directory parent = resolveParent(parts, path);
            String name = parts[parts.length - 1];
            if (parent.contains(name)) throw new FileSystemException.AlreadyExists(path);
            parent.add(new File(name));
        });
    }

    public String readFile(String path) {
        return withReadLock(() -> {
            Node node = resolve(parse(path), path);
            if (node instanceof Directory) throw new FileSystemException.IsDirectory(path);
            return ((File) node).read();
        });
    }

    public void writeFile(String path, String content) {
        withWriteLock(() -> {
            Node node = resolve(parse(path), path);
            if (node instanceof Directory) throw new FileSystemException.IsDirectory(path);
            ((File) node).write(content);
        });
    }

    public void appendToFile(String path, String content) {
        withWriteLock(() -> {
            Node node = resolve(parse(path), path);
            if (node instanceof Directory) throw new FileSystemException.IsDirectory(path);
            ((File) node).append(content);
        });
    }

    public List<String> list(String path) {
        return withReadLock(() -> {
            Node node = resolve(parse(path), path);
            if (!(node instanceof Directory dir)) throw new FileSystemException.NotADirectory(path);
            return dir.list().stream().map(Node::getName).sorted().toList();
        });
    }

    // Pass recursive=true to delete a non-empty directory.
    public void delete(String path, boolean recursive) {
        withWriteLock(() -> {
            String[] parts = parse(path);
            Directory parent = resolveParent(parts, path);
            String name = parts[parts.length - 1];
            Node node = parent.get(name);
            if (node == null) throw new FileSystemException.NotFound(path);
            if (node instanceof Directory dir && !dir.isEmpty() && !recursive) {
                throw new FileSystemException("Directory is not empty: " + path);
            }
            parent.remove(name);
        });
    }

    // ---- private helpers ----

    private void withWriteLock(Runnable action) {
        lock.writeLock().lock();
        try {
            action.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private <T> T withReadLock(Supplier<T> action) {
        lock.readLock().lock();
        try {
            return action.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    private String[] parse(String path) {
        if (path == null || !path.startsWith("/")) {
            throw new FileSystemException("Path must be absolute: " + path);
        }
        String[] parts = Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        if (parts.length == 0) {
            throw new FileSystemException("Cannot operate on root directly");
        }
        return parts;
    }

    private Node resolve(String[] parts, String originalPath) {
        Node current = root;
        for (String part : parts) {
            if (!(current instanceof Directory dir)) {
                throw new FileSystemException.NotADirectory(current.getName());
            }
            current = dir.get(part);
            if (current == null) throw new FileSystemException.NotFound(originalPath);
        }
        return current;
    }

    private Directory resolveParent(String[] parts, String originalPath) {
        // empty parentParts → resolve returns root, which is always a Directory
        String[] parentParts = Arrays.copyOf(parts, parts.length - 1);
        Node parent = resolve(parentParts, originalPath);
        if (!(parent instanceof Directory dir)) {
            throw new FileSystemException.NotADirectory(parent.getName());
        }
        return dir;
    }
}
