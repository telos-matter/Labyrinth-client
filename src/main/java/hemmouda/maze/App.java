package hemmouda.maze;

import de.fhac.mazenet.server.generated.ObjectFactory;
import hemmouda.maze.communication.Communicator;
import hemmouda.maze.game.Game;
import hemmouda.maze.util.Logger;

/**
 * Let make it clear from the start:
 * This app will be designed and developed
 * with the requirement in mind that
 * it will only ever run 1 single player at
 * a time. So there will be a lot
 * of singleton in here.
 * And all the parts are aware
 * and take into their design
 * that an instance of this App
 * will only be running 1 player.
 * One player. Not a manager.
 */
public interface App {

    /**
     * An OF for the whole app :)
     */
    public static final ObjectFactory OF = new ObjectFactory();

    public static void main(String[] args) {
        Logger.info("Starting the application");

        Game.initialize();

        Communicator communicator = Communicator.getCommunicator();
        communicator.initialize();
        communicator.beginGame();

        Logger.info("Ending the application");
    }

}
