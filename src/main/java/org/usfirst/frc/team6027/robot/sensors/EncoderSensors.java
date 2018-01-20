package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class EncoderSensors {
    public static final double DISTANCE_PER_REVOLUTION = 18.84;//Math.PI * 6;
    public static final double PULSE_PER_REVOLUTION = 1024;
    public static final double DISTANCE_PER_PULSE = DISTANCE_PER_REVOLUTION / PULSE_PER_REVOLUTION;

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
        // TODO: define as constants
        getRightEncoder().setMaxPeriod(.1);
        getRightEncoder().setMinRate(10);
        getRightEncoder().setReverseDirection(true);
        getRightEncoder().setSamplesToAverage(7);
        getRightEncoder().setDistancePerPulse(DISTANCE_PER_PULSE);
        getRightEncoder().reset();
        
        getLeftEncoder().setMaxPeriod(.1);
        getLeftEncoder().setMinRate(10);
        getLeftEncoder().setReverseDirection(true);
        getLeftEncoder().setSamplesToAverage(7);
        getLeftEncoder().setDistancePerPulse(DISTANCE_PER_PULSE);
        getLeftEncoder().reset();
    }
    
    public void setRightEncoder(Encoder rightEncoder) {
        this.rightEncoder = rightEncoder;
    }

    public Encoder getRightEncoder() {      
        return rightEncoder;
    }
    public void setLeftEncoder(Encoder leftEncoder) {
        this.leftEncoder = leftEncoder;
    }

    public Encoder getLeftEncoder() {      
        return leftEncoder;
    }
}
