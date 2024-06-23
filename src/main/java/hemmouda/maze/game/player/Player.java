package hemmouda.maze.game.player;

import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.CardData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.App;

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
     */
    protected static MoveMessageData constructMoveMessage (CardData shiftCard, Position shiftPosition, Position newPlayerPosition) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftCard(shiftCard);
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setNewPinPos(newPlayerPosition);

        return moveMessage;
    }

}
