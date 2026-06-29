package org.satvik.sysd.snakegame;

import org.satvik.sysd.snakegame.service.GameOrchestrator;

public class Main {
    static void main() {
        GameOrchestrator gameOrchestrator = new GameOrchestrator();
        gameOrchestrator.startGame();
    }
}

/*
Design a mobile snake game.

Core entities
snake
food
direction (up/down/right/left)
board
game


service
GameService
InputHander
 */
