package hemmouda.maze.game.player.bestTurnPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.player.Player;
import hemmouda.maze.game.player.bestTurnPlayer.maxNImpl.BoardEvaluation;
import hemmouda.maze.game.player.bestTurnPlayer.maxNImpl.BoardState;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;
import telosmatter.maxnj.MaxN;

import java.util.List;

/**
 * A player that gets the best move with a MaxN algorithm
 */
public final class BestTurnPlayer extends Player {

    private static BestTurnPlayer instance;

    public static BestTurnPlayer getInstance() {
        if (instance == null) {
            instance = new BestTurnPlayer();
        }
        return instance;
    }

    private MaxN <Integer, MoveMessageData, BoardState> algorithm;

    /**
     * Has it been initialized and ready
     * to be used
     */
    private boolean initialized;

    private BestTurnPlayer() {
        initialized = false;
    }

    @Override
    public void initialize() {
        // The only thing needed to initialize is to know how many players are there
        if (!initialized && GameInfo.getPlayersIds() != null) {
            int playersCount = GameInfo.getPlayersIds().size();
            int depth = (Settings.MAX_N_LOOK_AHEAD * playersCount) + 1;
            algorithm = new MaxN<>(BoardEvaluation.evaluationFunction, depth);

            initialized = true;
        }
    }

    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        initialize();
        if (!initialized) {
            Logger.error("MaxN has been asked to play, yet it still isn't able to be initialized!");
            throw new IllegalStateException("MaxN is not yet initialized!");
        }

        var boardState = new BoardState(board, currentTreasure, remainingTreasures);
        return algorithm.getBestMove(boardState);
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public String toString() {
        if (algorithm != null) {
            return "MaxNPlayer:%d".formatted(algorithm.getDepth());
        } else {
            return "MaxNPlayer";
        }
    }
}
