package frc.team6027.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team6027.robot.OperatorInterface;
import frc.team6027.robot.RobotConfigConstants;

public class Ballpickup extends SubsystemBase {
    private final Logger logger = LogManager.getLogger(getClass());

    private WPI_TalonSRX mainMotor = new WPI_TalonSRX(RobotConfigConstants.BALL_INTAKE_MOTOR_CIM_ID);
    private WPI_TalonSRX secondMotor = new WPI_TalonSRX(RobotConfigConstants.BALL_ELEVATOR_MOTOR_CIM_ID);

    public Ballpickup() {
        this.initialize();
    }

    protected void initialize() {
        this.stopMotor();
    }

    public void stop() {
        this.stopMotor();
    }

    public void stopMotor() {
        if (this.mainMotor != null) {
            mainMotor.set(ControlMode.PercentOutput, 0);
            secondMotor.set(ControlMode.PercentOutput, 0);
            this.mainMotor.stopMotor();
            this.secondMotor.stopMotor();
        }
    }

    public void spin(double power, MotorDirection spinDirection) {
        if (this.mainMotor != null) {
            this.mainMotor.set(spinDirection == MotorDirection.Reverse ? power * -1 : power);
            this.secondMotor.set(spinDirection == MotorDirection.Forward ? power * -1 : power);
/*
            if (power > .10) {
                this.secondMotor.set(spinDirection == MotorDirection.Forward ? power * -1 : power);
            } else {
                this.secondMotor.set(ControlMode.PercentOutput, 0);
            }
            */
        }
    }


}