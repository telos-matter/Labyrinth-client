package hemmouda.maze.game.logic.player.lookaheadplayer.core;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import hemmouda.maze.game.logic.util.BoardUtil;
import hemmouda.util.structures.Pair;

/**
 * Used to reference a
 * {@link de.fhac.mazenet.server.game.Board}
 * but it keeps track of the player
 * position. The player position on
 * the board object is almost always incorrect.
 * This is used
 * to cut down on the space used,
 * because with the normal board object, it's
 * the entire board data for
 * a single player position. So two
 * different player positions, that
 * are on the same board arrangement, still
 * require two different board objects.
 */
public class LightBoard {

    private Board board;
    private Position playerPosition;

    public LightBoard (Board board, Position playerPosition) {
        this.board = board;
        this.playerPosition = playerPosition;
    }

    public Board getBoard () {
        return board;
    }

    public Position getPlayerPosition () {
        return playerPosition;
    }

    /**
     * @return a new LightBoard that is the result
     * of the given insertion on this LightBoard. So if
     * the player was to be shifted, then the new LightBoard
     * will have the correct playerPosition value.
     */
    public LightBoard insert (Pair <Card, Position> insertion) {
        // For ease of referencing
        Position shiftPos = insertion.second;
        // Get the new board arrangement
        Board newBoard = BoardUtil.fakeInsert(board, shiftPos, insertion.first);
        Position newPos = new Position(playerPosition);
        // Check if the player is even affected
        // If it's a top or bottom shift, check if it's the same col
        if ((shiftPos.getRow() == 0 || shiftPos.getRow() == 6) &&
                shiftPos.getCol() == playerPosition.getCol()) {

            // A top shift
            if (shiftPos.getRow() == 0) {
                newPos.setRow((newPos.getRow() +1) % 7);

            // A bottom shift
            } else {
                int newRow = newPos.getRow() -1;
                newPos.setRow((newRow == -1)? 6 : newRow);
            }

        // If it's right or left shift, check if it's the same row
        } else if ((shiftPos.getCol() == 0 || shiftPos.getCol() == 6) &&
                shiftPos.getRow() == playerPosition.getRow()) {

            // A left shift
            if (shiftPos.getCol() == 0) {
                newPos.setCol((newPos.getCol() +1) % 7);

            // A right shift
            } else {
                int newCol = newPos.getCol() -1;
                newPos.setCol((newCol == -1)? 6 : newCol);
            }
        }

        return new LightBoard(newBoard, newPos);
    }

}
