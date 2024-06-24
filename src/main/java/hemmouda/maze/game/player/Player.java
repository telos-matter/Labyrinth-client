package hemmouda.maze.game.player;

import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.CardData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.App;
import hemmouda.maze.util.Logger;

/**
 * The player gameplay is not
 * necessarily linear. It is
 * just prompted to give the best
 * move for the given state of the
 * board.
 */
public abstract class Player {

    public abstract void initialize ();

    /**
     * Give me the move for this
     * state of the board
     */
    public abstract MoveMessageData getMove (AwaitMoveMessageData message);

    /**
     * Constructs a MoveMessage
     * from the given data.
     * Intended to be used when
     * generating a response move.
     */
    protected static MoveMessageData constructMoveMessage (CardData shiftCard, Position shiftPosition, Position newPlayerPosition) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftCard(shiftCard);
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setNewPinPos(newPlayerPosition);

        return moveMessage;
    }

    /**
     * Logs a debug messages to report
     * the move
     */
    protected static void reportMove (Card shiftCard, Position shiftPosition, Position originalPosition, Position newPosition) {
        Logger.debug("Inserting %s:%s at %s", shiftCard.getShape(), shiftCard.getOrientation(), shiftPosition);
        if (originalPosition.equals(newPosition)) {
            Logger.debug("And remaining still at %s", originalPosition);
        } else {
            Logger.debug("Then moving from %s to %s", originalPosition, newPosition);
        }
    }

}
