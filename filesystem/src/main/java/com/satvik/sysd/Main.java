package com.satvik.sysd;

public class Main {
    static void main() {
        System.out.println("hello");
    }
}
/*
design a file system

1. create files
2. open/read/modify/delete/rename files
3. file permissions, user permissions
4. create dirs
5. more file around dirs
6, findByPath
7. list contents

entities
1. File - id, filename, type, content createdAt, modifiedAt, createdBy, modifiedBy, size, parentFolder
2. Folder - id, directoryName, list<folder>, list<file>, createdBy, parentFolder, children
3. User - id, username
4. UserPermission - user, permission, path

Use composite design pattern - Node to handle files and directoryies cleanly behind a common abstract class



 */
