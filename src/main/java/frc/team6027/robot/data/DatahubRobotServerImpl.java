package frc.team6027.robot.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * A Datahub implementation that allows for one way caching of data
 * received from a Robot client. Does not support sending of data
 * from the robot to a remote client, but does allow for sharing
 * of data within the Robot classes themselves. Thread safety
 * is implemented by synchronizing access to the underlying map
 * that holds the entries being cached.
 */
public class DatahubRobotServerImpl implements Datahub {
    private final Logger logger = LogManager.getLogger(getClass());

    private Map<String,Object> entries = new HashMap<>();
    private String name;
    private Object _entriesLock = new Object();

    public DatahubRobotServerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getString(String key) {
        return this.getString(key, null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object value = null;
        try {
            value = this.getObj(key, defaultValue);
            if (value == null) {
                return defaultValue;
            }

            if (value instanceof String) {
                return (String) value;
            } else {
                return value.toString();
            }
        } catch (Exception ex) {
            logger.warn("Failed to parse value of '{}' for key '{}', returning default value. Value received: '{}'", value, key);
            return defaultValue;
        }
    }

    @Override
    public Double getDouble(String key) {
        return this.getDouble(key, null);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        Object value = null;
        try {
            value = this.getObj(key, defaultValue);
            if (value == null) {
                return defaultValue;
            }
            if (value instanceof String) {
                return Double.valueOf((String)value);
            } else {
                return (Double) value;
            }
        } catch (Exception ex) {
            logger.warn("Failed to parse value of '{}' for key '{}', returning default value. Value received: '{}'", value, key);
            return defaultValue;
        }
    }

    @Override
    public Float getFloat(String key) {
        return this.getFloat(key, null);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        Object value = null;
        try {
            value = this.getObj(key, defaultValue);
            if (value == null) {
                return defaultValue;
            }

            if (value instanceof String) {
                return Float.valueOf((String)value);
            } else {
                return (Float) value;
            }
        } catch (Exception ex) {
            logger.warn("Failed to parse value of '{}' for key '{}', returning default value. Value received: '{}'", value, key);
            return defaultValue;
        }
    }

    @Override
    public void put(String key, String value) {
        this.putObj(key, value);
    }

    @Override
    public void put(String key, Double value) {
        this.putObj(key, value);
    }

    protected void putObj(String key, Object value) {
        synchronized (this._entriesLock) {
            this.entries.put(key, value);
        }
    }

    protected Object getObj(String key) {
        synchronized (this._entriesLock) {
            return this.entries.get(key);
        }
    }

    protected Object getObj(String key, Object defaultValue) {
        Object value;
        synchronized (this._entriesLock) {
            value = this.entries.get(key);
        }
        return value != null ? value : defaultValue;
    }

    @Override
    public Map<String,Object> getAll() {
        synchronized(this._entriesLock) {
            return Collections.unmodifiableMap(this.entries);
        }
    }

    @Override
    public void put(Map<String, Object> values, boolean replace) {
        synchronized (this._entriesLock) {
            if (replace) {
                this.entries.clear();
            }
            this.entries.putAll(values);
        }
    }

}