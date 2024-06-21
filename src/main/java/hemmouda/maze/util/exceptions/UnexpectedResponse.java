package hemmouda.maze.util.exceptions;

import de.fhac.mazenet.server.generated.MazeCom;
import de.fhac.mazenet.server.generated.MazeComMessagetype;

/**
 * For responses you didn't expect
 */
public class UnexpectedResponse extends RuntimeException {

    public UnexpectedResponse (MazeCom received) {
        super("Was not expecting this message: `%s`".formatted(received.getMessagetype()));
    }

    public UnexpectedResponse (MazeCom received, MazeComMessagetype expected) {
        super("Received `%s`. But was expecting `%s`".formatted(received.getMessagetype(), expected));
    }

}
