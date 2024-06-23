package hemmouda.maze.game.player.randomPlayer;

import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.game.player.Player;

/**
 * A random player. Just for testing.
 */
public final class RandomPlayer implements Player {

    private static RandomPlayer instance;

    public static RandomPlayer getInstance() {
        if (instance == null) {
            instance = new RandomPlayer();
        }
        return instance;
    }

    private RandomPlayer () {}

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public MoveMessageData getMove(AwaitMoveMessageData message) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
