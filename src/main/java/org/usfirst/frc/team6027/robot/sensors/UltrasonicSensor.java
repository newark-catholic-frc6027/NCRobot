package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.Ultrasonic;

public class UltrasonicSensor {
    
//    private AnalogInput analogInput;
//    private int port;
    
    private Ultrasonic ultrasonic;
    
    public UltrasonicSensor() {
        this(RobotConfigConstants.ULTRASONIC_PING_CHANNEL, RobotConfigConstants.ULTRASONIC_ECHO_CHANNEL);
    }
    
    public UltrasonicSensor(int pingChannel, int echoChannel) {
        this.ultrasonic = new Ultrasonic(pingChannel, echoChannel);
        this.ultrasonic.setAutomaticMode(true);
    }
    
    public double getDistanceInches() {
        return this.ultrasonic.getRangeInches();
    }
    
}
