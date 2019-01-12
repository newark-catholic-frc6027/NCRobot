package frc.team6027.robot.sensors;

import frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.AnalogInput;

public class AirPressureSensor {
    private static final double SENSOR_MULTIPLIER_VALUE = 250.0;
    private static final double ANALOG_SUPPLY_VOLTAGE = 5.0;
    private static final double SENSOR_OFFSET = 20.0;
    
    private AnalogInput analogInput;
    private int port;
    
    public AirPressureSensor() {
        this(RobotConfigConstants.PRESSURE_SENSOR_PORT);
    }
    
    public AirPressureSensor(int port) {  
        this.port = port;
        this.analogInput = new AnalogInput(this.port);
    }
    
    public double getAirPressurePsi() {
        return SENSOR_MULTIPLIER_VALUE * this.analogInput.getVoltage() / ANALOG_SUPPLY_VOLTAGE - SENSOR_OFFSET;
    }

    
}
