package frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Ultrasonic;

public class UltrasonicSensor implements PIDSource {
    
    private Ultrasonic ultrasonic;
    
    UltrasonicSensor(int pingChannel, int echoChannel) {
        this.ultrasonic = new Ultrasonic(pingChannel, echoChannel);
        this.ultrasonic.setAutomaticMode(true);
    }
    
    public double getDistanceInches() {
        return -1 * this.ultrasonic.getRangeInches();
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
        // When using ultrasonic, we need to change our coordinate system
        // We need to view distance from target as a negative value that we want to increase to 0
        return -1 * this.ultrasonic.pidGet();
    }
    
}
