package infrastructure;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.slf4j.LoggerFactory;


public final class Logger {
    private static Logger INSTANCE;

    private final org.slf4j.Logger logger;

    private Logger(final org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public static void install(final CyServiceRegistrar registrar) {
        INSTANCE = new Logger(LoggerFactory.getLogger(Logger.class));
    }

    public static Logger getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public void error(final String msg) {
        logger.error(msg);
    }

    public void error(final Exception e) {
        logger.error(e.getMessage());
    }

    public void warning(final String msg) {
        logger.warn(msg);
    }
}