package hemmouda.maze.game.player.randomPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.App;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.player.Player;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

import java.util.List;
import java.util.Random;

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

    private Random rand;

    private RandomPlayer () {}

    @Override
    public void initialize() {
        if (Settings.SEED == null) {
            rand = new Random();
        } else {
            rand = new Random(Settings.SEED);
        }

        Logger.info("RandomPlayer has been initialized");
    }

    /**
     * Uses the already existing algorithms to find
     * a random valid insertion and a random valid move.
     */
    @Override
    public MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        Position shiftPosition = getRandomShiftPosition(board);
        Card shiftCard = new Card(board.getShiftCard()); // FIXME Could lead to error because it could still contain a pin. Maybe?
        applyShift(board, shiftPosition, shiftCard); // This does not modify the shiftCard data, rather changes the reference
        Position move = getRandomMove(board);

        return constructMoveMessage(shiftCard, shiftPosition, move);
    }

    /**
     * @return a readonly random valid shift position
     */
    private Position getRandomShiftPosition(Board board) {
        final var SHIFTS = Const.POSSIBLE_SHIFT_POSITIONS;

        Position shift;

        // Keep trying until you get a non forbidden shift
        do {
            shift = SHIFTS.get(rand.nextInt(SHIFTS.size()));
        } while (shift.equals(board.getForbidden())); // Automatically takes care of first turn case

        return shift;
    }

    /**
     * Applies the shift to the board
     */
    private void applyShift(Board board, Position shift, Card shiftCard) {
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftPosition(shift);
        moveMessage.setShiftCard(shiftCard);
        board.proceedShift(moveMessage);
    }

    /**
     * @return a random valid move position
     */
    private Position getRandomMove (Board board) {
        Position playerPosition = board.findPlayer(GameInfo.getPlayerId());
        var positions = board.getAllReachablePositions(playerPosition);
        return positions.get(rand.nextInt(positions.size()));
    }

    @Override
    public String toString() {
        return "RandomPlayer";
    }
}
