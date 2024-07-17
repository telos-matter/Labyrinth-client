package hemmouda.maze.game.player.bestTurnPlayer.core;

import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.game.player.util.BoardUtil;
import hemmouda.maze.game.player.util.Randomness;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

import java.util.List;

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
            Position treasurePosition) {

        // All what is needed is the value
        double value;

        // Normally, this wouldn't be called
        // if the treasure was reached, but nevertheless
        // let's check
        if (newPinPosition.equals(treasurePosition)) {
            value = Double.POSITIVE_INFINITY;

        // With that out of the way, the
        // best move is getting close
        // to a random shift position, so
        // that in the next turn
        // we can move freely
        } else {

            // TODO make it go to a set random shift position, and not a different one for every single move
            // Go to a shift position other than the one you can reach
            Position randomShiftPosition;
            do {
                randomShiftPosition = Randomness.getRandomElement(Const.POSSIBLE_SHIFT_POSITIONS);
            } while (newPinPosition.equals(randomShiftPosition));
            double x = newPinPosition.getCol() - randomShiftPosition.getCol();
            double y = newPinPosition.getRow() - randomShiftPosition.getRow();

            value = -Math.hypot(x, y); // The further, the worse
        }

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
            return BoardUtil.constructMoveMessage(card, shiftPosition, newPinPosition);
        }
    }

}
