package hemmouda.maze.game.player.inputPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.player.Player;
import hemmouda.maze.game.player.util.BoardUtil;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

/**
 * YOU are the player.
 */
public final class InputPlayer extends Player {

    private static InputPlayer instance;

    public static InputPlayer getInstance() {
        if (instance == null) {
            instance = new InputPlayer();
        }
        return instance;
    }

    /**
     * A Pattern for to read
     * a position in the form
     * of `ROW COL`.
     * Max of 1 digit
     */
    private static final Pattern POSITION_PATTERN = Pattern.compile("^(\\d)\\s+(\\d)$");
    /**
     * A Pattern for the orientation
     * 0, 90, 180, or 270
     */
    private static final Pattern ORIENTATION_PATTERN = Pattern.compile("^(\\d){1,3}$");

    /**
     * From where to contact the player.
     * Usually it's the console
     */
    private BufferedReader in;
    private PrintWriter out;

    private InputPlayer () {}

    @Override
    public void initialize() {
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(System.out);

        Logger.info("InputPlayer has been initialized");
    }

    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        // Show current position
        var pinPosition = board.findPlayer(GameInfo.getPlayerId());
        showPinPosition(pinPosition);
        // Show current treasure that needs to be found
        var treasurePositionData =  board.findTreasure(currentTreasure);
        var treasurePosition = treasurePositionData == null? null : new Position(treasurePositionData);
        showTreasure(currentTreasure, treasurePosition);
        // Show current shift card
        var shiftCard = new Card(board.getShiftCard());
        showShiftCard(shiftCard);
        // Ask where to insert the shift card
        Position shiftPosition = promptForShiftPosition(board);
        // Ask in which way to rotate the shift card
        Card.Orientation orientation = promptForOrientation();
        // Apply the shift
        BoardUtil.applyShift(board, shiftPosition, orientation);
        // Check if the shift moved the player
        {
            var newPosition = board.findPlayer(GameInfo.getPlayerId());
            if (!newPosition.equals(pinPosition)) {
                pinPosition = newPosition;
                write("Your position after shifting will be %s".formatted(newPosition));
            }
        }
        // Ask for the new position
        var newPinPosition = promptForMove(board, pinPosition);

        return constructMoveMessage(BoardUtil.rotate(shiftCard, orientation), shiftPosition, newPinPosition);
    }

    private void write (String message) {
        out.println(message);
        out.flush();
    }

    private String read () {
        try {
            return in.readLine();
        } catch (Exception e) {
            Logger.error("Couldn't read your input");
            throw new RuntimeException("Unable to read the user input because of %s".formatted(e));
        }
    }

    private void showPinPosition(Position pinPosition) {
        write("You are at %s".formatted(pinPosition));
    }

    /**
     * @param treasurePosition if null then the treasure is on the shift card
     */
    private void showTreasure (Treasure treasure, Position treasurePosition) {
        if (treasurePosition == null) {
            write("The treasure %s, which you need to reach, is on your shift card". formatted(treasure));
        } else {
            write("You need to reach the treasure %s at %s".formatted(treasure, treasurePosition));
        }
    }

    private void showShiftCard(Card shiftCard) {
        write("Your card this turn is %s".formatted(shiftCard.getShape()));
    }

    private Position promptForShiftPosition(Board board) {
        while (true) {
            write("Where would you like to insert the card? ROW COL");
            var input = read();
            var position = parsePosition(input);
            if (position == null) {
                write("Invalid input format: `%s`".formatted(input));
                continue;
            }
            if (!Const.POSSIBLE_SHIFT_POSITIONS.contains(position)) {
                write("This shift position %s is invalid".formatted(position));
                continue;
            }
            if (position.equals(board.getForbidden())) {
                write("Can't insert the card here because it was just taken out of there!");
                continue;
            }

            return position;
        }
    }

    private Card.Orientation promptForOrientation() {
        while (true) {
            write("In which rotation would you like to insert the card? 0, 90, 180, or 270?");
            var input = read();
            var orientation = parseOrientation(input);
            if (orientation == null) {
                write("Invalid input: `%s`".formatted(input));
                continue;
            }

            return orientation;
        }
    }

    private Position promptForMove(Board board, Position pinPosition) {
        while (true) {
            write("To where would you like to move? ROW COL");
            var input = read();
            var position = parsePosition(input);
            if (position == null) {
                write("Invalid input format: `%s`".formatted(input));
                continue;
            }
            if (!board.getAllReachablePositions(pinPosition).contains(position)) {
                write("You cannot reach %s from where you are at %s".formatted(position, pinPosition));
                continue;
            }

            return position;
        }
    }

    /**
     * @return the parsed Position
     * or null if unable to
     */
    private Position parsePosition (String input) {
        if (input == null) {
            return null;
        }

        var matcher = POSITION_PATTERN.matcher(input);
        if (matcher.find()) {
            try {
                var row = Integer.parseInt(matcher.group(1));
                var col = Integer.parseInt(matcher.group(2));
                return new Position(row, col);
            } catch (NumberFormatException ignored) {}
        }

        return null;
    }

    /**
     * @return the parsed Orientation
     * or null if unable to
     */
    private Card.Orientation parseOrientation (String input) {
        if (input == null) {
            return null;
        }

        var matcher = ORIENTATION_PATTERN.matcher(input);
        if (matcher.find()) {
            try {
                var value = Integer.parseInt((matcher.group(1)));
                return Card.Orientation.fromValue(value);
            } catch (IllegalArgumentException ignored) {}
        }

        return null;
    }

    @Override
    public String toString() {
        return "InputPlayer";
    }

}
