package frc.team6027.robot.commands.autonomous;

import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossLineFromCenterCommand extends CommandGroup {
    private Preferences prefs = Preferences.getInstance();

    public AutoCrossLineFromCenterCommand(SensorService sensorService, DrivetrainSubsystem drivetrain, 
            PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.addSequential(new PneumaticsInitializationCommand(pneumaticSubsystem));
        
        double distance = this.prefs.getDouble("H-L1-Cross", -12.0);
        this.addSequential( 
            new DriveStraightCommand(sensorService, drivetrain, operatorDisplay, 
                    distance, 
                    DriveDistanceMode.DistanceFromObject, .40) 
        );
    }
}
