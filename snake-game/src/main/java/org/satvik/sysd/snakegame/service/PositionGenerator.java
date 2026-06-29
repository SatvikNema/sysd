package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.entity.RandomizedPositionSet;
import org.satvik.sysd.snakegame.model.Position;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class PositionGenerator {

    public Optional<Position> generate(RandomizedPositionSet freeCells){
        return freeCells.pickRandom(ThreadLocalRandom.current());
    }
}
