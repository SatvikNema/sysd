package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.entity.*;
import org.satvik.sysd.snakegame.model.EdibleType;
import org.satvik.sysd.snakegame.model.Position;

public class EdibleFactory {

    public Edible generate(EdibleType type, Position position){
        switch (type){
            case FOOD -> { return new Food(position); }
            case POISON -> { return new Poison(position); }
            case null, default -> throw new RuntimeException("No edible impl found for "+type);
        }
    }
}
