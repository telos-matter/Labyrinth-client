package hemmouda.maze.game.player.nnPlayer;

import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.game.player.Player;

/**
 * A Neural Network player.
 */
public final class NnPlayer extends Player {

    private static NnPlayer instance;

    public static NnPlayer getInstance() {
        if (instance == null) {
            instance = new NnPlayer();
        }
        return instance;
    }

    private NnPlayer () {}

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public MoveMessageData getMove(AwaitMoveMessageData message) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
