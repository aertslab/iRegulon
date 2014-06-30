package servercommunication;

import org.cytoscape.service.util.CyServiceRegistrar;


public final class ComputationalServiceFactory {
    private static ComputationalServiceFactory INSTANCE;

    private final ComputationalService service;

    private ComputationalServiceFactory(final CyServiceRegistrar services) {
        service = new ComputationalServiceHTTP(services);
    }

    public static void install(final CyServiceRegistrar services) {
        if (INSTANCE != null) throw new IllegalStateException();
        INSTANCE = new ComputationalServiceFactory(services);
    }

    public static ComputationalServiceFactory getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    public ComputationalService getService() {
        return service;
    }
}
