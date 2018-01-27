package org.usfirst.frc.team6027.robot.sensors;

public class SensorService {
    public SensorService(EncoderSensors encoderSensors, PIDCapableGyro gyroSensor) {
		super();
		this.encoderSensors = encoderSensors;
		this.gyroSensor = gyroSensor;
	}
	private EncoderSensors encoderSensors;
    private PIDCapableGyro gyroSensor;
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
}
