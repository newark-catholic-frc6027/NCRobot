package frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;

public interface MotorEncoder<T> extends PIDSource {

    void markPosition();
    void resetMark();
    void reset();
    double getLastMarkPosition();
    double getPosition();
    double getRelativePosition();
    /**
     * Distance since mark
     */
    double getDistance();
    double getRelativeDistance();

    double getVelocity();


}