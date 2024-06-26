package hemmouda.maze.game.player.util;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.App;

public class BoardUtil {

    /**
     * Inserts the shiftCard
     * of the board with the
     * given orientation at the
     * given shiftPosition
     */
    public static void applyShift (Board board, Position shiftPosition, Card.Orientation orientation) {
        var shiftCard = new Card(board.getShiftCard());
        shiftCard = rotate(shiftCard, orientation);

        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setShiftCard(shiftCard);
        board.proceedShift(moveMessage);
    }

    /**
     * @return the given card rotated to the given orientation
     */
    public static Card rotate (Card card, Card.Orientation orientation) {
        return new Card(card.getShape(), orientation, card.getTreasure());
    }

}
