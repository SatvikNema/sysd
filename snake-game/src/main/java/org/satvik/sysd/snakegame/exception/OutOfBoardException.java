package org.satvik.sysd.snakegame.exception;

public class OutOfBoardException extends RuntimeException {
    public OutOfBoardException(String msg){
        super(msg);
    }

    public OutOfBoardException(){
        super();
    }
}
