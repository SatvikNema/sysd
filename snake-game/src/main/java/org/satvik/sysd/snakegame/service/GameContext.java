package org.satvik.sysd.snakegame.service;

import org.satvik.sysd.snakegame.entity.Board;
import org.satvik.sysd.snakegame.entity.Edible;
import org.satvik.sysd.snakegame.entity.MoveResult;
import org.satvik.sysd.snakegame.entity.RandomizedPositionSet;
import org.satvik.sysd.snakegame.entity.Snake;
import org.satvik.sysd.snakegame.exception.OutOfBoardException;
import org.satvik.sysd.snakegame.exception.SnakeCollisionException;
import org.satvik.sysd.snakegame.exception.SnakeDiedException;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.EdibleType;
import org.satvik.sysd.snakegame.model.GameState;
import org.satvik.sysd.snakegame.model.Position;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


public class GameContext {
    private final Board board;
    private final Snake snake;
    private final EdibleFactory edibleFactory;
    private final PositionGenerator positionGenerator;

    /** Cells occupied by neither the snake nor an edible — the source of truth for spawning. */
    private final RandomizedPositionSet freeCells;
    /** Active edibles keyed by their board position. */
    private final Map<Position, Edible> edibles;
    /** How many of each edible type to keep on the board. */
    private final Map<EdibleType, Integer> edibleCounts;

    private GameState state;

    public GameContext(int r, int c, Position snakePosition){
        this.board = new Board(r, c);
        if(board.isOutside(snakePosition)){
            throw new OutOfBoardException();
        }
        this.snake = new Snake(snakePosition);
        this.edibleFactory = new EdibleFactory();
        this.positionGenerator = new PositionGenerator();
        this.edibles = new HashMap<>();
        this.edibleCounts = new LinkedHashMap<>();
        this.edibleCounts.put(EdibleType.FOOD, 1);
        this.edibleCounts.put(EdibleType.POISON, 1);

        this.freeCells = new RandomizedPositionSet();
        this.state = GameState.IN_PROGRESS;
        initialiseFreeCells(snakePosition);
        initialiseEdibles();
    }

    private void initialiseFreeCells(Position snakePosition){
        for(int i = 0; i < board.getRow(); i++){
            for(int j = 0; j < board.getColumn(); j++){
                Position position = new Position(i, j);
                if(!position.equals(snakePosition)){
                    freeCells.add(position);
                }
            }
        }
    }

    private void initialiseEdibles(){
        for(Map.Entry<EdibleType, Integer> entry : edibleCounts.entrySet()){
            for(int i = 0; i < entry.getValue(); i++){
                spawn(entry.getKey());
            }
        }
    }

    public void tick(Direction direction){
        if(state != GameState.IN_PROGRESS){
            throw new RuntimeException("Game is not in progress!");
        }
        Position nextPosition = snake.getNextHead(direction);
        if(board.isOutside(nextPosition)){
            state = GameState.LOST;
            throw new OutOfBoardException("game over as out of bound at "+nextPosition);
        }
        try {
            Edible edible = edibles.remove(nextPosition);
            if(edible != null){
                applyMove(edible.apply(snake, direction));
                spawn(edible.getType());
            } else {
                applyMove(snake.move(direction));
            }
        } catch (SnakeCollisionException | SnakeDiedException ex){
            state = GameState.LOST;
            System.out.println("Game is lost");
            return;
        }
        System.out.println("snake is at "+snake.getHead()+" size: "+snake.getSize());
    }

    /**
     * Keep the free-cell set in sync with a snake movement. Vacated tails are added
     * back before the new head is removed so the tail-follow case (head moving into the
     * cell the tail just left) resolves to the cell being occupied.
     */
    private void applyMove(MoveResult result){
        for(Position tail : result.removedTails()){
            freeCells.add(tail);
        }
        freeCells.remove(result.addedHead());
    }

    /**
     * Place a new edible of the given type on a random free cell. If the board is full,
     * the player has filled every cell — the game is WON. State transitions live here.
     */
    private void spawn(EdibleType type){
        Optional<Position> position = positionGenerator.generate(freeCells);
        if(position.isEmpty()){
            state = GameState.WON;
            return;
        }
        Position cell = position.get();
        edibles.put(cell, edibleFactory.generate(type, cell));
        freeCells.remove(cell);
        System.out.println(type + " is at " + cell);
    }

    public GameState getState(){
        return state;
    }
}
