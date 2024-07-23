package hemmouda.maze.game.logic.util;

import hemmouda.maze.settings.Settings;

import java.util.List;
import java.util.Random;

/**
 * A randomness utility class
 */
public class Randomness {

    public static final Random RAND = (Settings.SEED == null)? new Random() : new Random(Settings.SEED);

    /**
     * Returns a random element
     * from a non-empty list.
     */
    public static <T> T getRandomElement (List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }

        return list.get(RAND.nextInt(list.size()));
    }

}
