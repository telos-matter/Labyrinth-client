package hemmouda.maze.game.logic.player.lookAheadPlayer.core;

import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.game.logic.util.MoveMessageUtil;
import hemmouda.maze.util.Logger;

/**
 * Evaluates moves for BestTurnPlayer
 */
public class MoveEvaluator {

    /**
     * Evaluates the move
     * and constructs a
     * moveRecord for it
     */
    public static MoveRecord constructMoveRecord (
            Card card,
            Position shiftPosition,
            Position newPinPosition,
            Position goalPosition) {

        // All what is needed is the value,
        // which the reverse of the distance
        // to the goalPosition
        double x = newPinPosition.getCol() - goalPosition.getCol();
        double y = newPinPosition.getRow() - goalPosition.getRow();

        double value = -Math.hypot(x, y); // The further, the worse

        return new MoveRecord(card, shiftPosition, newPinPosition, value);
    }

    /**
     * Picks the best of the two
     */
    public static MoveRecord pick (MoveRecord a, MoveRecord b) {
        if (b == null && a == null) {
            Logger.error("Both MoveRecords cannot be null!");
            // IllegalState, and not IllegalArgument
            throw new IllegalStateException("Both MoveRecords cannot be null!");
        }

        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }

        return (a.value > b.value)? a : b;
    }

    /**
     * A record to hold a move's value
     * and data
     */
    public static record MoveRecord (
            Card card,
            Position shiftPosition,
            Position newPinPosition,
            double value
    ) {
        public MoveMessageData constructMoveMessageData () {
            return MoveMessageUtil.construct(card, shiftPosition, newPinPosition);
        }
    }

}
