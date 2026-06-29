package org.satvik.sysd.snakegame.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Food implements Edible{
    private Position position;

    @Override
    public void apply(Snake snake, Direction direction) {
        snake.grow(direction);
    }
}
