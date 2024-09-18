package hemmouda.maze.game.logic.player;

import hemmouda.maze.game.logic.player.inputplayer.InputPlayer;
import hemmouda.maze.game.logic.player.lookaheadplayer.LookAheadPlayer;
import hemmouda.maze.game.logic.player.nnPlayer.NnPlayer;
import hemmouda.maze.game.logic.player.randomPlayer.RandomPlayer;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;

public final class PlayerFactory {

    private static Player usedPlayer;

    /**
     * @return the used Player
     */
    public static Player getPlayer () {
        if (usedPlayer == null) {
            usedPlayer = switch (Settings.PLAYER) {
                case "RANDOM" -> RandomPlayer.getInstance();
                case "INPUT" -> InputPlayer.getInstance();
                case "LOOK_AHEAD" -> LookAheadPlayer.getInstance();
                case "NN_PLAYER" -> NnPlayer.getInstance();
                default -> {
                    Logger.error("Unknown player type `%s`", Settings.PLAYER);
                    throw new IllegalArgumentException("Unknown player type `%s`".formatted(Settings.PLAYER));
                }
            };

            usedPlayer.initialize();
        }

        return usedPlayer;
    }
}
