package org.satvik.sysd.snakegame.entity;

import org.satvik.sysd.snakegame.exception.SnakeCollisionException;
import org.satvik.sysd.snakegame.model.Direction;
import org.satvik.sysd.snakegame.model.Position;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


public class Snake {
    private final Set<Position> positionSet;
    private final Deque<Position> body;

    public Snake(Position position){
        positionSet = new HashSet<>();
        body = new LinkedList<>();

        positionSet.add(position);
        body.add(position);
    }

    public Position getNextHead(Direction direction){
        Position current = body.peekFirst();
        if(current == null){
            throw new IllegalStateException("Snake is not initialised");
        }
        Position result;
        switch (direction){
            case UP -> result = new Position(current.getX()-1, current.getY());
            case DOWN -> result = new Position(current.getX()+1, current.getY());
            case RIGHT -> result = new Position(current.getX(), current.getY()+1);
            case LEFT -> result = new Position(current.getX(), current.getY()-1);
            case null, default -> throw new RuntimeException("Direction "+direction+" is not recognised");
        }
        return result;
    }

    public void move(Direction direction){
        Position nextPosition = getNextHead(direction);
        if(willCollide(nextPosition, true)){
            throw new SnakeCollisionException();
        }
        positionSet.remove(body.removeLast());
        positionSet.add(nextPosition);
        body.addFirst(nextPosition);
    }

    public void grow(Direction direction){
        Position nextPosition = getNextHead(direction);
        if(willCollide(nextPosition, false)){
            throw new SnakeCollisionException();
        }
        positionSet.add(nextPosition);
        body.addFirst(nextPosition);
    }

    public void shrink(Direction direction){
        move(direction);
        if(!body.isEmpty()) {
            positionSet.remove(body.removeLast());
        }
    }

    private boolean willCollide(Position nextHead, boolean ignoreTail){
        if(ignoreTail && nextHead.equals(body.peekLast())){
            return false;
        }
        return positionSet.contains(nextHead);
    }

    public Position getHead(){
        return body.peekFirst();
    }

    public Set<Position> getBody(){
        return new HashSet<>(positionSet);
    }

    public int getSize(){
        return positionSet.size();
    }
}
