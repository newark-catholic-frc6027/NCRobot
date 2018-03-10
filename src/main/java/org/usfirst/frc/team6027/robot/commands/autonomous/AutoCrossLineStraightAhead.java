package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineStraightAhead extends CommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String COMMAND_NAME = "Cross Line Straight Ahead";
    
	public AutoCrossLineStraightAhead(double distance, double power, SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
			OperatorDisplay operatorDisplay) {
		requires(drivetrainSubsystem);
		
		this.setName(COMMAND_NAME);

		Command driveStraightCommand = new DriveStraightCommand(sensorService, drivetrainSubsystem, operatorDisplay, distance, DriveDistanceMode.DistanceReadingOnEncoder, power);
		
		this.addSequential(driveStraightCommand);
	}

}
