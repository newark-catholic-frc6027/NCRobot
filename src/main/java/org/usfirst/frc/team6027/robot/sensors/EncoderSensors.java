package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class EncoderSensors {    
    public static final  double DISTANCE_PER_REVOLUTION =  3.0 * Math.PI * 5.9575;// 3 revolutions of the wheel per 1 encoder revolution, wheel is 5.9575" dia
    public static final  double PULSE_PER_REVOLUTION = 360;//1455;//1024;// 1024/7.65;
    //public   double DISTANCE_PER_PULSE = DISTANCE_PER_REVOLUTION / PULSE_PER_REVOLUTION;

    private Encoder rightEncoder = new Encoder(
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);
    
    private Encoder leftEncoder = new Encoder(
            RobotConfigConstants.LEFT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.LEFT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);
    
    public EncoderSensors() {
        initialize();
    }
    
    protected void initialize() {
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
    }
    
    public void setRightEncoder(Encoder rightEncoder) {
        this.rightEncoder = rightEncoder;
    }

    public Encoder getRightEncoder() {      
        return rightEncoder;
    }
    
    public void reset() {
        this.initialize();
    }
    
    public void setLeftEncoder(Encoder leftEncoder) {
        this.leftEncoder = leftEncoder;
    }

    public Encoder getLeftEncoder() {      
        return leftEncoder;
    }
}
