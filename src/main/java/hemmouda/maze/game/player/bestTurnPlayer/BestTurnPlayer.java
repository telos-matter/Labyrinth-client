package hemmouda.maze.game.player.bestTurnPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.player.Player;
import hemmouda.maze.game.player.bestTurnPlayer.core.MoveEvaluator;
import hemmouda.maze.game.player.util.BoardUtil;

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

    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        MoveEvaluator.MoveRecord bestMove = null;

        // For every rotation of the shift card
        for (Card card : BoardUtil.getAllRotations(new Card(board.getShiftCard()))) {
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
                    var moveRecord = MoveEvaluator.constructMoveRecord(card, shiftPosition, position, treasurePosition);
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
