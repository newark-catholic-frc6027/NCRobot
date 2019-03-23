package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistHatchPickupCommand extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public DriverAssistHatchPickupCommand(DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, SensorService sensorService) {
	}

    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

	@Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
    }

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
        this.default_onComplete();
    }

    @Override
    public void end() {
        this.onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }

}
