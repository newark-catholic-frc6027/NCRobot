package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
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

    private double offsetFromTargetDistance = 0.0;
    private double totalDistanceToTarget = 0.0;
    private double angleHeading = 0.0;
    
    /* NOT CURRENTLY USING */
    public AutoFinalApproachToSwitchCommand(double totalDistanceToTarget, /* e.g., 100, total distance to target */
            double offsetFromTargetDistance, /* e.g., -20.0 */
            double angleHeading,  /* not currently used */
            SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, 
            PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
 
    	this.totalDistanceToTarget = totalDistanceToTarget;
    	this.offsetFromTargetDistance = offsetFromTargetDistance;
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        
        Command driveStraightCommand = createDriveStraightCommand();
       
        this.addSequential(driveStraightCommand);
        //Do this in stages
        // 1: Drive first stage 80% of distance (driveForwardDistance + offset, offset is negative), make percentage a preference, use .8 power, use gyro pid only
        // 2: Drive remaining distance using ultrasonic pid (distance to target +/- offset)
        // 3: Add a turn command to check the angle
        
    }

    
    protected Command createDriveStraightCommand() {
        double driveDistance = this.totalDistanceToTarget + this.offsetFromTargetDistance;
        
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                driveDistance,
                DriveDistanceMode.DistanceFromObject, 
                0.6, // power
                .80  // Travel this percentage of total distance before cutting over to distance PID control
        );
        
        return cmd;
    }
    
}
