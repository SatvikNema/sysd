package com.satvik.sysd.service;

import java.time.Instant;
import java.util.UUID;

public record NodeInfo(
        UUID id,
        String path,
        String name,
        NodeType type,
        long size,
        Instant createdAt,
        Instant modifiedAt
) {
}
