package hemmouda.maze.game.logic.player.util;

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
     * from the list, or <code>null</code>
     * if the list is empty
     */
    public static <T> T getRandomElement (List<T> list) {
        if (list.isEmpty()) {
            return null;
        }

        return list.get(RAND.nextInt(list.size()));
    }

}
