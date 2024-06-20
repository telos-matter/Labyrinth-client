package hemmouda.maze.communication;

import hemmouda.maze.communication.local.LocalCommunicator;
import hemmouda.maze.communication.server.ServerCommunicator;
import hemmouda.maze.settings.Settings;

/**
 * An interface to abstract away the relation
 * between the communication and the player.
 * A communicator will ask the current player
 * for a move and communicate it back to
 * the receiving end, which in most cases
 * will be the server. (I may implement
 * the game logic here if end up
 * implementing a NN so that it can
 * be train fast and not over TCP. If I
 * do then
 * the receiving end will be that part
 * of the application and not the server)
 */
public interface Communicator {

    /**
     * To initialize the communicator.
     */
    public abstract void initialize();

    /**
     * We are ready to begin the game.
     */
    public abstract void beginGame();

    /**
     * @return the used Communicator
     */
    public static Communicator getCommunicator () {
        if (Settings.LOCAL_COMMUNICATION) {
            return LocalCommunicator.getInstance();
        } else {
            return ServerCommunicator.getInstance();
        }
    }

}
