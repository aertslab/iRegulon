package domainmodel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TargetomeDatabase {
    private static final Map<String, TargetomeDatabase> CODE2TARGETOMEDATABASE = new HashMap<String, TargetomeDatabase>();

    public static final TargetomeDatabase MSIGDB = new TargetomeDatabase("msigdb", "MSigDB");
    public static final TargetomeDatabase GENESIGDB = new TargetomeDatabase("genesigdb", "GeneSigDB");
    public static final TargetomeDatabase GANESH = new TargetomeDatabase("ganesh", "Ganesh Clusters");
    public static final TargetomeDatabase UNKNOWN = new TargetomeDatabase("?", "?");

    public static TargetomeDatabase getTargetomeDatabase(final String code) {
        if (CODE2TARGETOMEDATABASE.containsKey(code)) {
            return CODE2TARGETOMEDATABASE.get(code);
        }
        return UNKNOWN;
    }

    public static List<TargetomeDatabase> getAllTargetomeDatabases() {
        final List<TargetomeDatabase> result = new ArrayList<TargetomeDatabase>(CODE2TARGETOMEDATABASE.values());
        result.remove(UNKNOWN);
        return result;
    }

    private final String dbCode;
    private final String description;

    private TargetomeDatabase(final String dbCode, final String description) {
        this.dbCode = dbCode;
        this.description = description;
        CODE2TARGETOMEDATABASE.put(dbCode, this);
    }

    public String getDbCode() {
        return dbCode;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return this.description;
    }
}
