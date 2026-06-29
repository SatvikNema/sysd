package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.entity.Board;
import org.satvik.sysd.snakegame.entity.Edible;
import org.satvik.sysd.snakegame.entity.Snake;
import org.satvik.sysd.snakegame.exception.OutOfBoardException;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.EdibleType;
import org.satvik.sysd.snakegame.model.Position;

import java.util.HashSet;
import java.util.Set;


public class GameContext {
    private Board board;
    private Snake snake;
    private EdibleFactory edibleFactory;
    private Edible poison;
    private Edible food;

    public GameContext(int r, int c, Position snakePosition){
        this.board = new Board(r, c);
        if(board.isOutside(snakePosition)){
            throw new OutOfBoardException();
        }
        this.snake = new Snake(snakePosition);
        initialiseEdibles();
    }

    public void initialiseEdibles(){
        this.edibleFactory = new EdibleFactory(new PositionGenerator());
        this.food = edibleFactory.generate(EdibleType.FOOD, this);
        this.poison = edibleFactory.generate(EdibleType.POISON, this);
        System.out.println("Food is at "+food.getPosition());
        System.out.println("Poison is at "+poison.getPosition());
    }

    public void tick(Direction direction){
        Position nextPosition = snake.getNextHead(direction);
        if(board.isOutside(nextPosition)){
            throw new OutOfBoardException("game over as out of bound at "+nextPosition);
        }
        Edible edible = null;
        if(nextPosition.equals(poison.getPosition())){
            edible = poison;
            this.poison = edibleFactory.generate(EdibleType.POISON, this);
            System.out.println("Poison is at "+poison.getPosition());
        } else if(nextPosition.equals(food.getPosition())){
            edible = food;
            this.food = edibleFactory.generate(EdibleType.FOOD, this);
            System.out.println("Food is at "+food.getPosition());
        }
        if(edible != null){
            edible.apply(snake, direction);
        } else {
            snake.move(direction);
        }
        System.out.println("snake is at "+snake.getHead()+" size: "+snake.getSize());
    }


    // improvement this can be made O(1) if we keep a track of occupied positions on board on every tick
    public Set<Position> getOccupiedPositions(){
        Set<Position> occupiedPositions = new HashSet<>(snake.getBody());
        if(food != null) {
            occupiedPositions.add(food.getPosition());
        }
        if(poison != null) {
            occupiedPositions.add(poison.getPosition());
        }
        return occupiedPositions;
    }

    public int[] getGameSize(){
        return new int[]{board.getRow(), board.getColumn()};
    }
}
