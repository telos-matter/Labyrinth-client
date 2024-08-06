package hemmouda.maze.game.logic.player.lookAheadPlayer;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.game.Card;
import de.fhac.mazenet.server.game.Game;
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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    protected MoveMessageData think(Board board, Treasure treasure, List<TreasuresToGoData> remainingTreasures) {
        List <Pair<MoveMessageData, Board>> moves = new ArrayList<>(512); // 418 on avg
        // array list and the add all not gucci

        // TODO optimize. Speed over space

        // For every rotation of the shift card
        for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
            // And for every shift position
            for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
                // Get the new board, then get both the treasure
                // position and the player position on it
                Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, treasure);
                Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());

                var reach = boardAfter.getAllReachablePositions(playerPosition);
                if (reach.contains(treasurePosition)) {
                    System.out.println("Found a direct move");
                    return MoveMessageUtil.construct(card, shiftPosition, treasurePosition);
                }

                var f = reach.stream().
                        map(
                                position -> MoveMessageUtil.construct(card, shiftPosition, position)
                        ).
                        map(
                                moveMessageData -> new Pair<MoveMessageData, Board>(moveMessageData, boardAfter)
                        ).toList();
                moves.addAll(f);

            }
        }


        List<Pair<MoveMessageData, Node<Board>>> roots = new ArrayList<>(moves.stream().
                map(pair -> {
                    var b = (Board) board.clone();
                    b.proceedTurn(pair.first, GameInfo.getPlayerId());
                    var root = new Node<Board>(null, b);
                    return new Pair <MoveMessageData, Node<Board>> (pair.first, root);
                }).toList());


        // TODO add catch for outofmemeory and stackoverflow
        int depth = 0;
        // explore each level
        while (depth < Settings.TURNS_AHEAD) {
            for (var root : roots) {
                if (exploreDepth(root.second, depth, treasure, depth +1 < Settings.TURNS_AHEAD)) {
                    System.out.println("Can reach in " +(depth +1) +" turns");
                    return root.first;
                }
            }

            depth++;
        }

        System.out.println("Found nothing at all, playing random");
        return Randomness.getRandomElement(moves).first;

    }

    // fuckin hell. I have to have a new board for every move and
    // not just keep track of my position because the shifts
    // can move you.
    // Optimize later and decouple the two. Have a struct
    // that references just the board, and another one that
    // keeps track of the player position

    private boolean exploreDepth (Node<Board> root, int depth, Treasure treasure, boolean save) {
        if (depth == 0) {
            if (!root.children.isEmpty()) {
                throw new IllegalStateException("How are they already populated?");
            }
            var board = root.value;
            for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
                for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
                    Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
                    Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, treasure);
                    Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());
                    var reach = boardAfter.getAllReachablePositions(playerPosition);
                    if (reach.contains(treasurePosition)) {
                        return true;
                    }
                    if (save) {
                        var l = reach.stream().
                                map(
                                        position -> MoveMessageUtil.construct(card, shiftPosition, position)
                                ).
                                map(
                                        moveMessageData -> {
                                            var clone = (Board) board.clone();
                                            clone.proceedTurn(moveMessageData, GameInfo.getPlayerId());
                                            return clone;
                                        }
                                ).
                                map(
                                        newBoard -> new Node <Board> (root, newBoard)
                                ).
                                toList();
                        root.children.addAll(l);
                    }
                }
            }

            return false;
        }

        if (root.children.isEmpty()) {
            throw new IllegalStateException("How are they empty?");
        }
        for (var node : root.children) {
            if (exploreDepth(node, depth -1, treasure, save)) {
                return true;
            }
        }
        return false;
//        throw new RuntimeException("Unreachable");
    }



//        System.out.println("###########" + GameInfo.getTurnsCount());
//        var move = exploreMoves(board, currentTreasure, Settings.TURNS_AHEAD);
//        System.out.printf("playing this: %n");
//        System.out.printf("insert at: %s and move to: %s%n" ,move.getShiftPosition(), move.getNewPinPos());
//        return move;
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

    /**
     * Ahhhhhh
     *
     * So it wasn't working because it explores the moves vertically
     * and not horizontally. So any depth above 1 wouldn't work properly
     * wtf even depth 1 with test board has a prob. eh too much to check why,
     * let's rewrite this
     */
//    public MoveMessageData exploreMoves (Board board, Treasure goal, int depth) {
//        // Where the boards after each move is stored
//        List <Pair<MoveMessageData, Board>> moves = null;
//        moves = new ArrayList<>(512); // 418 on avg
//
//        int moveIndex = 0;
//
//        // For every rotation of the shift card
//        for (Card card : new Card(board.getShiftCard()).getPossibleRotations()) {
//            // And for every shift position
//            for (Position shiftPosition : BoardUtil.getAllShiftPositions(board)) {
//                // Get the new board, then get both the treasure
//                // position and the player position on it
//                Board boardAfter = BoardUtil.fakeInsert(board, shiftPosition, card);
//                Position treasurePosition = BoardUtil.getTreasurePosition(boardAfter, goal);
//                Position playerPosition = boardAfter.findPlayer(GameInfo.getPlayerId());
//
//                // no im retarded, why did i expect it to somehow know where it shoud move
////                if (GameInfo.getTurnsCount() == 5 && depth == 0 && boardAfter.getAllReachablePositions(playerPosition).contains(treasurePosition)) {
////                    System.out.println("Right here");
////                    var all = boardAfter.getAllReachablePositions(playerPosition);
////                    System.out.println(all.size());
////                    System.out.println(all.contains(treasurePosition));
////                    System.out.println(playerPosition); // this shit right here. shifting does not update the player
////                    var op = card.getOpenings();
////                    System.out.printf("t: %s, r: %s, b: %s, l: %s%n", op.isTop(), op.isRight(), op.isBottom(), op.isLeft());
////                }
//
//
//                // And for every reachable position within that board
//                for (Position position : boardAfter.getAllReachablePositions(playerPosition)) {
//
//                    moveIndex++;
//
//
//                    var move = MoveMessageUtil.construct(card, shiftPosition, position);
//
//                    // Check if we can reach the treasure
//                    if (position.equals(treasurePosition)) {
//                        // If so, that is the move
//                        System.out.println("found" + depth);
//                        System.out.println("Move #" +moveIndex);
//                        System.out.printf("insert at: %s and move to: %s%n" ,move.getShiftPosition(), move.getNewPinPos());
//                        return move;
//                    }
//
//                    // Otherwise, let MoveEvaluator pick the best move
//                    moves.add(new Pair<>(move, boardAfter));
//
//                }
//            }
//        }
//
//
//
//        if (depth == 0) {
//            return null;
//        }
//
//        for (int i = 0; i < moves.size(); i++) {
//            var move = moves.get(i);
//            if (exploreMoves(move.second, goal, depth -1) != null) {
//                System.out.println("smth?" + depth);
//                System.out.println("Space: " +moves.size());
//                System.out.println("Move #" +(i+1));
//                System.out.printf("insert at: %s and move to: %s%n" ,move.first.getShiftPosition(), move.first.getNewPinPos());
//                System.out.println("treasure will be at: " + BoardUtil.getTreasurePosition(move.second, goal));
//                return move.first;
//            }
//        }
//
//        if (Settings.TURNS_AHEAD == depth) {
//            System.out.println("nothing?" + depth);
//            return Randomness.getRandomElement(moves).first;
//        } else {
//            return null;
//        }
//    }

    public static class Pair <F, S> {
        public F first;
        public S second;

        public Pair (F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class Node <T> {
        public Node <T> parent = null;
        public T value;
        public List <Node <T>> children = new LinkedList<>();

        public Node (Node<T> parent, T value) {
            this.parent = parent;
            this.value = value;
        }
    }

    @Override
    public void otherPlayerMove(MoveInfoData moveInfo) {} // Hh, ok 3

    @Override
    public String toString() {
        return "LookAheadPlayer (" +Settings.TURNS_AHEAD +")";
    }
}
