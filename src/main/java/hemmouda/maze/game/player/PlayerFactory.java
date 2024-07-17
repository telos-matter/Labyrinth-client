package hemmouda.maze.game.player;

import hemmouda.maze.game.player.inputPlayer.InputPlayer;
import hemmouda.maze.game.player.bestTurnPlayer.BestTurnPlayer;
import hemmouda.maze.game.player.nnPlayer.NnPlayer;
import hemmouda.maze.game.player.randomPlayer.RandomPlayer;
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
                case "BEST_TURN" -> BestTurnPlayer.getInstance();
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
