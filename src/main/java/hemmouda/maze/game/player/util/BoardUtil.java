package hemmouda.maze.game.player.util;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.PositionData;
import de.fhac.mazenet.server.generated.Treasure;
import hemmouda.maze.App;
import hemmouda.maze.util.Const;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BoardUtil {

    // TODO restructure. Maybe another class for some other stuff

    /**
     * Inserts the given shiftCard
     * at the given shiftPosition.
     * @implNote updates the given board.
     */
    public static void applyShift (Board board, Position shiftPosition, Card shiftCard) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftPosition(shiftPosition);
        moveMessage.setShiftCard(shiftCard);
        board.proceedShift(moveMessage);
    }

    /**
     * Inserts the shiftCard
     * of the board with the
     * given orientation at the
     * given shiftPosition.
     * @implNote updates the given board.
     */
    public static void applyShift (Board board, Position shiftPosition, Card.Orientation orientation) {
        var shiftCard = new Card(board.getShiftCard());
        shiftCard = rotate(shiftCard, orientation);

        applyShift(board, shiftPosition, shiftCard);
    }

    /**
     * Fake inserts
     * the given shiftCard
     * at the given shiftPosition.
     */
    public static Board fakeInsert (Board board, Position shiftPosition, Card shiftCard) {
        Board clone = (Board) board.clone();
        applyShift(clone, shiftPosition, shiftCard);
        return clone;
    }

    /**
     * @return the given card rotated to the given orientation
     */
    public static Card rotate (Card card, Card.Orientation orientation) {
        return new Card(card.getShape(), orientation, card.getTreasure());
    }

    /**
     * @return all possible rotations of this card
     */
    public static Collection<Card> getAllRotations (Card card) {
        return Arrays.stream(Card.Orientation.values()).
                map(orientation -> rotate(card, orientation)).
                collect(Collectors.toList());
    }

    /**
     * @return all possible shift position on this board
     */
    public static List<Position> getAllShiftPositions (Board board) {
        return Const.POSSIBLE_SHIFT_POSITIONS.stream().
                filter(shift -> !shift.equals(board.getForbidden())).
                collect(Collectors.toList());
    }

    /**
     * @return all reachable positions
     * of the specified player after the insertion
     * @implNote does not modify the board
     */
    public static Collection<Position> getAllReachablePositions (Board board, Card card, Position shiftPosition, int playerId) {
        Board clone = (Board) board.clone();
        applyShift(clone, shiftPosition, card);
        Position playerPosition = clone.findPlayer(playerId);
        return clone.getAllReachablePositions(playerPosition);
    }

    /**
     * @return the treasure position on the board
     * or <code>null</code> if it's on the shift card
     */
    public static Position getTreasurePosition (Board board, Treasure treasure) {
        PositionData positionData = board.findTreasure(treasure);
        if (positionData == null) {
            return null;
        } else {
            return new Position(positionData);
        }
    }

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
