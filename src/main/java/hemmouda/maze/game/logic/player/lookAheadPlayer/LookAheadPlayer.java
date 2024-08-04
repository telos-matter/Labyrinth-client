package hemmouda.maze.game.logic.player.lookAheadPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Position;
import de.fhac.mazenet.server.generated.MoveInfoData;
import de.fhac.mazenet.server.generated.Treasure;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import hemmouda.maze.game.logic.player.Player;
import hemmouda.maze.game.logic.player.lookAheadPlayer.core.MoveEvaluator;
import hemmouda.maze.game.logic.util.BoardUtil;
import hemmouda.maze.game.logic.util.MoveMessageUtil;
import hemmouda.maze.game.logic.util.Randomness;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Const;
import hemmouda.maze.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A player that plays the "best" move for the current turn
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

        Logger.info("LookAheadPlayer has been initialized, and will look %d turn(s) into the future.", Settings.TURNS_AHEAD);
    }

    /**
     * <p>Obviously, if the treasure is
     * directly reachable in the current turn,
     * then that's the move it would take.</p>
     *
     * <p>The question is what to do when it's not. Trying
     * to get close to a treasure, so that in
     * the next turn we can more easily reach it
     * is not that good of an idea
     * because:
     * <ol>
     * <li>It could get stuck going back and
     * forth between two neighboring cells.</li>
     * <li>One should not forget, that the player
     * can move freely as long as there is a path, so
     * getting closer to the treasure isn't really that
     * useful.</li>
     * </ol>
     * </p>
     *
     * <p>What I thought of as a solution to this problem
     * is to simply go to a random insertion position. That
     * way, in the next turn, we have a lot more
     * flexibility / more reach
     * because we can jump to the other side of the board.</p>
     *
     * <p>ATM, it just picks a random insertion position, it could
     * do with a little improvement to make sure it does
     * not pick the opposite of the forbidden insertion
     * position, but that would not happen usually, so
     * it's fine.</p>
     *
     * <p>Also, in response to "Why not pick the insertion
     * position nearest to the
     * treasure?" Well, because when the treasure is unreachable,
     * you'd want randomness in
     * your decision and not repetition, otherwise, your player
     * could get stuck. More precisely, you want to avoid
     * getting stuck, easiest way to do that, not the optimal
     * tho, is randomness.</p>
     */
    @Override
    protected MoveMessageData think(Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        System.out.println("###########");
        var move = exploreMoves(board, currentTreasure, Settings.TURNS_AHEAD);
        System.out.printf("playing this: %n");
        System.out.printf("insert at: %s and move to: %s%n" ,move.getShiftPosition(), move.getNewPinPos());
        return move;
//        // Where to go if the treasure is unreachable
//        Position alternativeGoal = Randomness.getRandomElement(Const.POSSIBLE_SHIFT_POSITIONS);
//        // The best move to get us to that alternativeGoal
//        MoveEvaluator.MoveRecord bestMove = null;
//
//        // 418 moves on avg
//
//        // For every rotation of the shift card
//        for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
//            // And for every shift position
//            for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
//                // Get the new board, then get both the treasure
//                // position and the player position on it
//                Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
//                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, currentTreasure);
//                Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());
//                // And for every reachable position within that board
//                for (Position position : boardAfter.getAllReachablePositions(playerPosition)) {
//                    // Check if we can reach the treasure
//                    if (position.equals(treasurePosition)) {
//                        // If so, that is the move
//                        Logger.debug("%s found direct route to treasure!", this);
//                        return MoveMessageUtil.construct(card, shiftPosition, position);
//                    }
//
//                    // Otherwise, let MoveEvaluator pick the best move
//                    var moveRecord = MoveEvaluator.constructMoveRecord(card, shiftPosition, position, alternativeGoal);
//                    bestMove = MoveEvaluator.pick(bestMove, moveRecord);
//                }
//            }
//        }
//
//        // If the treasure cannot be reached, return the best move
//        Logger.debug("%s couldn't find direct route to treasure, trying to go to %s instead.", this, alternativeGoal);
//        return bestMove.constructMoveMessageData();
    }

    /**
     * Ahhhhhh
     *
     * So it wasn't working because it explores the moves vertically
     * and not horizontally. So any depth above 1 wouldn't work properly
     * wtf even depth 1 with test board has a prob. eh too much to check why,
     * let's rewrite this
     */
    public MoveMessageData exploreMoves (Board board, Treasure goal, int depth) {
        // Where the boards after each move is stored
        List <Pair<MoveMessageData, Board>> moves = null;
        moves = new ArrayList<>(512); // 418 on avg

        int moveIndex = 0;

        // For every rotation of the shift card
        for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
            // And for every shift position
            for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
                // Get the new board, then get both the treasure
                // position and the player position on it
                Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, goal);
                Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());
                // And for every reachable position within that board
                for (Position position : boardAfter.getAllReachablePositions(playerPosition)) {

                    moveIndex++;

                    var move = MoveMessageUtil.construct(card, shiftPosition, position);

                    // Check if we can reach the treasure
                    if (position.equals(treasurePosition)) {
                        // If so, that is the move
                        System.out.println("found" + depth);
                        System.out.println("Move #" +moveIndex);
                        System.out.printf("insert at: %s and move to: %s%n" ,move.getShiftPosition(), move.getNewPinPos());
                        return move;
                    }

                    // Otherwise, let MoveEvaluator pick the best move
                    moves.add(new Pair<>(move, boardAfter));

                }
            }
        }

        if (Settings.TURNS_AHEAD == 0) {
            System.out.println("Not this");
            return Randomness.getRandomElement(moves).first;
        }

        if (depth == 0) {
            return null;
        }

        for (int i = 0; i < moves.size(); i++) {
            var move = moves.get(i);
            if (exploreMoves(move.second, goal, depth -1) != null) {
                System.out.println("smth?" + depth);
                System.out.println("Space: " +moves.size());
                System.out.println("Move #" +(i+1));
                System.out.printf("insert at: %s and move to: %s%n" ,move.first.getShiftPosition(), move.first.getNewPinPos());
                System.out.println("treasure will be at: " + BoardUtil.getTreasurePosition(move.second, goal));
                return move.first;
            }
        }

        if (Settings.TURNS_AHEAD == depth) {
            System.out.println("nothing?" + depth);
            return Randomness.getRandomElement(moves).first;
        } else {
            return null;
        }
    }

    public static class Pair <F, S> {
        public F first;
        public S second;

        public Pair (F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {} // Hh, ok 3

    @Override
    public String toString() {
        return "LookAheadPlayer (" +Settings.TURNS_AHEAD +")";
    }
}
