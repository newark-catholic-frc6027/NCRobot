package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.AutoDeliverToSwitch.DeliverySide;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoFinalApproachToSwitchCommand extends CommandGroup {
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;

    private double driveForwardDistance = 0.0;
    private double startUsingUltrasonicDistance = 0.0;
    private double angleHeading = 0.0;
    
    
    public AutoFinalApproachToSwitchCommand(double driveForwardDistance /* e.g., 100, total distance to target */, double offsetFromTargetDistance /* e.g., -20.0 */, double angleHeading, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
 
    	this.driveForwardDistance = driveForwardDistance;
    	this.startUsingUltrasonicDistance = startUsingUltrasonicDistance;
    	
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        
        Command driveStraightCommand = createDriveStraightCommand();
       
        //Do this in stages
        // 1: Drive first stage 80% of distance (driveForwardDistance + offset, offset is negative), make percentage a preference, use .8 power, use gyro pid only
        // 2: Drive remaining distance using ultrasonic pid (distance to target +/- offset)
        // 3: Add a turn command to check the angle
        
    }

    
    protected Command createDriveStraightCommand() {
        double leg1Distance = this.driveForwardDistance;

        double leg1Angle = this.angleHeading; //this.prefs.getDouble("leg1.angle", 0.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance)
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay, 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.6
        );
        
        return cmd;
    }
    
}
