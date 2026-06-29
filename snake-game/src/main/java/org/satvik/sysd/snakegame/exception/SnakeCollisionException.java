package org.satvik.sysd.snakegame.exception;

public class SnakeCollisionException extends RuntimeException {
    public SnakeCollisionException(String msg){
        super(msg);
    }

    public SnakeCollisionException(){
        super();
    }
}
