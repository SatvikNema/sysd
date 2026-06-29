package org.satvik.sysd.snakegame.entity;

import org.satvik.sysd.snakegame.model.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * A set of positions supporting O(1) add, remove and uniform random pick,
 * using the classic list + index-map swap-remove pattern.
 */
public class RandomizedPositionSet {
    private final List<Position> items = new ArrayList<>();
    private final Map<Position, Integer> indexOf = new HashMap<>();

    public void add(Position position) {
        if (indexOf.containsKey(position)) {
            return;
        }
        indexOf.put(position, items.size());
        items.add(position);
    }

    public void remove(Position position) {
        Integer index = indexOf.remove(position);
        if (index == null) {
            return;
        }
        int lastIndex = items.size() - 1;
        Position last = items.get(lastIndex);
        items.set(index, last);
        if (!last.equals(position)) {
            indexOf.put(last, index);
        }
        items.remove(lastIndex);
    }

    public Optional<Position> pickRandom(Random random) {
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.get(random.nextInt(items.size())));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }
}
