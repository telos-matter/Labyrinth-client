package hemmouda.maze.game.player.maxNPlayer.maxNImpl;

import de.fhac.mazenet.server.game.Board;
import de.fhac.mazenet.server.generated.MoveMessageData;
import de.fhac.mazenet.server.generated.TreasuresToGoData;
import hemmouda.maze.game.GameInfo;
import telosmatter.maxnj.GameState;

import java.util.Collection;
import java.util.List;

/**
 * {@link telosmatter.maxnj.GameState} implementation
 */
public class BoardState implements GameState <Integer, MoveMessageData, BoardState> {

    public BoardState (Board board, Treasure currentTreasure, List<TreasuresToGoData> remainingTreasures) {
        // TODO
        // wait fck, what m i gonna do with currentTreasure?
    }

    @Override
    public Collection<Integer> getPlayers() {
        return GameInfo.getPlayersIds();
    }

    @Override
    public Integer getPlayer() {
        return null;
    }

    @Override
    public Collection<MoveMessageData> getPossibleMoves() {
        return null;
    }

    @Override
    public BoardState playMove(MoveMessageData moveMessageData) {
        return null;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

}
