package hemmouda.maze.game.player.randomPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.CardData;
import de.fhac.mazenet.server.generated.MoveMessageData;
import hemmouda.maze.App;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.player.Player;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

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
     * a valid insertion and a valid move.
     */
    @Override
    public MoveMessageData getMove(AwaitMoveMessageData message) {
        Logger.debug("RandomPlayer started \"thinking\"");
        final long startTime = System.nanoTime();

        Board board = (Board) new Board(message.getBoard()).clone(); // As to not modify the original board
        Position shiftPosition = getRandomShiftPosition(board);
        CardData shiftCard = message.getBoard().getShiftCard(); // FIXME Could lead to error because it could still contain a pin
        applyShift(board, shiftPosition, shiftCard);
        Position move = getRandomMove(board);
        MoveMessageData response = constructMoveMessage(message.getBoard().getShiftCard(), shiftPosition, move);

        final long finishTime = System.nanoTime();
        final long durationMilli = (finishTime - startTime) / 1_000_000L;
        final float durationSec = durationMilli / 1_000F; // TODO i want just 0.000 and thats it not 0.00100
        Logger.debug("RandomPlayer finished \"thinking\". Took %f seconds", durationSec);

        reportMove(new Card(shiftCard), shiftPosition, board.findPlayer(GameInfo.getPlayerId()), move);
        return response;
    }

    /**
     * @return a random valid shift position
     */
    private Position getRandomShiftPosition(Board board) {
        final var SHIFTS = Const.POSSIBLE_SHIFT_POSITIONS;

        Position shift = new Position();

        // Keep trying until you get a non forbidden shift
        do {
            var possible = SHIFTS.get(rand.nextInt(SHIFTS.size()));
            shift.setRow(possible.getRow());
            shift.setCol(possible.getCol());

        } while (shift.equals(board.getForbidden())); // Automatically takes care of first turn case

        return shift;
    }

    /**
     * Applies the shift to the board
     */
    private void applyShift(Board board, Position shift, CardData shiftCard) {
        // Normally, setting just
        // the shiftPosition in MoveMessageData
        // should be fine as that it uses
        // nothing else.
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

}
