package frc.team6027.robot.sensors;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MotorPIDControllerCANImpl implements MotorPIDController<CANPIDController> {
    private final Logger logger = LogManager.getLogger(getClass());


    private CANPIDController pid;
    private double setpoint;

    public MotorPIDControllerCANImpl(CANPIDController pid) {
        this.pid = pid;
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
    public boolean setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        CANError err = pid.setReference(setpoint, ControlType.kPosition);
        return err == CANError.kOk;
    }

    @Override
    public double getSetpoint() {
        return this.setpoint;
    }

/*
    @Override
    public void reset() {
        // TODO;
    }
*/

    @Override
    public boolean setOutputRange(double min, double max) {
        return check(pid.setOutputRange(min, max));
    }

    @Override
    public boolean setFF(double ff) {
        return check(pid.setFF(ff));
    }


    protected boolean check(CANError error) {
        if (error != null && error != CANError.kOk) {
            logger.warn("CANError: {}", error.toString());
            return false;
        }

        return true;
    }

    @Override
    public boolean setP(double gain) {
        return check(pid.setP(gain));
    }

    @Override
    public boolean setI(double gain) {
        return check(pid.setI(gain));
    }

    @Override
    public double getIZone() {
        return pid.getIZone();
    }

    @Override
    public boolean setIZone(double izone) {
        return check(pid.setIZone(izone));
    }

    @Override
    public boolean setD(double gain) {
        return check(pid.setD(gain));
    }

    @Override
    public double getFF() {
        return pid.getFF();
    }

    @Override
    public double getOutputMax() {
        return pid.getOutputMax();
    }

    @Override
    public double getOutputMin() {
        return pid.getOutputMin();
    }

}