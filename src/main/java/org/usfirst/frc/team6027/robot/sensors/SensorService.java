package org.usfirst.frc.team6027.robot.sensors;


public class SensorService {

	private EncoderSensors encoderSensors;
	private PIDCapableGyro gyroSensor;
	private AirPressureSensor airPressureSensor;
	private UltrasonicSensor ultrasonicSensor;
	private CameraSensors cameraSensor;

	public SensorService() {
		this.encoderSensors = new EncoderSensors();
		this.gyroSensor = new NavxGyroSensor();
		this.airPressureSensor = new AirPressureSensor();
		this.ultrasonicSensor = new UltrasonicSensor();
		this.cameraSensor = new CameraSensors();
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


    public UltrasonicSensor getUltrasonicSensor() {
        return ultrasonicSensor;
    }


    public void setUltrasonicSensor(UltrasonicSensor ultrasonicSensor) {
        this.ultrasonicSensor = ultrasonicSensor;
    }


    public CameraSensors getCameraSensor() {
        return cameraSensor;
    }


    public void setCameraSensor(CameraSensors cameraSensor) {
        this.cameraSensor = cameraSensor;
    }
}
