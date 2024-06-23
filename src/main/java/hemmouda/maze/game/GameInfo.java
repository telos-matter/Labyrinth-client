package hemmouda.maze.game;

import hemmouda.maze.util.Logger;

/**
 * A record for the current information about the
 * game. And for the player.
 */
public final class GameInfo {

    private static int playerId;

    private static GameStatus status;
    /**
     * How many turns have been played
     */
    private static int turnsCount;

    public static void initialize () {
        playerId = -1;
        status = GameStatus.PREPARING;
        turnsCount = 0;

        Logger.debug("GameInfo has been initialized");
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

    /**
     * Lemme know when the game starts
     */
    public static void gameStarted () {
        status = GameStatus.IN_PROGRESS;
        Logger.info("Game started");
    }

    public static GameStatus getStatus () {
        return status;
    }

    /**
     * Lemme know when it's a new turn
     */
    public static void newTurn () {
        turnsCount++;
        Logger.info("It's the player's turn. Turn #%d", turnsCount);
    }

    public static int getTurnsCount () {
        return turnsCount;
    }

}
