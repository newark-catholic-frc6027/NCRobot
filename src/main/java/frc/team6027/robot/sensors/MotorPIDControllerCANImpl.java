package frc.team6027.robot.sensors;

import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

public class MotorPIDControllerCANImpl implements MotorPIDController<CANPIDController> {

    private CANPIDController pid;
    private double setpoint;

    public MotorPIDControllerCANImpl(CANPIDController pid) {
        this.pid = pid;
    }

    @Override
    public void setPID(double p, double i, double d) {
        pid.setP(p);
        pid.setI(i);
        pid.setD(d);
    }

    @Override
    public double getP() {
        return pid.getP();
    }

    @Override
    public double getI() {
        return pid.getI();
    }

    @Override
    public double getD() {
        return pid.getD();
    }

    @Override
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        pid.setReference(setpoint, ControlType.kPosition);
    }

    @Override
    public double getSetpoint() {
        return this.setpoint;
    }

    @Override
    public double getError() {
        return 0;
    }

    @Override
    public void reset() {
        // TODO;
    }

    @Override
    public void setOutputRange(double min, double max) {
        pid.setOutputRange(min, max);
    }

    @Override
    public void setFeedForward(double ff) {
        pid.setFF(ff);
    }

}