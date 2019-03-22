package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.command.Command;

public class ToggleDrivetrainModeCommand extends Command {
    public enum DrivetrainMode {
        Brake,
        Coast
    }

    private final Logger logger = LogManager.getLogger(getClass());
    public static final int MAX_RUN_TIME_MS = 200;

    protected DrivetrainSubsystem drivetrain;

    protected boolean done = false;
    protected Long startTime = null;

    public ToggleDrivetrainModeCommand(DrivetrainSubsystem drivetrain) {
        this.drivetrain = drivetrain;
        requires(drivetrain);
    }
    
    protected void reset() {
        this.done = false;
        startTime = null;
    }

    @Override
    protected boolean isFinished() {
        if (this.done) {
            this.reset();
        }
        return this.done;
    }
    
    protected void execute() {
        long currentTime = System.currentTimeMillis();
        if (startTime == null) {
            logger.info("{} running...", this.getClass().getSimpleName());
            startTime = currentTime = System.currentTimeMillis();
        }

        if (! done && currentTime - startTime < MAX_RUN_TIME_MS) {
            if (this.drivetrain.isBrakeModeEnabled()) {
                this.done = this.drivetrain.enableCoastMode();
            } else if (this.drivetrain.isCoastModeEnabled()) {
                this.done = this.drivetrain.enableBrakeMode();
            }
        } else {
            if (currentTime - startTime >= MAX_RUN_TIME_MS) {
                logger.warn("Maximum run time exceeded before drivetrain mode could be successfully changed");
            }
            this.done = true;
        }
    }

}
