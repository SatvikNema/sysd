package com.satvik.sysd.service;

import com.satvik.sysd.entity.Directory;
import com.satvik.sysd.entity.File;
import com.satvik.sysd.entity.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static com.satvik.sysd.service.FileSystemException.*;

/**
 * A thread-safe, in-memory hierarchical filesystem.
 *
 * <p>All public operations are atomic. Paths must be absolute; repeated separators,
 * trailing separators, and {@code .}/{@code ..} components are normalized.</p>
 */
public final class FileSystem {

    private static final String ROOT_NAME = "root";

    private final Directory root = new Directory(ROOT_NAME);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public void mkdir(String path) {
        withWriteLock(() -> create(path, true));
    }

    public void createFile(String path) {
        withWriteLock(() -> create(path, false));
    }

    /** Retained as a shell-style alias for createFile. */
    public void touch(String path) {
        createFile(path);
    }

    public String readFile(String path) {
        return withReadLock(() -> requireFile(resolve(parse(path)), path).read());
    }

    public void writeFile(String path, String content, WriteMode mode) {
        Objects.requireNonNull(content, "content cannot be null");
        Objects.requireNonNull(mode, "write mode cannot be null");
        withWriteLock(() -> {
            File file = requireFile(resolve(parse(path)), path);
            if (mode == WriteMode.APPEND) {
                file.append(content);
            } else {
                file.overwrite(content);
            }
        });
    }

    /** Returns a stable, name-sorted snapshot. Listing a file returns that file. */
    public List<NodeInfo> list(String path) {
        return withReadLock(() -> {
            Node node = resolve(parse(path));
            if (node instanceof File) {
                return List.of(toInfo(node));
            }
            return ((Directory) node).list().stream()
                    .sorted(Comparator.comparing(Node::getName))
                    .map(this::toInfo)
                    .toList();
        });
    }

    public void ls(String path) {
        list(path).forEach(System.out::println);
    }

    public NodeInfo stat(String path) {
        return withReadLock(() -> toInfo(resolve(parse(path))));
    }

    public boolean exists(String path) {
        return withReadLock(() -> {
            try {
                resolve(parse(path));
                return true;
            } catch (PathNotFound ignored) {
                return false;
            }
        });
    }

    public void delete(String path, boolean recursive) {
        withWriteLock(() -> {
            List<String> components = parse(path);
            rejectRootMutation(components, "delete");
            Node node = resolve(components);
            if (node instanceof Directory directory && !recursive && !directory.isEmpty()) {
                throw new DirectoryNotEmpty(node.path());
            }
            node.getParent().remove(node.getName());
        });
    }

    /**
     * Moves source to the exact destination path. A different destination leaf name
     * performs a rename as part of the same atomic operation.
     */
    public void move(String sourcePath, String destinationPath) {
        withWriteLock(() -> {
            List<String> sourceComponents = parse(sourcePath);
            List<String> destinationComponents = parse(destinationPath);
            rejectRootMutation(sourceComponents, "move");
            rejectRootMutation(destinationComponents, "replace");

            Node source = resolve(sourceComponents);
            ParentAndName destination = resolveParent(destinationComponents);
            if (source.getParent() == destination.parent()
                    && source.getName().equals(destination.name())) {
                return;
            }
            if (destination.parent().contains(destination.name())) {
                throw new AlreadyExists(destinationPath);
            }
            if (source instanceof Directory) {
                for (Directory ancestor = destination.parent(); ancestor != null; ancestor = ancestor.getParent()) {
                    if (ancestor == source) {
                        throw new InvalidOperation("A directory cannot be moved into its descendant");
                    }
                }
            }

            Directory oldParent = source.getParent();
            String oldName = source.getName();
            oldParent.remove(oldName);
            source.renameTo(destination.name());
            destination.parent().add(source);
        });
    }

    public void rename(String path, String newName) {
        try {
            Node.requireValidName(newName);
        } catch (IllegalArgumentException exception) {
            throw new InvalidPath(exception.getMessage());
        }
        List<String> components = parse(path);
        rejectRootMutation(components, "rename");
        List<String> destination = new ArrayList<>(components.subList(0, components.size() - 1));
        destination.add(newName);
        move(path, toPath(destination));
    }

    private void create(String path, boolean directory) {
        List<String> components = parse(path);
        rejectRootMutation(components, "create");
        ParentAndName target = resolveParent(components);
        if (target.parent().contains(target.name())) {
            throw new AlreadyExists(path);
        }
        target.parent().add(directory ? new Directory(target.name()) : new File(target.name()));
    }

    private Node resolve(List<String> components) {
        Node current = root;
        for (String component : components) {
            if (!(current instanceof Directory directory)) {
                throw new NotDirectory(current.path());
            }
            current = directory.get(component);
            if (current == null) {
                throw new PathNotFound(toPath(components));
            }
        }
        return current;
    }

    private ParentAndName resolveParent(List<String> components) {
        rejectRootMutation(components, "resolve parent of");
        List<String> parentComponents = components.subList(0, components.size() - 1);
        Node parent = resolve(parentComponents);
        if (!(parent instanceof Directory directory)) {
            throw new NotDirectory(parent.path());
        }
        return new ParentAndName(directory, components.getLast());
    }

    private List<String> parse(String path) {
        if (path == null || path.isBlank() || !path.startsWith("/")) {
            throw new InvalidPath("Path must be a non-empty absolute path: " + path);
        }

        Deque<String> normalized = new ArrayDeque<>();
        for (String component : path.split("/")) {
            if (component.isEmpty() || component.equals(".")) {
                continue;
            }
            if (component.equals("..")) {
                if (normalized.isEmpty()) {
                    throw new InvalidPath("Path traverses above root: " + path);
                }
                normalized.removeLast();
                continue;
            }
            try {
                normalized.addLast(Node.requireValidName(component));
            } catch (IllegalArgumentException exception) {
                throw new InvalidPath(exception.getMessage());
            }
        }
        return List.copyOf(normalized);
    }

    private File requireFile(Node node, String requestedPath) {
        if (node instanceof Directory) {
            throw new IsDirectory(requestedPath);
        }
        return (File) node;
    }

    private NodeInfo toInfo(Node node) {
        return new NodeInfo(
                node.getId(),
                node.path(),
                node == root ? "/" : node.getName(),
                node instanceof Directory ? NodeType.DIRECTORY : NodeType.FILE,
                node.size(),
                node.getCreatedAt(),
                node.getModifiedAt()
        );
    }

    private void rejectRootMutation(List<String> components, String operation) {
        if (components.isEmpty()) {
            throw new InvalidOperation("Cannot " + operation + " the root directory");
        }
    }

    private String toPath(List<String> components) {
        return components.isEmpty() ? "/" : "/" + String.join("/", components);
    }

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

    private record ParentAndName(Directory parent, String name) {
    }
}
