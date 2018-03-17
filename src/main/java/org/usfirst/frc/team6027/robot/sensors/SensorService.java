package org.usfirst.frc.team6027.robot.sensors;


public class SensorService {

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
        this.getGyroSensor().reset();
        this.getEncoderSensors().reset();
        
        // TODO: add more resets?
    }
}
