package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistHatchDeliveryCommand extends CommandGroup implements KillableAutoCommand {
    
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private ElevatorSubsystem elevatorSubsystem;

    private Preferences prefs;

    public DriverAssistHatchDeliveryCommand(LevelSelection levelSelection, DrivetrainSubsystem drivetrainSubsystem,
            ElevatorSubsystem elevatorSubsystem, PneumaticSubsystem pneumaticsSubsystem, SensorService sensorService,
            OperatorDisplay operatorDisplay) {
        this.sensorService = sensorService;
        this.elevatorSubsystem = elevatorSubsystem;

        // Run elevator to set point
        this.addSequential(makeElevatorCommand(levelSelection));
/*
        // Drive in
        this.addSequential(new DriveStraightCommand("E-L1-DriverAssist-Hatch", DriveDistanceMode.DistanceFromObject, "E-P1-DriverAssist-Hatch", 
            null, this.sensorService, drivetrainSubsystem, operatorDisplay));
        // Kick hatch
        this.addSequential(new ToggleKickHatchCommand(pneumaticsSubsystem));
        // Back up
        this.addSequential(new DriveStraightCommand("E-L2-DriverAssist-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "E-P2-DriverAssist-Hatch", 
            null, this.sensorService, drivetrainSubsystem, operatorDisplay));
        // Retract hatch kicker
        this.addSequential(new ToggleKickHatchCommand(pneumaticsSubsystem));
        // Run elevator to set point
//        this.addSequential(new ElevatorCommand("elevator.driving.height", "elevatorCommand.power", this.sensorService, this.elevatorSubsystem));
*/        
    }
    
    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

    @Override
    public void end() {
        this.onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }


    protected Command makeElevatorCommand(LevelSelection levelSelection) {
        String prefName = null;
        switch(levelSelection) {
            case Middle:
                prefName = "rocketHatch.middleLevel";
                break;
            case Upper:
                prefName = "rocketHatch.upperLevel";
                break;
            case Lower:
            default:
                prefName = "rocketHatch.lowerLevel";
                break;
        }

        Command cmd = new ElevatorCommand(prefName, "E-P0-DriverAssist-Hatch", this.sensorService, this.elevatorSubsystem);
        return cmd;
    }

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
        this.default_onComplete();
    }
}
