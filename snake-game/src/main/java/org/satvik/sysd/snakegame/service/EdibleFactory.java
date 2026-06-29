package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.entity.*;
import org.satvik.sysd.snakegame.model.EdibleType;
import org.satvik.sysd.snakegame.model.Position;

public class EdibleFactory {

    private final PositionGenerator positionGenerator;

    public EdibleFactory(PositionGenerator generator){
        this.positionGenerator = generator;
    }

    public Edible generate(EdibleType type, GameContext gameContext){
        Position position = positionGenerator.generate(gameContext);
        Edible edible;
        switch (type){
            case FOOD -> edible = new Food(position);
            case POISON -> edible = new Poison(position);
            case null, default -> throw new RuntimeException("No edible impl found for "+type);
        }
        return edible;
    }
}
