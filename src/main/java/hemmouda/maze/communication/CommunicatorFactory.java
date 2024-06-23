package hemmouda.maze.communication;

import hemmouda.maze.communication.localCommunicator.LocalCommunicator;
import hemmouda.maze.communication.server.ServerCommunicator;
import hemmouda.maze.settings.Settings;

public final class CommunicatorFactory {

    private static Communicator usedCommunicator;

    /**
     * @return the used Communicator
     */
    public static Communicator getCommunicator () {
        if (usedCommunicator == null) {
            if (Settings.LOCAL_COMMUNICATION) {
                usedCommunicator = LocalCommunicator.getInstance();
            } else {
                usedCommunicator = ServerCommunicator.getInstance();
            }

            usedCommunicator.initialize();
        }

        return usedCommunicator;
    }

}
