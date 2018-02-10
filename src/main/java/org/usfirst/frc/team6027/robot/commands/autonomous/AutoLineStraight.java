package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.TurnCommand;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLineStraight extends CommandGroup {

	public AutoLineStraight(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
			OperatorDisplay operatorDisplay) {
		requires(drivetrainSubsystem);

		// DriveStraightCommand driveStraightCmd = new
		// DriveStraightCommand(encoderSensors, drivetrainSubsystem, operatorDisplay,
		// 42, gyro);
		// this.addSequential(driveStraightCmd);

		// TODO Remove after experimentation
		TurnCommand turnCommand = new TurnCommand(Preferences.getInstance().getDouble("turnCommand.turnAngle", 90), sensorService, drivetrainSubsystem, operatorDisplay);
		this.addSequential(turnCommand);
	}

}
