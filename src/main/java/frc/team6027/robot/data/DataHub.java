package frc.team6027.robot.data;

public interface DataHub {
    String getName();
    String getString(String key);
    String getString(String key, String defaultValue);
    Double getDouble(String key);
    Double getDouble(String key, Double doubleValue);

    void put(String key, String value);
    void put(String key, Double value);
}