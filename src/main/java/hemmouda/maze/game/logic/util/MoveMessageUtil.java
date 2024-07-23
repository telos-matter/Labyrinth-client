package hemmouda.maze.game.logic.util;

import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.App;

/**
 * A utility class for the
 * MoveMessageData type of
 * messages
 */
public class MoveMessageUtil {

    /**
     * Constructs a moveMessage from the given data
     */
    public static MoveMessageData constructMoveMessage (Card shiftCard, Position shiftPosition, Position newPinPosition) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftCard(shiftCard);
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setNewPinPos(newPinPosition);

        return moveMessage;
    }

}
