package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineVeerLeft extends CommandGroup {
    public static final String COMMAND_NAME = "Charge Left";

	public AutoCrossLineVeerLeft(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
			OperatorDisplay operatorDisplay) {
		this.setName(COMMAND_NAME);
	    requires(drivetrainSubsystem);
		
		// Add sequential steps here
	}
	
}
