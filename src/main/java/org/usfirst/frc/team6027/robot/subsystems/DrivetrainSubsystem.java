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

    private WPI_TalonSRX rightGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.RIGHT_GEARBOX_CIM_1_ID);
    private WPI_TalonSRX leftGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.LEFT_GEARBOX_CIM_1_ID);
    private WPI_TalonSRX rightGearBoxSlave1 = new WPI_TalonSRX(RobotConfigConstants.RIGHT_GEARBOX_CIM_2_ID);
    private WPI_TalonSRX leftGearBoxSlave1 = new WPI_TalonSRX(RobotConfigConstants.LEFT_GEARBOX_CIM_2_ID);
    /* Removing this since we only have two motors for this build 
    private WPI_TalonSRX rightGearBoxSlave2 = new WPI_TalonSRX(RobotConfigConstants.RIGHT_GEARBOX_CIM_3_ID);
    private WPI_TalonSRX leftGearBoxSlave2 = new WPI_TalonSRX(RobotConfigConstants.LEFT_GEARBOX_CIM_3_ID);
    */
    
    private RobotDrive robotDrive = new RobotDrive(leftGearBoxMaster, rightGearBoxMaster);
    

    private OperatorInterface operatorInterface;

    public DrivetrainSubsystem(OperatorInterface operatorInterface){
        this.operatorInterface = operatorInterface;
        this.initialize();
    }

    protected void initialize () {
    	this.rightGearBoxSlave1.follow(rightGearBoxMaster);
    	this.leftGearBoxSlave1.follow(leftGearBoxMaster);

    	// Setting the speed controllers forward for our drivetrain
    	boolean invert = RobotConfigConstants.OPTIONAL_DRIVETRAIN_DIRECTION_INVERSION == -1;
    	
    	this.rightGearBoxMaster.setInverted(invert);
        this.rightGearBoxSlave1.setInverted(invert);
        this.leftGearBoxMaster.setInverted(invert);
        this.leftGearBoxSlave1.setInverted(invert);
        
        this.stopMotor();
    }

    /**
     * When the run method of the scheduler is called this method will be called.
     */
    @Override
    public void periodic() {
    }
    
    
    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void setDefaultCommand(Command command){
        super.setDefaultCommand(command);
    }

    public void doArcadeDrive(double forwardValue, double rotateValue) {
        getRobotDrive().arcadeDrive(forwardValue, rotateValue);
    }

    public void stopArcadeDrive() {
        getRobotDrive().arcadeDrive(0,0);
        getRobotDrive().stopMotor();
    }

    public void drive(double outputMagnitude, double curve) {
        getRobotDrive().drive(outputMagnitude, curve);
    }
    
    public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }

    protected RobotDrive getRobotDrive() {
        return robotDrive;
    }

    public void setRobotDrive(RobotDrive robotDrive) {
        this.robotDrive = robotDrive;
        
    }

    public void tankDrive(double leftValue, double rightValue) {
        getRobotDrive().tankDrive(leftValue, rightValue);
        
    }

    public void stopMotor() {
        this.leftGearBoxMaster.stopMotor();
        this.leftGearBoxSlave1.stopMotor();
        this.rightGearBoxMaster.stopMotor();
        this.rightGearBoxSlave1.stopMotor();
        getRobotDrive().stopMotor();
        
    }
}
