package frc.team6027.robot.sensors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Ultrasonic;

public class UltrasonicSensor implements PIDSource {
    private final Logger logger = LogManager.getLogger(getClass());

    private static final String INFINITY_LOWER_PREF = "ultrasonicInfinityLower";
    private static final String INFINITY_UPPER_PREF = "ultrasonicInfinityUpper";
    private static final double INFINITY_LOWER_DEFAULT = 2.0170;
    private static final double INFINITY_UPPER_DEFAULT = 2.0179;

    private Ultrasonic ultrasonic;
    private Double infinityLower = null;
    private Double infinityUpper = null;
    private Preferences prefs = Preferences.getInstance();

    UltrasonicSensor(int pingChannel, int echoChannel) {
        this.ultrasonic = new Ultrasonic(pingChannel, echoChannel);
        this.ultrasonic.setAutomaticMode(true);
    }
    
    public Double getDistanceInches() {
        if (this.infinityLower == null) {
            this.infinityLower = prefs.getDouble(INFINITY_LOWER_PREF, INFINITY_LOWER_DEFAULT);
        }
        if (this.infinityUpper == null) {
            this.infinityUpper = prefs.getDouble(INFINITY_UPPER_PREF, INFINITY_UPPER_DEFAULT);
        }

        Double dist = this.ultrasonic.getRangeInches();
        if (dist >= infinityLower && dist <= infinityUpper) {
            this.logger.warn("!!!!! Ultrasonic INFINITY! {} <= #{}# <= {}. Returning null", infinityLower, dist, infinityUpper);
            dist = null;
        }
        return dist;
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
