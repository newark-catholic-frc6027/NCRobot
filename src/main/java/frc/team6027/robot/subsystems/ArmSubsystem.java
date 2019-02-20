package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorInterface;
import frc.team6027.robot.RobotConfigConstants;
// import com.ctre.CANTalon;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import frc.team6027.robot.sensors.MotorEncoder;
import frc.team6027.robot.sensors.MotorEncoderCANImpl;
import frc.team6027.robot.sensors.MotorPIDController;
import frc.team6027.robot.sensors.MotorPIDControllerCANImpl;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class ArmSubsystem extends Subsystem {
    public enum MotorKey {
        MotorMain
    }

    public enum MotorDirection {
        In,
        Out
    }

    @SuppressWarnings("unused")
    private final Logger logger = LogManager.getLogger(getClass());

    private WPI_TalonSRX mainMotor = new WPI_TalonSRX(RobotConfigConstants.ARM_MOTOR_CIM_1_ID);
    private OperatorInterface operatorInterface;

    public ArmSubsystem(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
        this.initialize();
    }

    protected void initialize() {
        this.stopMotor();
    }

    /**
     * When the run method of the scheduler is called this method will be
     * called.
     */
    @Override
    public void periodic() {
    }

    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    public void spin(double power, MotorDirection spinDirection) {
        this.mainMotor.set(spinDirection == MotorDirection.Out ? power * -1 : power);
    }

    public void stop() {
        this.stopMotor();
    }

   public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }

    public void stopMotor() {
        this.mainMotor.stopMotor();
//        getRobotDrive().stopMotor();

    }

}
