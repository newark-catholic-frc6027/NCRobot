package frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.PIDInterface;

public interface MotorPIDController<T> extends PIDInterface {

    public void setOutputRange(double min, double max);

	public void setFeedForward(double ff);
}