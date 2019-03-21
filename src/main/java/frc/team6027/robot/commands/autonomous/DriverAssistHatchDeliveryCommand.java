package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistHatchDeliveryCommand extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public DriverAssistHatchDeliveryCommand(LevelSelection levelSelection, DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, PneumaticSubsystem pneumaticsSubsystem, SensorService sensorService) {
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
