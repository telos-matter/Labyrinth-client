package hemmouda.maze.game.logic.player.randomPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.logic.player.Player;
import hemmouda.maze.game.logic.util.BoardUtil;
import hemmouda.maze.game.logic.util.MoveMessageUtil;
import hemmouda.maze.game.logic.util.Randomness;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

import java.util.List;

/**
 * A random player. Plays random
 * yet valid moves.
 * Just for testing.
 */
public final class RandomPlayer extends Player {

    private static RandomPlayer instance;

    public static RandomPlayer getInstance() {
        if (instance == null) {
            instance = new RandomPlayer();
        }
        return instance;
    }

    private RandomPlayer () {}

    @Override
    public void initialize() {
        Logger.info("RandomPlayer has been initialized");
    }

    /**
     * Uses the already existing algorithms to find
     * a random valid insertion and a random valid move.
     */
    @Override
    public MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        Position shiftPosition = getRandomShiftPosition(board);
        Card shiftCard = new Card(board.getShiftCard());
        // There is some bug in the server, because this does not work
//        Card.Orientation orientation = getRandomOrientation(shiftCard);
//        BoardUtil.applyShift(board, shiftPosition, orientation);
        // Even with their method, so like it's not something from me
//        BoardUtil.applyShift(board, shiftPosition, Randomness.getRandomElement(shiftCard.getPossibleRotations()));
        BoardUtil.applyShift(board, shiftPosition, shiftCard);
        Position move = getRandomMove(board);

        return MoveMessageUtil.construct(shiftCard, shiftPosition, move);
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {} // Hh, ok

    /**
     * @return a readonly random valid shift position
     */
    private Position getRandomShiftPosition(Board board) {
        Position shift;

        // Keep trying until you get a non forbidden shift
        do {
            shift = Randomness.getRandomElement(Const.POSSIBLE_SHIFT_POSITIONS);
        } while (shift.equals(board.getForbidden())); // Automatically takes care of first turn case

        return shift;
    }

    /**
     * @return a random orientation for the given card
     */
    private Card.Orientation getRandomOrientation (Card card) {
        var possibleRotations = card.getPossibleRotations();
        return Randomness.getRandomElement(possibleRotations).getOrientation();
    }

    /**
     * @return a random valid move newPinPosition
     */
    private Position getRandomMove (Board board) {
        Position playerPosition = board.findPlayer(GameInfo.getPlayerId());
        var positions = board.getAllReachablePositions(playerPosition);
        return Randomness.getRandomElement(positions);
    }

    @Override
    public String toString() {
        return "RandomPlayer";
    }
}
