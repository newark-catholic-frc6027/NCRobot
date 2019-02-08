package frc.team6027.robot.data;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DataHubNetworkTableImpl implements DataHub {

    private String tableName;
    private NetworkTable networkTable;
    private Map<String, NetworkTableEntry> ntEntryCache = new HashMap<>();


    public DataHubNetworkTableImpl(String tableName) {
        this.tableName = tableName;
        this.networkTable = NetworkTableInstance.getDefault().getTable(this.tableName);
    }

    @Override
    public String getString(String key) {
        return this.getString(key, "");
    }


    @Override
    public String getString(String key, String defaultValue) {
        NetworkTableEntry entry = this.getEntry(key);
        return entry.getString(defaultValue);
    }

    @Override
    public Double getDouble(String key) {
        return this.getDouble(key, -1.0);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        NetworkTableEntry entry = this.getEntry(key);
        return entry.getDouble(defaultValue);
    }

    protected NetworkTableEntry getEntry(String key) {
        NetworkTableEntry entry = null;
        if (! this.ntEntryCache.containsKey(key)) {
            entry = this.networkTable.getEntry(key);
            this.ntEntryCache.put(key, entry);
        } else {
            entry = this.ntEntryCache.get(key);
        }

        return entry;
    }

    @Override
    public void put(String key, String value) {
        // TODO

    }

    @Override
    public void put(String key, Double value) {
        // TODO

    }

    @Override
    public String getName() {
        return this.tableName;
    }

}