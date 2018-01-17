package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorInterface;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
// import com.ctre.CANTalon;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DrivetrainSubsystem extends Subsystem {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WPI_TalonSRX frontRight = new WPI_TalonSRX(RobotConfigConstants.FRONT_RIGHT_CANTALON_DRIVE_ID);
    private WPI_TalonSRX backRight = new WPI_TalonSRX(RobotConfigConstants.REAR_RIGHT_CANTALON_DRIVE_ID);
    private WPI_TalonSRX frontLeft = new WPI_TalonSRX(RobotConfigConstants.FRONT_LEFT_CANTALON_DRIVE_ID);
    private WPI_TalonSRX backLeft = new WPI_TalonSRX(RobotConfigConstants.REAR_LEFT_CANTALON_DRIVE_ID);

    private RobotDrive robotDrive = new RobotDrive(frontLeft,backLeft,frontRight,backRight);
//    private RobotDrive robotDrive = new RobotDrive(0,0,0,0);

    private OperatorInterface operatorInterface;

    public DrivetrainSubsystem(OperatorInterface operatorInterface){
        this.operatorInterface = operatorInterface;
    }

    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void setDefaultCommand(Command command){
        super.setDefaultCommand(command);
    }

    public void startArcadeDrive(double forwardValue, double rotateValue) {
        this.doArcadeDrive(forwardValue, rotateValue);
    }

    public void doArcadeDrive(double forwardValue, double rotateValue) {
        getRobotDrive().arcadeDrive(forwardValue, rotateValue);
    }

    public void stopArcadeDrive() {
        getRobotDrive().arcadeDrive(0,0);
    }

    public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }

    public RobotDrive getRobotDrive() {
        return robotDrive;
    }

    public void setRobotDrive(RobotDrive robotDrive) {
        this.robotDrive = robotDrive;
        
    }
}
