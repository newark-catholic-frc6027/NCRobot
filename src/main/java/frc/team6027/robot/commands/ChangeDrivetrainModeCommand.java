package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.command.Command;

public class ChangeDrivetrainModeCommand extends Command {
    public enum DrivetrainMode {
        Brake,
        Coast
    }

    private final Logger logger = LogManager.getLogger(getClass());
    public static final int MAX_RUN_TIME_MS = 200;

    protected DrivetrainSubsystem drivetrain;
    protected DrivetrainMode mode;

    protected boolean done = false;
    protected Long startTime = null;

    public ChangeDrivetrainModeCommand(DrivetrainMode mode, DrivetrainSubsystem drivetrain) {
        this.drivetrain = drivetrain;
        this.mode = mode;
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
            if (this.mode == DrivetrainMode.Brake) {
                this.done = this.drivetrain.enableBrakeMode();
            } else if (this.mode == DrivetrainMode.Coast) {
                this.done = this.drivetrain.enableCoastMode();
            }
        } else {
            this.done = true;
        }
    }

}
