package hemmouda.maze.game.logic.util;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.PositionData;
import de.fhac.mazenet.server.generated.Treasure;
import hemmouda.maze.App;
import hemmouda.maze.util.Const;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for the board
 */
public class BoardUtil {

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
        shiftCard = CardUtil.rotate(shiftCard, orientation);

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
     * @return all possible shift position on this board
     */
    public static List<Position> getAllShiftPositions (Board board) {
        return Const.POSSIBLE_SHIFT_POSITIONS.stream().
                filter(shift -> !shift.equals(board.getForbidden())).
                collect(Collectors.toList());
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

}
