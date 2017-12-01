package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorInterface;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Default command to drive the robot with joysticks. It may be overridden during autonomous mode to run drive sequence. This command grabs
 * the left and right joysticks on an XBox controller and sets them to arcade drive.
 */
public class StickDriveCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DrivetrainSubsystem drivetrain;
    private OperatorInterface operatorInterface;

    /**
     * Requires DriveTrain
     */
    public StickDriveCommand(DrivetrainSubsystem drivetrain, OperatorInterface operatorInterface) {
        this.drivetrain = drivetrain;
        this.operatorInterface = operatorInterface;
        requires(this.drivetrain);

    }

    protected void initialize() {
        logger.info("Using stick drive");
    }

    protected void execute() {
        this.getDrivetrain().startArcadeDrive(
            (-1) * this.getOperatorInterface().getJoystick().getRawAxis(RobotConfigConstants.LEFT_ANALOG_STICK), 
            (-1) * this.getOperatorInterface().getJoystick().getRawAxis(RobotConfigConstants.RIGHT_ANALOG_STICK)
        );
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
        this.getDrivetrain().stopArcadeDrive();
    }

    protected void interrupted() {
        logger.info("Stick drive interrupted");
    }

    public DrivetrainSubsystem getDrivetrain() {
        return drivetrain;
    }

    public void setDrivetrain(DrivetrainSubsystem drivetrain) {
        this.drivetrain = drivetrain;
    }

    public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }
}
