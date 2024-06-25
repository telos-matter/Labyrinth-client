package hemmouda.maze.game.player;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import de.fhac.mazenet.server.generated.Treasure;
import hemmouda.maze.App;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The player gameplay is not
 * necessarily linear. It is
 * just prompted to give the best
 * move for the given state of the
 * board.
 * This Player base class takes
 * away the boilerplate
 * and lets the actual implementation
 * of Player only focus on giving
 * the moves.
 */
public abstract class Player {

    public abstract void initialize ();

    /**
     * Give me the response move for this
     * state of the board.
     */
    public final MoveMessageData getMove (AwaitMoveMessageData message) {
        // Copy the data so the player can do whatever he wants with it
        var board = (Board) new Board(message.getBoard()).clone();
        var currentTreasure = message.getTreasureToFindNext();
        var remainingTreasures = new ArrayList<>(message.getTreasuresToGo());

        // Start the timer
        Logger.debug("%s started thinking", this);
        final long startTime = System.nanoTime();

        // Ask the player for a response
        var response = think(board, currentTreasure, remainingTreasures);

        // Stop the timer
        final float duration = (System.nanoTime() - startTime) / 1_000_000_000f;
        Logger.debug("%s finished thinking in %.3f secs",this, duration);

        // Report and return
        reportMove(message, response);
        return response;
    }

    /**
     * The method that Player implementations
     * should implement to respond.
     * The method getMove does the rest.
     * Feel free to modify the data
     * however you like.
     */
    protected abstract MoveMessageData think (Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures);

    /**
     * Utility method for subclasses
     * to constructs a MoveMessage
     * from the given data.
     * Intended to be used
     * by Player implementations to
     * generate a response move.
     */
    protected static MoveMessageData constructMoveMessage (Card shiftCard, Position shiftPosition, Position newPinPosition) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftCard(shiftCard);
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setNewPinPos(newPinPosition);

        return moveMessage;
    }

    /**
     * Logs a debug messages to report
     * the move
     */
    protected static void reportMove (AwaitMoveMessageData message, MoveMessageData response) {
        if (!Settings.DEBUG) {
            return; // No need to waste memory or time
        }

        var board = new Board(message.getBoard());
        var shiftCard = new Card(board.getShiftCard());
        var shiftPosition = new Position(response.getShiftPosition());
        var originalPosition = board.findPlayer(GameInfo.getPlayerId());
        var newPosition = new Position(response.getNewPinPos());

        StringBuilder sb = new StringBuilder();
        sb.append("Move report:%n\t-> Inserting %s:%s at %s%n\t-> ");
        if (originalPosition.equals(newPosition)) {
            sb.append("Remaining still at %s");
            Logger.debug(sb.toString(),
                    shiftCard.getShape(),
                    shiftCard.getOrientation(),
                    shiftPosition,
                    originalPosition);
        } else {
            sb.append("Moving from %s to %s");
            Logger.debug(sb.toString(),
                    shiftCard.getShape(),
                    shiftCard.getOrientation(),
                    shiftPosition,
                    originalPosition,
                    newPosition);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
