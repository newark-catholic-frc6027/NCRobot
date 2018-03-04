package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Ultrasonic;

public class UltrasonicSensor implements PIDSource {
    
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

    public boolean isRangeValid() {
        return this.ultrasonic.isRangeValid();
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        this.ultrasonic.setPIDSourceType(pidSource);
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return this.ultrasonic.getPIDSourceType();
    }

    @Override
    public double pidGet() {
        return this.ultrasonic.pidGet();
    }
    
}
