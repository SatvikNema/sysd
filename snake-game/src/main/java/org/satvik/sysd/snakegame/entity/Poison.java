package org.satvik.sysd.snakegame.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.EdibleType;
import org.satvik.sysd.snakegame.model.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Poison implements Edible{
    private Position position;

    @Override
    public MoveResult apply(Snake snake, Direction direction) {
        return snake.shrink(direction);
    }

    @Override
    public EdibleType getType() {
        return EdibleType.POISON;
    }
}
