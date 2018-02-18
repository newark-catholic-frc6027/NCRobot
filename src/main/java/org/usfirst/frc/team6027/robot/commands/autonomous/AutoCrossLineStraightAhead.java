package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineStraightAhead extends CommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String COMMAND_NAME = "Charge!";
    
	public AutoCrossLineStraightAhead(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
			OperatorDisplay operatorDisplay) {
		requires(drivetrainSubsystem);
		
		this.setName(COMMAND_NAME);

		// DriveStraightCommand driveStraightCmd = new
		// DriveStraightCommand(encoderSensors, drivetrainSubsystem, operatorDisplay,
		// 42, gyro);
		// this.addSequential(driveStraightCmd);

		// TODO Remove after experimentation
		TurnCommand turnCommand = new TurnCommand(Preferences.getInstance().getDouble("turnCommand.turnAngle", 90), sensorService, drivetrainSubsystem, operatorDisplay);
		this.addSequential(turnCommand);
	}

}
