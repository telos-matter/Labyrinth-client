package hemmouda.maze.game.logic.player.lookAheadPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.logic.player.Player;
import hemmouda.maze.game.logic.player.lookAheadPlayer.core.LightBoard;
import hemmouda.maze.game.logic.util.BoardUtil;
import hemmouda.maze.game.logic.util.MoveMessageUtil;
import hemmouda.maze.game.logic.util.Randomness;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;
import hemmouda.util.structures.Node;
import hemmouda.util.structures.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A player that looks ahead into future, as if
 * it's playing alone, and picks the move
 * that takes him to the treasure
 */
public final class LookAheadPlayer extends Player {

    private static LookAheadPlayer instance;

    public static LookAheadPlayer getInstance() {
        if (instance == null) {
            instance = new LookAheadPlayer();
        }
        return instance;
    }

    private LookAheadPlayer() {}

    @Override
    public void initialize() {
        if (Settings.TURNS_AHEAD < 0) {
            Logger.error("The value for TURNS_AHEAD must be greater than or equal to zero. Value provided: %d", Settings.TURNS_AHEAD);
            throw new IllegalArgumentException("TURNS_AHEAD must be greater than or equal to zero");
        }

        Logger.info("%s has been initialized.", this);
    }

    /**
     * <p>Checks all the possible
     * moves, turn by turn, until it reaches the value
     * specified in {@link Settings#TURNS_AHEAD}.</p>
     *
     * <p>This, obviously, comes with a constraint,
     * which is that the space of possible moves
     * explodes in size. After running the
     * game a couple of times, the average space
     * of possible moves was determined
     * to be around 420 (the actual value I got is
     * 418, but UK). This means that if we try
     * to look, for example, only three turns ahead,
     * then we are evaluating
     * around about 74 million
     * possible moves. Not only does that take noticeable
     * time to evaluate, but it also, in case we are not
     * allowing the objects
     * to be GarbageCollected, does requires
     * quite a bit of memory.</p>
     *
     * <p>So measures and optimizations should be taken
     * to make this operational. Speed is prioritized over space due
     * to the server's response time limit. A key optimization
     * is to generate the moves only once. Since moves
     * are checked level by level, if a level has no valid
     * move, subsequent levels shouldn't need to regenerate
     * previous levels in order to reach theirs.
     * That's it for speed, as for space; the
     * structure
     * {@link hemmouda.maze.game.logic.player.lookAheadPlayer.core.LightBoard}
     * was created to avoid duplicate board copies
     * that only differ in the players position
     * and not in the cards arrangement.</p>
     *
     * <p>Of course more improvements
     * and optimizations could be made,
     * but most treasures
     * are reachable within two turns, and it's
     * rare that it would require three turns. So this
     * implementation
     * should be sufficient and should
     * be runnable on non-beefy machines easily.</p>
     */
    @Override
    protected MoveMessageData think(Board board, Treasure treasure, List<TreasuresToGoData> remainingTreasures) {
        // Where the possible moves from the current board
        // are stored.
        // An initial capacity of 512 should be more than enough.
        // These moves are not checked trough checkLevel because
        // the MoveMessageData is needed.
        var roots = new ArrayList<Pair<MoveMessageData, Node<LightBoard>>>(512);

        // Check all possible moves
        for (var insertion : BoardUtil.getAllInsertions(board)) {
            Board boardAfter = BoardUtil.fakeInsert(board, insertion.second, insertion.first);
            Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, treasure);
            Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());

            for (Position reachablePosition : boardAfter.getAllReachablePositions(playerPosition)) {
                var moveMessageData = MoveMessageUtil.construct(insertion.first, insertion.second, reachablePosition);

                // If we can directly reach the treasure, then that's the move
                if (reachablePosition.equals(treasurePosition)) {
                    Logger.debug("%s found a direct route to the treasure.", this);
                    return moveMessageData;
                }

                // Otherwise store the move and check the next possibility
                var lightBoard = new LightBoard(boardAfter, reachablePosition);
                var root = new Node<>(null, lightBoard);
                var pair = new Pair<>(moveMessageData, root);
                roots.add(pair);
            }
        }

        // Since we got here, there is no direct route
        // to the treasure. So start checking / traversing
        // the game tree horizontally, level by level.
        try {
            int level = 0;
            while (level < Settings.TURNS_AHEAD) {
                boolean lastIteration = level +1 == Settings.TURNS_AHEAD;

                // For every move that we can take
                // check #level deep
                for (var root : roots) {
                    // If it's not the last iteration, then save the generated
                    // moves, because we may need to check a deeper level.
                    if (checkLevel(root.second, level, treasure, !lastIteration)) {
                        Logger.debug("%s can reach the treasure in %d turn(s)", this, level +1);
                        return root.first;
                    }
                }

                level++;
            }
        } catch (OutOfMemoryError | StackOverflowError e) {
            Logger.warning("Buy more RAM.");
        }

        // And if we got here, then there
        // is no possible route to the treasure, even
        // turns ahead. So just return a random move.
        Logger.debug("Even in %d turn(s), the treasure is unreachable. So %s is picking a random move.", Settings.TURNS_AHEAD, this);
        return Randomness.getRandomElement(roots).first;
    }

    /**
     * Checks if the treasure can be reached
     * in the specified level.
     * Level equal to zero means check
     * the given node.
     */
    private boolean checkLevel (Node<LightBoard> root, int level, Treasure treasure, boolean saveMoves) {
        // Base case
        if (level == 0) {
            // There should be no child. Because why this level be checked twice?
            if (!root.children.isEmpty()) {
                throw new AssertionError("How are your already populated?");
            }

            var lightBoard = root.value;
            for (var insertion : BoardUtil.getAllInsertions(lightBoard.getBoard())) {
                var lightBoardAfter = lightBoard.insert(insertion);
                var boardAfter = lightBoardAfter.getBoard();
                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, treasure);
                Position playerPosition = lightBoardAfter.getPlayerPosition();

                var reachablePositions = boardAfter.getAllReachablePositions(playerPosition);
                // Check if the treasure can be reached
                if (treasurePosition != null &&
                        reachablePositions.contains(treasurePosition)) {
                    // If so, just get out of here
                    return true;
                }

                // If it's not reachable, save the moves if
                // they should be saved
                if (saveMoves) {
                    for (var reachablePosition : reachablePositions) {
                        var newLightBoard = new LightBoard(boardAfter, reachablePosition);
                        var newChild = new Node<>(root, newLightBoard);
                        root.children.add(newChild);
                    }
                }
            }

            return false;
        }

        // The previous level should have already been checked
        if (root.children.isEmpty()) {
            throw new AssertionError("How are they empty?");
        }
        // Recursively get the specified
        // level to check it for each child
        for (var child : root.children) {
            if (checkLevel(child, level -1, treasure, saveMoves)) {
                return true;
            }
        }

        // Well, not with the move you gave me.
        return false;
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {} // Hh, ok 3

    @Override
    public String toString() {
        return "LookAheadPlayer (" +Settings.TURNS_AHEAD +")";
    }
}
