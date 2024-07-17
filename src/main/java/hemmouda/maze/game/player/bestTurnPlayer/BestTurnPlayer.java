package hemmouda.maze.game.player.bestTurnPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.player.Player;

import java.util.List;

/**
 * A player that plays the "best" move for the current turn
 */
public final class BestTurnPlayer extends Player {

    private static BestTurnPlayer instance;

    public static BestTurnPlayer getInstance() {
        if (instance == null) {
            instance = new BestTurnPlayer();
        }
        return instance;
    }

    private BestTurnPlayer() {}

    @Override
    public void initialize() {

    }

    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        return null;
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public String toString() {
        return "BestTurnPlayer";
    }
}
