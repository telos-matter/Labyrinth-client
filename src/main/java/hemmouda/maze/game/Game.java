package hemmouda.maze.game;

import hemmouda.maze.util.Logger;

/**
 * Represents the game from the perspective of
 * this player.
 */
public final class Game {

    private static int playerId;

    public static void initialize () {
        playerId = -1;

        Logger.info("Game initialized successfully");
    }

    public static void setPlayerId (int id) {
        if (playerId != -1) {
            Logger.warning("PlayerId has already been set to %d and now it's being changed to %d", playerId, id); // Who could set it again? IDK
        }
        playerId = id;
    }

    public static int getPlayerId () {
        return playerId;
    }

}
