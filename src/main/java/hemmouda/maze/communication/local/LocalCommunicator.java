package hemmouda.maze.communication.local;

import hemmouda.maze.communication.Communicator;

/**
 * Communicates internally within this instance of the application.
 * Useful in case I want to train a NN to have fast communication.
 */
public class LocalCommunicator implements Communicator {

    private static LocalCommunicator instance;

    public static LocalCommunicator getInstance() {
        if (instance == null) {
            instance = new LocalCommunicator();
        }
        return instance;
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void beginGame() {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
