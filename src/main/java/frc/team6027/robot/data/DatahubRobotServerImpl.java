package frc.team6027.robot.data;

import java.util.HashMap;
import java.util.Map;

public class DatahubRobotServerImpl implements Datahub {

    private Map<String, DatahubEntryList> entries = new HashMap<>();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return null;
    }

    @Override
    public Double getDouble(String key) {
        return null;
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return null;
    }

    @Override
    public Float getFloat(String key) {
        return null;
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return null;
    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void put(String key, Double value) {

    }

}