package cisTargetX;

public enum CisTargetType {
	PREDICTED_REGULATORS("Predicted regulators"),
	DATABASE_FOR_REGULATORS("Database for regulators"),
	DATABASE_FOR_TARGETOME("Database for targotome"),
	DATABASE_NETWORK_ANNOTATIONS("Database for network annotations");
	
	private final String name;
	
	private CisTargetType(final String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
