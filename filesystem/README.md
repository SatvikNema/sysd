# In-memory filesystem LLD

This module models a single-process, in-memory hierarchical filesystem. It is
intended to demonstrate low-level design rather than operating-system storage.

## Supported operations

- Create directories and files (`mkdir`, `createFile`/`touch`)
- Read, overwrite, and append file content
- List and inspect nodes (`list`, `stat`, `exists`)
- Delete, including explicit recursive directory deletion
- Move and rename nodes atomically

Paths must be absolute. Repeated and trailing separators are ignored, `.` and
`..` are normalized, and traversal above root is rejected.

## Invariants

- A node has at most one parent.
- Sibling names are unique across files and directories.
- A directory cannot be moved below itself.
- Root cannot be created, deleted, moved, or renamed.
- Callers receive immutable `NodeInfo` snapshots, not mutable tree nodes.

## Concurrency and complexity

A fair filesystem-wide read/write lock makes every public operation atomic.
Independent reads may run concurrently; mutations are serialized. This is a
deliberate interview-scale choice. Fine-grained node locks could improve write
parallelism but would require deterministic lock ordering and more complex move
semantics.

Path lookup is `O(d)` average time for `d` components. Directory listing is
`O(n log n)` because results are sorted. Directory size is `O(k)` over its
descendants.

## Deliberately out of scope

Persistence, permissions/ACLs, symbolic links, quotas, file handles, journaling,
and distributed operation are extension points. Adding permissions would also
require an authenticated user context on every operation; metadata-only
permissions would be misleading.
