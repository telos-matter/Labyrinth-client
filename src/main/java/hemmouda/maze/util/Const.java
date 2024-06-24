package hemmouda.maze.util;

import de.fhac.mazenet.server.game.Position;

import java.util.List;

/**
 * A constants record
 */
public final class Const {

    public static final List<Position> POSSIBLE_SHIFT_POSITIONS = List.of(
            // Going clock wise
            // Top shifts
            new Position(0, 1),
            new Position(0, 3),
            new Position(0, 5),
            // Right shifts
            new Position(1, 6),
            new Position(3, 6),
            new Position(5, 6),
            // Bottom shifts
            new Position(6, 5),
            new Position(6, 3),
            new Position(6, 1),
            // Left shifts
            new Position(5, 0),
            new Position(3, 0),
            new Position(1, 0)
    );

}
