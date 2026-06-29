package org.satvik.sysd.snakegame.entity;

import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.Position;

public interface Edible {
    void apply(Snake snake, Direction direction);

    Position getPosition();
}
