package org.usfirst.frc.team6027.robot.sensors;


public class SensorService {

	private EncoderSensors encoderSensors;
	private PIDCapableGyro gyroSensor;
	private AirPressureSensor airPressureSensor;

	public SensorService() {
		this.encoderSensors = new EncoderSensors();
		this.gyroSensor = new NavxGyroSensor();
		this.airPressureSensor = new AirPressureSensor();
	}


	public EncoderSensors getEncoderSensors() {
		return encoderSensors;
	}

	public void setEncoderSensors(EncoderSensors encoderSensors) {
		this.encoderSensors = encoderSensors;
	}

	public PIDCapableGyro getGyroSensor() {
		return gyroSensor;
	}

	public void setGyroSensor(PIDCapableGyro gyroSensor) {
		this.gyroSensor = gyroSensor;
	}


    public AirPressureSensor getAirPressureSensor() {
        return airPressureSensor;
    }


    public void setAirPressureSensor(AirPressureSensor airPressureSensor) {
        this.airPressureSensor = airPressureSensor;
    }
}
