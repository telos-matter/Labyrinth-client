package hemmouda.maze.game.player.minMaxPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.player.Player;

import java.util.List;

/**
 * A player that gets the best move with a MinMax algorithm
 */
public final class MinMaxPlayer extends Player {

    private static MinMaxPlayer instance;

    public static MinMaxPlayer getInstance() {
        if (instance == null) {
            instance = new MinMaxPlayer();
        }
        return instance;
    }

    private MinMaxPlayer () {}

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
