package hemmouda.maze.game.player.minMaxPlayer;

import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.game.player.Player;

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
    public MoveMessageData getMove(AwaitMoveMessageData message) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
