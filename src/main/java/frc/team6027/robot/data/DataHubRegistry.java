package frc.team6027.robot.data;

import java.util.HashMap;
import java.util.Map;

public class DataHubRegistry {
    public static final String VISION_KEY = "vision"; 

    private Map<String, DataHub> registry = new HashMap<>();

    private static DataHubRegistry instance;

    public synchronized static DataHubRegistry instance() {
        if (instance == null) {
            instance = new DataHubRegistry();
        }

        return instance;
    }

    public void register(DataHub dataHub) {
        this.register(dataHub.getName(), dataHub);
    }

    public void register(String key, DataHub dataHub) {
        this.registry.put(key, dataHub);
    }

    public DataHub get(String key) {
        return this.registry.get(key);
    }
}