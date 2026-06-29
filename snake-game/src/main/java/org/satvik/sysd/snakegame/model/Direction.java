package org.satvik.sysd.snakegame.model;

public enum Direction {
    UP, DOWN, RIGHT, LEFT;

    public static Direction from(String d){
        if("r".equals(d)){
            return RIGHT;
        } else if("d".equals(d)){
            return DOWN;
        } else if("l".equals(d)){
            return LEFT;
        } else if("u".equals(d)){
            return UP;
        } else {
            throw new IllegalArgumentException(d+" is not a recognised direction");
        }
    }
}
