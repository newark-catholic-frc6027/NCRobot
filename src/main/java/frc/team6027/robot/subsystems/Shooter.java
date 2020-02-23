package frc.team6027.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team6027.robot.RobotConfigConstants;

public class Shooter extends SubsystemBase {
    private final Logger logger = LogManager.getLogger(getClass());

    private CANSparkMax mainMotor = null;//new CANSparkMax(RobotConfigConstants.SHOOTER_MOTOR_CIM_ID, MotorType.kBrushless);
//    private OperatorInterface operatorInterface;

    public Shooter() {
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
        if (mainMotor != null) {
            this.mainMotor.set(0);
            this.mainMotor.stopMotor();
        }
    }

    public void spin(double power, MotorDirection spinDirection) {
        if (mainMotor != null) {
            this.mainMotor.set(spinDirection == MotorDirection.Forward ? power : -1 * power);
        }
    }


}