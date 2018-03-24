package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.PneumaticsInitializationCommand;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineFromCenterCommand extends CommandGroup {
    private Preferences prefs = Preferences.getInstance();

    public AutoCrossLineFromCenterCommand(SensorService sensorService, DrivetrainSubsystem drivetrain, 
            PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.addSequential(new PneumaticsInitializationCommand(pneumaticSubsystem));
        this.addSequential( 
            new DriveStraightCommand(sensorService, drivetrain, operatorDisplay, 
                    -12.0, 
                    DriveDistanceMode.DistanceFromObject, .40) 
        );
    }
}
