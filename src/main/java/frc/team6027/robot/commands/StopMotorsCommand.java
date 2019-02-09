package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class StopMotorsCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private ElevatorSubsystem elevatorSubsystem;
    private DrivetrainSubsystem drivetrain;
    boolean done = false;

    public StopMotorsCommand(ElevatorSubsystem elevatorSubsystem, DrivetrainSubsystem drivetrain) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.drivetrain = drivetrain;
    }
    
    @Override
    protected boolean isFinished() {
        if (done) {
            logger.info("StopMotorsCommand finished");
        }
        return done;
    }
    
    protected void execute() {
        logger.info("StopMotorsCommand running...");
        this.drivetrain.stopMotor();
        this.elevatorSubsystem.elevatorStop();
        this.done = true;
    }

}
