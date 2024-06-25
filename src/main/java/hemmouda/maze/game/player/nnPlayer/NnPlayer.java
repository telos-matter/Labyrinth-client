package hemmouda.maze.game.player.nnPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.player.Player;

import java.util.List;

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
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
