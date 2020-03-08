package frc.team6027.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team6027.robot.RobotConfigConstants;

public class Ballpickup extends SubsystemBase {
    private final Logger logger = LogManager.getLogger(getClass());

    private WPI_TalonSRX ballIntakeMotor = new WPI_TalonSRX(RobotConfigConstants.BALL_INTAKE_MOTOR_CIM_ID);
    private WPI_TalonSRX ballElevatorMotor = new WPI_TalonSRX(RobotConfigConstants.BALL_ELEVATOR_MOTOR_CIM_ID);

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
        if (this.ballIntakeMotor != null) {
            ballIntakeMotor.set(ControlMode.PercentOutput, 0);
            ballElevatorMotor.set(ControlMode.PercentOutput, 0);
            this.ballIntakeMotor.stopMotor();
            this.ballElevatorMotor.stopMotor();
        }
    }

    public void spin(double power, MotorDirection spinDirection) {
        if (this.ballIntakeMotor != null) {
            this.ballIntakeMotor.set(spinDirection == MotorDirection.Reverse ? power * -1 : power);
            this.ballElevatorMotor.set(spinDirection == MotorDirection.Forward ? power * -1 : power);
        }
    }

    
}