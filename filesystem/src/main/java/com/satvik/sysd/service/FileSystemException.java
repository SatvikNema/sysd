package com.satvik.sysd.service;

public class FileSystemException extends RuntimeException {

    public FileSystemException(String message) {
        super(message);
    }

    public static final class InvalidPath extends FileSystemException {
        public InvalidPath(String message) {
            super(message);
        }
    }

    public static final class PathNotFound extends FileSystemException {
        public PathNotFound(String path) {
            super("Path not found: " + path);
        }
    }

    public static final class AlreadyExists extends FileSystemException {
        public AlreadyExists(String path) {
            super("Path already exists: " + path);
        }
    }

    public static final class NotDirectory extends FileSystemException {
        public NotDirectory(String path) {
            super("Not a directory: " + path);
        }
    }

    public static final class IsDirectory extends FileSystemException {
        public IsDirectory(String path) {
            super("Is a directory: " + path);
        }
    }

    public static final class DirectoryNotEmpty extends FileSystemException {
        public DirectoryNotEmpty(String path) {
            super("Directory is not empty: " + path);
        }
    }

    public static final class InvalidOperation extends FileSystemException {
        public InvalidOperation(String message) {
            super(message);
        }
    }
}
