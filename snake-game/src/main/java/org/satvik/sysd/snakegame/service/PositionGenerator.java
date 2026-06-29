package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.model.Position;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PositionGenerator {

    public Position generate(GameContext gameContext){
        int[] gameSize = gameContext.getGameSize();
        int rows = gameSize[0];
        int cols = gameSize[1];

        Set<Position> occupied = gameContext.getOccupiedPositions();

        Random random = ThreadLocalRandom.current();

        while (true) {
            Position position = new Position(
                    random.nextInt(rows),
                    random.nextInt(cols)
            );

            if (!occupied.contains(position)) {
                return position;
            }
        }
    }
}
