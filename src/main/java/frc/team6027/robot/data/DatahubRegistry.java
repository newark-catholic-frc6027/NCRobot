package frc.team6027.robot.data;

import java.util.HashMap;
import java.util.Map;

public class DatahubRegistry {
    private Map<String, Datahub> registry = new HashMap<>();

    private static DatahubRegistry instance;

    public synchronized static DatahubRegistry instance() {
        if (instance == null) {
            instance = new DatahubRegistry();
        }

        return instance;
    }

    public void register(Datahub datahub) {
        this.register(datahub.getName(), datahub);
    }

    public void register(String key, Datahub datahub) {
        this.registry.put(key, datahub);
    }

    public Datahub get(String key) {
        return this.registry.get(key);
    }
}