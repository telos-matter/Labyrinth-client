package hemmouda.maze.communication.server;

import de.fhac.mazenet.server.networking.XmlInputStream;
import de.fhac.mazenet.server.networking.XmlOutputStream;
import hemmouda.maze.communication.Communicator;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;

import java.io.IOException;
import java.net.Socket;

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

    private Socket socket;
    private XmlInputStream in;
    private XmlOutputStream out;

    private ServerCommunicator () {}

    @Override
    public void initialize() {
        try {
            socket = new Socket(Settings.SERVER_ADDRESS, Settings.SERVER_PORT);
            in = new XmlInputStream(socket.getInputStream());
            out = new XmlOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            Logger.error("An error occurred while trying to establish a connection with the server at %s:%d because of `%s`.", Settings.SERVER_ADDRESS.getHostAddress(), Settings.SERVER_PORT, e.getMessage());
            throw new RuntimeException(e);
        }

        Logger.info("ServerCommunicator initialized successfully");
    }

    @Override
    public void beginGame() {
        // TODO impl
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
