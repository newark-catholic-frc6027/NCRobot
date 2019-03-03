package frc.team6027.robot.sensors;

import frc.team6027.robot.RobotConfigConstants;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;


public class EncoderSensors {
    public enum EncoderKey {
        DriveMotorLeft,
        DriveMotorRight,
        Elevator
    }
    public static final  double DISTANCE_PER_REVOLUTION =  3.0 * Math.PI * 5.9575;// 3 revolutions of the wheel per 1 encoder revolution, wheel is 5.9575" dia
    public static final  double PULSE_PER_REVOLUTION = 360;//1455;//1024;// 1024/7.65;

    /** 
     * The elevator encoder values when sampled produced a linear equation. 
     * The values below are the slope (M) and constant (B);
     * y = mx + b.
     * x is the raw encoder reading
     * y will be the measurement in inches
     */
//    public static final double ELEVATOR_ENCODER_EQUATION_M_FACTOR = 0.0034;
    public static final double ELEVATOR_ENCODER_EQUATION_M_FACTOR = 0.0034146;
//    public static final double ELEVATOR_ENCODER_EQUATION_B = 0.2165;
    public static final double ELEVATOR_ENCODER_EQUATION_B = 0;

    //public   double DISTANCE_PER_PULSE = DISTANCE_PER_REVOLUTION / PULSE_PER_REVOLUTION;

    private Map<EncoderKey, MotorEncoder> encoderRegistry = new HashMap<>();

    private Encoder elevatorEncoder = new Encoder(
        RobotConfigConstants.ELEVATOR_ENCODER_DIO_CHANNEL_A,
        RobotConfigConstants.ELEVATOR_ENCODER_DIO_CHANNEL_B,
        false,  EncodingType.k4X
    );
    /*
    private Encoder rightEncoder = new Encoder(
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);
    
    private Encoder leftEncoder = new Encoder(
            RobotConfigConstants.LEFT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.LEFT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);
    */

    public EncoderSensors() {
    }

    public void registerEncoder(EncoderKey key, MotorEncoder encoder) {
        this.encoderRegistry.put(key, encoder);
    }

    public void registerEncoders(Map<EncoderKey, MotorEncoder> encoders) {
        this.encoderRegistry.putAll(encoders);
    }

    public MotorEncoder getMotorEncoder(EncoderKey key) {
        return this.encoderRegistry.get(key);
    }

    public void removeMotorEncoder(EncoderKey key) {
        this.encoderRegistry.remove(key);
    }

    protected void initialize() {
        this.reset();
        /*
        double factor =  Preferences.getInstance().getDouble("driveStraightCommand.distanceFactor", 1.0);
        double adjustedDistancePerRevolution = factor * DISTANCE_PER_REVOLUTION;
        double distancePerPulse = adjustedDistancePerRevolution / PULSE_PER_REVOLUTION;
        
        // TODO: define as constants
        getRightEncoder().setMaxPeriod(.1);
        getRightEncoder().setMinRate(10);
        getRightEncoder().setReverseDirection(true);
        getRightEncoder().setSamplesToAverage(7);
        getRightEncoder().setDistancePerPulse(distancePerPulse);
        getRightEncoder().reset();
        
        getLeftEncoder().setMaxPeriod(.1);
        getLeftEncoder().setMinRate(10);
        getLeftEncoder().setReverseDirection(true);
        getLeftEncoder().setSamplesToAverage(7);
        getLeftEncoder().setDistancePerPulse(distancePerPulse);
        getLeftEncoder().reset();
        */
    }
    
    public MotorEncoder getRightEncoder() {      
        return this.encoderRegistry.get(EncoderKey.DriveMotorRight);
    }


    public void reset() {
        this.encoderRegistry.get(EncoderKey.DriveMotorLeft).reset();
        this.encoderRegistry.get(EncoderKey.DriveMotorRight).reset();
    }
    
    public MotorEncoder getLeftEncoder() {      
        return this.encoderRegistry.get(EncoderKey.DriveMotorLeft);
    }
    
    public Encoder getElevatorEncoder() {
        return this.elevatorEncoder;
    }
}
