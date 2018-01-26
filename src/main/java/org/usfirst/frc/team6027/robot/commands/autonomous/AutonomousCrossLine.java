package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.DriveStraightCommand;
import org.usfirst.frc.team6027.robot.commands.TurnCommand;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class AutonomousCrossLine extends CommandGroup {
    
    public AutonomousCrossLine(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem, OperatorDisplay operatorDisplay, PIDCapableGyro gyro) {
        requires(drivetrainSubsystem);
        
//        DriveStraightCommand driveStraightCmd = new DriveStraightCommand(encoderSensors, drivetrainSubsystem, operatorDisplay, 42, gyro);
//        this.addSequential(driveStraightCmd);
        
        //TODO Remove after experimentation
        TurnCommand turnCommand = new TurnCommand(0, gyro, drivetrainSubsystem, operatorDisplay);
        this.addSequential(turnCommand);
    }

    
}
