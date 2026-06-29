package com.satvik.sysd.interviewclaude;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {

    private FileSystem fs;

    @BeforeEach
    void setUp() {
        fs = new FileSystem();
    }

    @Test
    void createAndReadFile() {
        fs.mkdir("/home");
        fs.createFile("/home/notes.txt");
        fs.writeFile("/home/notes.txt", "hello");
        fs.appendToFile("/home/notes.txt", " world");
        assertEquals("hello world", fs.readFile("/home/notes.txt"));
    }

    @Test
    void listReturnsSortedNames() {
        fs.mkdir("/dir");
        fs.createFile("/dir/z.txt");
        fs.createFile("/dir/a.txt");
        assertEquals(List.of("a.txt", "z.txt"), fs.list("/dir"));
    }

    @Test
    void duplicateThrows() {
        fs.mkdir("/a");
        assertThrows(FileSystemException.AlreadyExists.class, () -> fs.mkdir("/a"));
        assertThrows(FileSystemException.AlreadyExists.class, () -> fs.createFile("/a"));
    }

    @Test
    void missingParentThrows() {
        assertThrows(FileSystemException.NotFound.class, () -> fs.createFile("/missing/file.txt"));
    }

    @Test
    void fileUsedAsDirectoryThrows() {
        fs.createFile("/file");
        assertThrows(FileSystemException.NotADirectory.class, () -> fs.createFile("/file/child"));
    }

    @Test
    void deleteNonEmptyDirectoryRequiresRecursive() {
        fs.mkdir("/tmp");
        fs.createFile("/tmp/file");
        assertThrows(FileSystemException.class, () -> fs.delete("/tmp", false));
        fs.delete("/tmp", true);
        assertThrows(FileSystemException.NotFound.class, () -> fs.readFile("/tmp/file"));
    }

    @Test
    void readWriteOnDirectoryThrows() {
        fs.mkdir("/dir");
        assertThrows(FileSystemException.IsDirectory.class, () -> fs.readFile("/dir"));
        assertThrows(FileSystemException.IsDirectory.class, () -> fs.writeFile("/dir", "x"));
    }
}
