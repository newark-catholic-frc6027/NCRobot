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

    private WPI_TalonSRX mainMotor = null;//new WPI_TalonSRX(RobotConfigConstants.BALL_INTAKE_MOTOR_CIM_ID);
//    private OperatorInterface operatorInterface;

    public Ballpickup() {
//        this.operatorInterface = operatorInterface;
        this.initialize();
    }

    protected void initialize() {
        this.stopMotor();
    }

    public void stop() {
        this.stopMotor();
    }
/*
   public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }
*/
    public void stopMotor() {
        if (this.mainMotor != null) {
            mainMotor.set(ControlMode.PercentOutput, 0);
            this.mainMotor.stopMotor();
        }
    }

    public void spin(double power, MotorDirection spinDirection) {
        if (this.mainMotor != null) {
            this.mainMotor.set(spinDirection == MotorDirection.Reverse ? power * -1 : power);
        }
    }


}