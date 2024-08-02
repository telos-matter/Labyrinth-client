package hemmouda.maze.settings;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

/**
 * The game settings from the config file.
 * Initialized "first". Rather, depends on nothing.
 */
public final class Settings {

    public static final boolean DEBUG;

    public static final Long SEED;

    public static final String PLAYER_NAME;

    public static final boolean LOCAL_COMMUNICATION;

    public static final InetAddress SERVER_ADDRESS;
    public static final int SERVER_PORT;

    public static final String PLAYER;

    public static final int TURNS_AHEAD;

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
        try {
            DEBUG = Boolean.parseBoolean(prop.getProperty("DEBUG", "true"));
            {
                String seedString = prop.getProperty("SEED", "42");
                if (seedString.equalsIgnoreCase("null")) {
                    SEED = null;
                } else {
                    SEED = Long.parseLong(seedString);
                }
            }
            PLAYER_NAME = prop.getProperty("PLAYER_NAME", "HEMMOUDA Aymane");
            LOCAL_COMMUNICATION = Boolean.parseBoolean(prop.getProperty("LOCAL_COMMUNICATION", "false"));
            SERVER_ADDRESS = InetAddress.getByName(prop.getProperty("SERVER_ADDRESS", "127.0.0.1"));
            SERVER_PORT = Integer.parseInt(prop.getProperty("SERVER_PORT", "5123"));
            PLAYER = prop.getProperty("PLAYER", "RANDOM").toUpperCase();
            TURNS_AHEAD = Integer.parseInt(prop.getProperty("TURNS_AHEAD", "1"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
