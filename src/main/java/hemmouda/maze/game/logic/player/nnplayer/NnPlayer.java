package hemmouda.maze.game.logic.player.nnplayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.logic.player.Player;

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

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
