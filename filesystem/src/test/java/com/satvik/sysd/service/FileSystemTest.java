package com.satvik.sysd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.satvik.sysd.service.FileSystemException.*;
import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {

    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystem();
    }

    @Test
    void createsAndResolvesNormalizedAbsolutePaths() {
        fileSystem.mkdir("/home");
        fileSystem.mkdir("/home//satvik/");
        fileSystem.createFile("/home/satvik/./notes.txt");

        assertTrue(fileSystem.exists("/home/satvik/../satvik/notes.txt"));
        assertEquals(NodeType.DIRECTORY, fileSystem.stat("/").type());
        assertThrows(InvalidPath.class, () -> fileSystem.mkdir("relative"));
        assertThrows(InvalidPath.class, () -> fileSystem.stat("/.."));
    }

    @Test
    void rejectsDuplicateNamesWithoutDestroyingExistingContent() {
        fileSystem.mkdir("/data");
        fileSystem.createFile("/data/report.txt");
        fileSystem.writeFile("/data/report.txt", "keep me", WriteMode.OVERWRITE);

        assertThrows(AlreadyExists.class, () -> fileSystem.createFile("/data/report.txt"));
        assertThrows(AlreadyExists.class, () -> fileSystem.mkdir("/data/report.txt"));
        assertEquals("keep me", fileSystem.readFile("/data/report.txt"));
    }

    @Test
    void readsWritesAppendsAndMeasuresUtf8Bytes() {
        fileSystem.createFile("/message.txt");
        fileSystem.writeFile("/message.txt", "hé", WriteMode.OVERWRITE);
        fileSystem.writeFile("/message.txt", "!", WriteMode.APPEND);

        assertEquals("hé!", fileSystem.readFile("/message.txt"));
        assertEquals(4, fileSystem.stat("/message.txt").size());
        assertThrows(IsDirectory.class, () -> fileSystem.readFile("/"));
    }

    @Test
    void reportsMissingParentsAndFilesUsedAsDirectories() {
        assertThrows(PathNotFound.class, () -> fileSystem.createFile("/missing/file.txt"));

        fileSystem.createFile("/plain-file");
        assertThrows(NotDirectory.class, () -> fileSystem.createFile("/plain-file/child"));
    }

    @Test
    void listsStableSortedSnapshots() {
        fileSystem.mkdir("/work");
        fileSystem.createFile("/work/z.txt");
        fileSystem.createFile("/work/a.txt");

        List<NodeInfo> listing = fileSystem.list("/work");

        assertEquals(List.of("a.txt", "z.txt"), listing.stream().map(NodeInfo::name).toList());
        assertThrows(UnsupportedOperationException.class, () -> listing.add(fileSystem.stat("/")));
        assertEquals(List.of("z.txt"), fileSystem.list("/work/z.txt").stream().map(NodeInfo::name).toList());
    }

    @Test
    void requiresRecursiveDeleteForNonEmptyDirectoryAndProtectsRoot() {
        fileSystem.mkdir("/tmp");
        fileSystem.createFile("/tmp/file");

        assertThrows(DirectoryNotEmpty.class, () -> fileSystem.delete("/tmp", false));
        assertThrows(InvalidOperation.class, () -> fileSystem.delete("/", true));

        fileSystem.delete("/tmp", true);
        assertFalse(fileSystem.exists("/tmp"));
    }

    @Test
    void atomicallyMovesAndRenamesNodes() {
        fileSystem.mkdir("/source");
        fileSystem.mkdir("/destination");
        fileSystem.createFile("/source/a.txt");
        fileSystem.writeFile("/source/a.txt", "content", WriteMode.OVERWRITE);

        fileSystem.move("/source/a.txt", "/destination/b.txt");

        assertFalse(fileSystem.exists("/source/a.txt"));
        assertEquals("content", fileSystem.readFile("/destination/b.txt"));

        fileSystem.rename("/destination/b.txt", "final.txt");
        assertTrue(fileSystem.exists("/destination/final.txt"));
        assertThrows(InvalidPath.class, () -> fileSystem.rename("/destination/final.txt", "bad/name"));
    }

    @Test
    void preventsDirectoryCyclesAndDestinationCollisions() {
        fileSystem.mkdir("/a");
        fileSystem.mkdir("/a/b");
        fileSystem.mkdir("/other");
        fileSystem.createFile("/other/existing");

        assertThrows(InvalidOperation.class, () -> fileSystem.move("/a", "/a/b/a"));
        assertThrows(AlreadyExists.class, () -> fileSystem.move("/a/b", "/other/existing"));
        assertTrue(fileSystem.exists("/a/b"));
    }
}
