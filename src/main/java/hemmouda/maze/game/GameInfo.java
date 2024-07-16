package hemmouda.maze.game;

import hemmouda.maze.util.Logger;

/**
 * A record for the current information about the
 * game. And for the player.
 */
public final class GameInfo {

    private static Integer playerId;

    private static GameStatus status;
    /**
     * How many turns have been played
     */
    private static int turnsCount;

    /**
     * How many players are there.
     */
    private static int playersCount;

    private static Integer winnerId;

    public static void initialize () {
        playerId = null;
        status = GameStatus.PREPARING;
        turnsCount = 0;
        playersCount = -1;
        winnerId = null;

        Logger.debug("GameInfo has been initialized");
    }

    public static void setPlayerId (int id) {
        if (playerId != null) {
            Logger.error("PlayerId has already been set to %d. Can't change it.", playerId); // Who could set it again? IDK
            throw new IllegalStateException("PlayerId has already been set.");
        }
        playerId = id;
    }

    public static int getPlayerId () {
        return playerId;
    }

    /**
     * @return has the playersCount been set or not
     */
    public static boolean isPlayersCountSet () {
        return playersCount != -1;
    }

    public static void setPlayersCount (int count) {
        if (playersCount != -1) {
            Logger.error("PlayersCount has already been set to %d. Can't change it.", playersCount);
            throw new IllegalStateException("PlayersCount has already been set.");
        }
        playersCount = count;
    }

    public static int getPlayersCount () {
        return playersCount;
    }

    /**
     * Lemme know when the game starts
     */
    public static void gameStarted () {
        status = GameStatus.IN_PROGRESS;
        Logger.info("Game started");
    }

    /**
     * Lemme when the game is over and who
     * won
     */
    public static void gameWon (int winnerId) {
        status = GameStatus.COMPLETED;
        GameInfo.winnerId = winnerId;
        if (winnerId == playerId) {
            Logger.info("Game finished. You won!");
        } else {
            Logger.info("Game finished. Player #%d won", winnerId);
        }
    }

    public static void gameEndedAbruptly () {
        status = GameStatus.ABRUPTLY_ENDED;
        Logger.info("Game ended abruptly");
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

    public static int getWinnerId () {
        if (status != GameStatus.COMPLETED) {
            throw new IllegalStateException("Game is not yet over or there is no winner");
        }

        return winnerId;
    }

}
