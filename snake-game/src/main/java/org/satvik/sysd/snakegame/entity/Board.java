package org.satvik.sysd.snakegame.entity;

import lombok.Data;
import org.satvik.sysd.snakegame.model.Position;


@Data
public class Board {
    private int row;
    private int column;

    public Board(int r, int c){
        this.row = r;
        this.column = c;
    }

    public boolean isOutside(Position p){
        int i = p.getX(), j = p.getY();
        return i<0 || j<0 || i>=row || j>=column;
    }
}
