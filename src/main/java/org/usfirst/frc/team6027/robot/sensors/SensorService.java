package org.usfirst.frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;

public class SensorService {

	private EncoderSensors encoderSensors;
	private PIDCapableGyro gyroSensor;
	private AnalogInput analogInput;
	int analogInputNumber;

	public SensorService() {
		this.encoderSensors = new EncoderSensors();
		this.gyroSensor = new NavxGyroSensor();
		this.analogInput = new AnalogInput(analogInputNumber);
	}

	public double getAirPressurePsi() {
		return 250.0 * analogInput.getVoltage() / 5.0 - 20.0;
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
}
