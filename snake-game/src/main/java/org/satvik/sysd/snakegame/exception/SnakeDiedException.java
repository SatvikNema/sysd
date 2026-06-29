package org.satvik.sysd.snakegame.exception;

public class SnakeDiedException extends RuntimeException {
    public SnakeDiedException(String msg){
        super(msg);
    }

    public SnakeDiedException(){
        super();
    }
}
