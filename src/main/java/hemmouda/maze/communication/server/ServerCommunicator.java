package hemmouda.maze.communication.server;

import de.fhac.mazenet.server.generated.ClientRole;
import de.fhac.mazenet.server.generated.LoginMessageData;
import de.fhac.mazenet.server.generated.MazeCom;
import de.fhac.mazenet.server.generated.MazeComMessagetype;
import de.fhac.mazenet.server.networking.XmlInputStream;
import de.fhac.mazenet.server.networking.XmlOutputStream;
import hemmouda.maze.App;
import hemmouda.maze.communication.Communicator;
import hemmouda.maze.settings.Settings;
import hemmouda.maze.util.Logger;

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
            Logger.error("Unable to establish a connection with the server at %s:%d because of `%s`", Settings.SERVER_ADDRESS.getHostAddress(), Settings.SERVER_PORT, e);
            throw new RuntimeException(e);
        }

        Logger.info("ServerCommunicator initialized successfully");
    }

    @Override
    public void beginGame() {
        login();
    }

    private void send (MazeCom message) {
        try {
            out.write(message);
        } catch (Exception e) {
            Logger.error("Unable to send this message `%s` because of `%s`", message, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Blocks until message received
     */
    private MazeCom receive () {
        try {
            return in.readMazeCom();
        } catch (Exception e) {
            Logger.error("An error occurred while waiting for a message: `%s`", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs into the server
     */
    private void login () {
        LoginMessageData loginMessage = App.OF.createLoginMessageData();
        loginMessage.setName(Settings.PLAYER_NAME);
        loginMessage.setRole(ClientRole.PLAYER);

        MazeCom message = App.OF.createMazeCom();
        message.setLoginMessage(loginMessage);
        message.setMessagetype(MazeComMessagetype.LOGIN);

        send(message);
        var m = receive();
        Logger.info("eyyyy %s", m.getMessagetype());
    }
}
