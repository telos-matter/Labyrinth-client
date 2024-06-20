package hemmouda.maze.util;

import hemmouda.maze.settings.Settings;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/**
 * A simple logger.
 */
public final class Logger {

    private static final java.util.logging.Logger logger;

    static {
        logger = java.util.logging.Logger.getLogger(Logger.class.getName());
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler() {
            @Override
            protected void setOutputStream(java.io.OutputStream out) throws SecurityException {
                super.setOutputStream(System.out);
            }
        };
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());

        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }

    private static void log (Level level, Object any) {
        logger.log(level, String.valueOf(any));
    }

    public static void error (Object any) {
        log(Level.SEVERE, any);
    }

    public static void warning (Object any) {
        log(Level.WARNING, any);
    }

    public static void info (Object any) {
        log(Level.INFO, any);
    }

    public static void debug (Object any) {
        if (Settings.DEBUG) {
            log(Level.FINE, any);
        }
    }

}
