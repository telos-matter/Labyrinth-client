package hemmouda.maze.game.player.randomPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.AwaitMoveMessageData;
import de.fhac.mazenet.server.generated.MoveMessageData;
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
     * a valid insertion and a valid move.
     */
    @Override
    public MoveMessageData getMove(AwaitMoveMessageData message) {
        Logger.debug("RandomPlayer started \"thinking\"");
        final long startTime = System.nanoTime();

        Board board = (Board) new Board(message.getBoard()).clone(); // As to not modify the original board
        Position shift = getRandomShift(board);
        applyShift(board, shift);
        Position move = getRandomMove(board);
        MoveMessageData response = constructMoveMessage(message.getBoard().getShiftCard(), shift, move);

        final long finishTime = System.nanoTime();
        final long durationMilli = (finishTime - startTime) / 1_000_000L;
        final float durationSec = durationMilli / 1_000F;
        Logger.debug("RandomPlayer finished \"thinking\". Took %f seconds", durationSec);

        return response;
    }

    /**
     * @return a random valid shift position
     */
    private Position getRandomShift (Board board) {
        var ROWS = Const.POSSIBLE_SHIFT_ROWS;
        var COLS = Const.POSSIBLE_SHIFT_COLS;

        Position shift = new Position();

        // Keep trying until you get a non forbidden shift
        do {
            int row = ROWS.get(rand.nextInt(ROWS.size()));
            int col = COLS.get(rand.nextInt(COLS.size()));
            shift.setRow(row);
            shift.setCol(col);

        } while (shift.equals(board.getForbidden())); // Automatically takes care of first turn case

        return shift;
    }

    /**
     * Applies the shift to the board
     */
    private void applyShift(Board board, Position shift) {
        // Normally, setting just
        // the shiftPosition in MoveMessageData
        // should be fine as that it uses
        // nothing else.
        MoveMessageData moveMessage = App.OF.createMoveMessageData();
        moveMessage.setShiftPosition(shift);
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
