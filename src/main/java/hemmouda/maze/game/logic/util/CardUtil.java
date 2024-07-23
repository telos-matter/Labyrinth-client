package hemmouda.maze.game.logic.util;

import de.fhac.mazenet.server.game.Card;

/**
 * A utility class for the card
 */
public class CardUtil {

    /**
     * @return the given card rotated to the given orientation
     */
    public static Card rotate (Card card, Card.Orientation orientation) {
        return new Card(card.getShape(), orientation, card.getTreasure());
    }

}
