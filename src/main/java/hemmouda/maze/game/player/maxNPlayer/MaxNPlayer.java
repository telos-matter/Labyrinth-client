package hemmouda.maze.game.player.maxNPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.player.Player;

import java.util.List;

/**
 * A player that gets the best move with a MaxN algorithm
 */
public final class MaxNPlayer extends Player {

    private static MaxNPlayer instance;

    public static MaxNPlayer getInstance() {
        if (instance == null) {
            instance = new MaxNPlayer();
        }
        return instance;
    }

    private MaxNPlayer() {}

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
