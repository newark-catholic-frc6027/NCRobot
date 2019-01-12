package frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineStraightAhead extends CommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Preferences prefs = Preferences.getInstance();

    public static final String COMMAND_NAME = "Cross Line From End Position";
    
	public AutoCrossLineStraightAhead(double distance, double power, SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
			OperatorDisplay operatorDisplay) {
		requires(drivetrainSubsystem);
		
		this.setName(COMMAND_NAME);

		Command driveStraightCommand = new DriveStraightCommand(sensorService, drivetrainSubsystem, operatorDisplay, distance, DriveDistanceMode.DistanceReadingOnEncoder, power);
		
		this.addSequential(driveStraightCommand);
	}

}
