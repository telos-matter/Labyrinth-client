package hemmouda.maze.settings;

import java.io.IOException;
import java.util.Properties;

/**
 * The game settings from the config file.
 * Initialized "first". Rather, depends on nothing.
 */
public final class Settings {

    public static final boolean DEBUG;

    public static final String PLAYER_NAME;

    // Initializes the values from the config file.
    // If an error occurred it uses the default values.
    static {
        final String CONFIG_FILE_NAME = "config.properties"; // The config file is expected to be at the root of the resources dir

        // Load
        Properties prop = new Properties();
        try {
            prop.load(Settings.class.getResourceAsStream("/" +CONFIG_FILE_NAME)); // The `/` is platform independent
        } catch (IOException e) {
            System.err.printf("Unable to read the config file `%s` because of `%s`. Will be using default values.%n", CONFIG_FILE_NAME, e.getMessage());
        }

        // Initialize. Better not have any wrong values in the config file
        DEBUG = Boolean.parseBoolean(prop.getProperty("DEBUG", "true"));
        PLAYER_NAME = prop.getProperty("PLAYER_NAME", "HEMMOUDA Aymane");
    }

}
