package com.satvik.sysd.interviewclaude;

class FileSystemException extends RuntimeException {

    FileSystemException(String message) { super(message); }

    static class NotFound extends FileSystemException {
        NotFound(String path) { super("Not found: " + path); }
    }

    static class AlreadyExists extends FileSystemException {
        AlreadyExists(String path) { super("Already exists: " + path); }
    }

    static class NotADirectory extends FileSystemException {
        NotADirectory(String path) { super("Not a directory: " + path); }
    }

    static class IsDirectory extends FileSystemException {
        IsDirectory(String path) { super("Is a directory: " + path); }
    }
}
