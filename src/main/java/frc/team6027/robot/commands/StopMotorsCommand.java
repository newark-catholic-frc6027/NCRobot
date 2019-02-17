package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.RearLiftSubsystem;
import edu.wpi.first.wpilibj.command.Command;

public class StopMotorsCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private RearLiftSubsystem rearLiftSubsystem;
    private DrivetrainSubsystem drivetrain;
    boolean done = false;

    public StopMotorsCommand(RearLiftSubsystem rearLiftSubsystem, DrivetrainSubsystem drivetrain) {
        this.rearLiftSubsystem = rearLiftSubsystem;
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
        this.rearLiftSubsystem.stopMotor();
        this.done = true;
    }

}
