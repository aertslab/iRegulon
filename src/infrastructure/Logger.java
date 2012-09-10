package infrastructure;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;


public final class Logger {
    private static final Logger INSTANCE = new Logger();

    private final CyLogHandler logger = ConsoleLogger.getLogger();

    private Logger() {
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void error(final String msg) {
        logger.handleLog(LogLevel.LOG_ERROR, msg);
    }

    public void error(final Exception e) {
        logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
    }

    public void warning(final String msg) {
        logger.handleLog(LogLevel.LOG_WARN, msg);
    }
}
