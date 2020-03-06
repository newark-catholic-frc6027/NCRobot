package frc.team6027.robot.data;

import java.util.Map;

public interface Datahub {
    String getName();
    String getString(String key);
    String getString(String key, String defaultValue);
    Double getDouble(String key);
    Double getDouble(String key, Double defaultValue);
    Float getFloat(String key);
    Float getFloat(String key, Float defaultValue);
    Number getNumber(String key, Number defaultValue);
    Number getNumber(String key);

    Map<String, Object> getAll();
    
    void put(String key, String value);
    void put(String key, Double value);
    void put(String key, Number number);
    /**
     * Puts the given map of values into this Datahub instance.
     * When {@code replace} is {@code true}, the current set of
     * values in the datahub is first cleared before the given
     * values are inserted.
     */
    void put(Map<String, Object> values, boolean replace);
}