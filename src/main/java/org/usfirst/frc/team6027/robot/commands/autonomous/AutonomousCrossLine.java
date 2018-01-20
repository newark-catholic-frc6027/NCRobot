package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.DriveStraightCommand;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCrossLine extends CommandGroup {
    
    public AutonomousCrossLine(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem, OperatorDisplay operatorDisplay) {
        requires(drivetrainSubsystem);
        
        DriveStraightCommand driveStraightCmd = new DriveStraightCommand(encoderSensors, drivetrainSubsystem, operatorDisplay, 100);
        this.addSequential(driveStraightCmd);
    }

    
}
