package hemmouda.maze.game.logic.player.bestTurnPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.logic.player.Player;
import hemmouda.maze.game.logic.player.bestTurnPlayer.core.MoveEvaluator;
import hemmouda.maze.game.logic.player.util.BoardUtil;
import hemmouda.maze.game.logic.player.util.Randomness;
import hemmouda.maze.util.Const;

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
    public void initialize() {}

    /**
     * <p>Obviously, if the treasure is
     * directly reachable in the current turn,
     * then that's the move it would take.</p>
     *
     * <p>The question is what to do when it's not. Trying
     * to get close to a treasure, so that in
     * the next turn we can more easily reach it
     * is not that good of an idea
     * because:
     * <ol>
     * <li>It could get stuck going back and
     * forth between two neighboring cells.</li>
     * <li>One should not forget, that the player
     * can move freely as long as there is a path, so
     * getting closer to the treasure isn't really that
     * useful.</li>
     * </ol>
     * </p>
     *
     * <p>What I thought of as a solution to this problem
     * is to simply go to a random insertion position. That
     * way, in the next turn, we have a lot more
     * flexibility / more reach
     * because we can jump to the other side of the board.</p>
     *
     * <p>ATM, it just picks a random insertion position, it could
     * do with a little improvement to make sure it does
     * not pick the opposite of the forbidden insertion
     * position, but that would not happen usually, so
     * it's fine.</p>
     *
     * <p>Also, in response to "Why not pick the insertion
     * position nearest to the
     * treasure?" Well, because when the treasure is unreachable,
     * you'd want randomness in
     * your decision and not repetition, otherwise, your player
     * could get stuck. More precisely, you want to avoid
     * getting stuck, easiest way to do that, not the optimal
     * tho, is randomness.</p>
     */
    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        // Where to go if the treasure is unreachable
        Position alternativeGoal = Randomness.getRandomElement(Const.POSSIBLE_SHIFT_POSITIONS);
        // The best move to get us to that alternativeGoal
        MoveEvaluator.MoveRecord bestMove = null;

        // For every rotation of the shift card
        for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
            // And for every shift position
            for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
                // Get the new board, then get both the treasure
                // position and the player position on it
                Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, currentTreasure);
                Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());
                // And for every reachable position within that board
                for (Position position : boardAfter.getAllReachablePositions(playerPosition)) {
                    // Check if we can reach the treasure
                    if (position.equals(treasurePosition)) {
                        // If so, that is the move
                        return constructMoveMessage(card, shiftPosition, position);
                    }

                    // Otherwise, let MoveEvaluator pick the best move
                    var moveRecord = MoveEvaluator.constructMoveRecord(card, shiftPosition, position, alternativeGoal);
                    bestMove = MoveEvaluator.pick(bestMove, moveRecord);
                }
            }
        }

        // If the treasure cannot be reached, return the best move
        return bestMove.constructMoveMessageData();
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {} // Hh, ok 3

    @Override
    public String toString() {
        return "BestTurnPlayer";
    }
}
