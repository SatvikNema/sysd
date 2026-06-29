package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.Position;

public class GameOrchestrator {
    public void startGame(){
        int boardSize = Integer.parseInt(IO.readln("Enter board size: "));

        GameContext gameContext = new GameContext(boardSize, boardSize, new Position(0, 0));

        while(true){
            String dir = IO.readln("Enter direction: ");
            Direction d = Direction.from(dir);
            gameContext.tick(d);
        }
    }
}
