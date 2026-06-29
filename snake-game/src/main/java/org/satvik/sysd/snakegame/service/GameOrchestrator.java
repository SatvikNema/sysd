package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.exception.OutOfBoardException;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.GameState;
import org.satvik.sysd.snakegame.model.Position;

public class GameOrchestrator {
    public void startGame(){
        int boardSize = Integer.parseInt(IO.readln("Enter board size: "));

        GameContext gameContext = new GameContext(boardSize, boardSize, new Position(0, 0));

        while(gameContext.getState() == GameState.IN_PROGRESS){
            String dir = IO.readln("Enter direction: ");
            Direction d = Direction.from(dir);
            try {
                gameContext.tick(d);
            } catch (OutOfBoardException ex){
                System.out.println(ex.getMessage());
            }
        }

        System.out.println("Game over! Final state: " + gameContext.getState());
    }
}
