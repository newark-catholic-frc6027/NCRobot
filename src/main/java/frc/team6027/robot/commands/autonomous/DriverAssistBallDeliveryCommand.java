package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistBallDeliveryCommand extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    
    public DriverAssistBallDeliveryCommand(LevelSelection levelSelection, DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, SensorService sensorService) {
	}

    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command starting...", this.getClass().getSimpleName());
        super.start();
    }

	@Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
    }

}
