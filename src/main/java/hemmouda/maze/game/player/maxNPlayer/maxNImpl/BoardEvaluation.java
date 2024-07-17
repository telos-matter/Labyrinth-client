package hemmouda.maze.game.player.maxNPlayer.maxNImpl;

import de.fhac.mazenet.server.generated.MoveMessageData;
import telosmatter.maxnj.Evaluation;

/**
 * The evaluation function
 */
public class BoardEvaluation {

    public static final Evaluation <Integer, MoveMessageData, BoardState> evaluationFunction = new Evaluation<>() {
        /**
         * Evaluation works as follows:
         * Won -> +inf
         * Lost -> -inf
         * Otherwise -> (200 * #treasures_found) + inverse_distance_to_next_treasure
         * What I mean with the latter is that the closer you
         * are to a treasure the better, the further, the worse
         */
        @Override
        public double evaluate(BoardState boardState, Integer gamePlayer) {
            // TODO impl
            return 0;
        }
    };

}
