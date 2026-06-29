package org.satvik.sysd.snakegame.entity;

import org.satvik.sysd.snakegame.model.Position;

import java.util.List;

/**
 * Describes the cells that changed as a result of a snake movement.
 * {@code addedHead} is the new head cell now occupied by the snake.
 * {@code removedTails} are the cells the snake vacated this move
 * (empty when growing, one when moving, two when shrinking).
 */
public record MoveResult(Position addedHead, List<Position> removedTails) {
}
