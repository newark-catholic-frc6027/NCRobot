package org.usfirst.frc.team6027.robot.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

	private EncoderSensors encoderSensors;
	private PIDCapableGyro gyroSensor;
	private AirPressureSensor airPressureSensor;
	private UltrasonicSensor ultrasonicSensor;
	private CameraSensors cameraSensor;
	private LimitSwitchSensors limitSwitchSensors;

	public SensorService() {
		this.encoderSensors = new EncoderSensors();
		this.gyroSensor = new NavxGyroSensor();
		this.airPressureSensor = new AirPressureSensor();
		this.ultrasonicSensor = new UltrasonicSensor();
		this.cameraSensor = new CameraSensors();
		this.limitSwitchSensors = new LimitSwitchSensors();
	}


	public EncoderSensors getEncoderSensors() {
		return encoderSensors;
	}


	public PIDCapableGyro getGyroSensor() {
		return gyroSensor;
	}


    public AirPressureSensor getAirPressureSensor() {
        return airPressureSensor;
    }



    public UltrasonicSensor getUltrasonicSensor() {
        return ultrasonicSensor;
    }



    public CameraSensors getCameraSensor() {
        return cameraSensor;
    }




    public LimitSwitchSensors getLimitSwitchSensors() {
        return limitSwitchSensors;
    }




    public void resetAll() {
        logger.info("SensorService is resetting all sensors...");
        this.getGyroSensor().reset();
        this.getEncoderSensors().reset();
        
        // TODO: add more resets?
    }
}
