package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSequenceSquare extends CommandGroup {
    public AutoSequenceSquare(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay) {
        requires(drivetrainSubsystem);

        DriveStraightCommand longLeg = new DriveStraightCommand(sensorService, drivetrainSubsystem, operatorDisplay, 93.0, DriveDistanceMode.DistanceReadingOnEncoder);
        TurnCommand turnCommand = new TurnCommand(90, sensorService, drivetrainSubsystem, operatorDisplay);
        
        this.addSequential(longLeg);
        this.addSequential(turnCommand);
        this.addSequential(new Command() {
            @Override
            protected boolean isFinished() {
                return true;
            }
            
            protected void execute() {
                sensorService.getGyroSensor().reset(); 
                sensorService.getEncoderSensors().reset();
            }
        });
        DriveStraightCommand shortLeg = new DriveStraightCommand(sensorService, drivetrainSubsystem, operatorDisplay, 72.0, DriveDistanceMode.DistanceReadingOnEncoder);
        
//        this.addSequential(shortLeg);
//        this.addSequential(turnCommand);
//        this.addSequential(longLeg);
        
    }

}
