package hemmouda.maze.util;

import hemmouda.maze.settings.Settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

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
        consoleHandler.setFormatter(new Formatter() {
            private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss a");

            // Mainly copied from SimpleFormatter
            @Override
            public String format(LogRecord record) {
                ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
                String formattedZdt = zdt.format(DATE_TIME_FORMATTER);

                String message = formatMessage(record);

                String throwable = "";
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    pw.println();
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    throwable = sw.toString();
                }

                return "[%s] %s: %s%n%s".formatted(
                        formattedZdt,
                        record.getLevel().getLocalizedName(),
                        message,
                        throwable);
            }
        });

        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }

    private static void log (Level level, Object any) {
        logger.log(level, String.valueOf(any));
    }

    private static void log (Level level, String format, Object ... objects) {
        log(level, String.format(format, objects));
    }

    public static void error (Object any) {
        log(Level.SEVERE, any);
    }

    public static void error (String format, Object ... objects) {
        log(Level.SEVERE, format, objects);
    }

    public static void warning (Object any) {
        log(Level.WARNING, any);
    }

    public static void warning (String format, Object ... objects) {
        log(Level.WARNING, format, objects);
    }

    public static void info (Object any) {
        log(Level.INFO, any);
    }

    public static void info (String format, Object ... objects) {
        log(Level.INFO, format, objects);
    }

    public static void debug (Object any) {
        if (Settings.DEBUG) {
            log(Level.FINE, any);
        }
    }

    public static void debug (String format, Object ... objects) {
        if (Settings.DEBUG) {
            log(Level.FINE, format, objects);
        }
    }

}
