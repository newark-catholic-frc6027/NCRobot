package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistHatchPickupCommand extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public DriverAssistHatchPickupCommand(DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, SensorService sensorService) {
	}

	@Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
    }

}
