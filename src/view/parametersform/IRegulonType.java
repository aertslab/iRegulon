package view.parametersform;

@Deprecated
public enum IRegulonType {
    PREDICTED_REGULATORS("Predicted regulators"),
    DATABASE_FOR_REGULATORS("Database for regulators"),
    DATABASE_FOR_TARGETOME("Database for targotome"),
    DATABASE_NETWORK_ANNOTATIONS("Database for network annotations");

    private final String name;

    private IRegulonType(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
