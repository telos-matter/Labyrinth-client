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
import hemmouda.maze.util.exceptions.UnexpectedResponse;

import java.net.Socket;

/**
 * Communicates with the actual server over TCP.
 *
 * The communication is not error tolerant, and
 * is not designed to be able to recover from one. I mean
 * we are not communicating with a space shuttle.
 * Plus if an error occurred and I get disconnected I
 * automatically lose the game, so.
 *
 * Also, the communication will be happening on the
 * main thread so if an error occurred it'll stop
 * the application.
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
            Logger.error("Unable to send MazeCom message of type `%s` because of `%s`", message.getMessagetype(), e);
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
     * Logs into the server.
     * Only login when the game already starts
     * on the server big man.
     */
    private void login () {
        LoginMessageData loginMessage = App.OF.createLoginMessageData();
        loginMessage.setName(Settings.PLAYER_NAME);
        loginMessage.setRole(ClientRole.PLAYER);

        MazeCom message = App.OF.createMazeCom();
        message.setLoginMessage(loginMessage);
        message.setMessagetype(MazeComMessagetype.LOGIN);

        send(message);
        MazeCom response = receive();

        // If it's anything other than LoginReply, probably Disconnect
        // then it's most likely because the game has yet to start
        if (!response.getMessagetype().equals(MazeComMessagetype.LOGINREPLY)) {
            Logger.error("Unable to log into the game. Probably because the game hasn't started yet.");
            throw new UnexpectedResponse(response, MazeComMessagetype.LOGINREPLY);
        }

        // TODO continue implementing
    }
}
