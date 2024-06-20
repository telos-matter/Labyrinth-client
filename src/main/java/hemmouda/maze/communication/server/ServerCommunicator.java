package hemmouda.maze.communication.server;

import hemmouda.maze.communication.Communicator;

/**
 * Communicates with the actual server over TCP
 */
public final class ServerCommunicator implements Communicator {

    private static ServerCommunicator instance;

    public static ServerCommunicator getInstance() {
        if (instance == null) {
            instance = new ServerCommunicator();
        }
        return instance;
    }

    private ServerCommunicator () {}

    @Override
    public void initialize() {
        // TODO impl
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void beginGame() {
        // TODO impl
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
