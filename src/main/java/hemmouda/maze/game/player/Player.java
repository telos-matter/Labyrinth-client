package hemmouda.maze.game.player;

import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;

/**
 * The player gameplay is not
 * necessarily linear. It is
 * just prompted to give the best
 * move for the given state of the
 * board.
 */
public interface Player {

    public abstract void initialize ();

    /**
     * Give me the move for this
     * state of the board
     */
    public abstract MoveMessageData getMove (AwaitMoveMessageData message);

    public static Player getPlayer () {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
