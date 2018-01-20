package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.commands.DriveStraightCommand;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCrossLine extends CommandGroup {
    
    public AutonomousCrossLine(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem) {
        DriveStraightCommand driveStraightCmd = new DriveStraightCommand(encoderSensors, drivetrainSubsystem, 40);
        this.addSequential(driveStraightCmd);
    }

}
