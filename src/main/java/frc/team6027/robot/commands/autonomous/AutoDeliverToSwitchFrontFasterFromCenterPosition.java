package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToSwitchFrontFasterFromCenterPosition extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    
    
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StartingPositionSide startingPositionSide;
    private Field field;


    public AutoDeliverToSwitchFrontFasterFromCenterPosition(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingPositionSide = startingSide;
        this.field = field;
          
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));
        
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();

        this.addSequential(multiLegDriveCmd);
        this.addSequential(driveToSwitchCmd);
        this.addSequential(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem, this.field));
        this.addSequential(AutoCommandHelper.createCubeDeliveryCommand(pneumaticSubsystem, this.field));
    }

    protected Command createDriveToSwitchCommand() {
        // Value is negative because we are using ultrasonic
        double leg4Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L4-LC-Switch", -12.0) : // Left
                        this.prefs.getDouble("F-L4-RC-Switch", -12.0)   // Right
        );
        
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                leg4Distance,
                DriveDistanceMode.DistanceFromObject, 
                0.55
        );

        
        return cmd;
    }
    
    protected Command createMultiLegDriveCommand() {
        // Interpreting StartingPostionSide Left here as the side we are delivering to

        double leg1Angle = 0.0;
        double leg1Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L1-LC-Switch", 40.0) : // Left
                        this.prefs.getDouble("F-L1-RC-Switch", 40.0)   // Right
        );
        
        double leg2Angle = (this.startingPositionSide == StartingPositionSide.Left ? -1.0 : 1.0) * 45.0;
        double leg2Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L2-LC-Switch", 55.0) : // Left
                        this.prefs.getDouble("F-L2-RC-Switch", 35.0)   // Right
        );
        
        double leg3Angle = 0.0;
        double leg3Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L3-LC-Switch", 20.0) : // Left
                        this.prefs.getDouble("F-L3-RC-Switch", 20.0)   // Right
        );

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                new TargetVector(leg2Angle, leg2Distance),
                new TargetVector(leg3Angle, leg3Distance),
                
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.8
        );
        
        return cmd;
    }


    public DrivetrainSubsystem getDrivetrainSubsystem() {
        return drivetrainSubsystem;
    }


    public void setDrivetrainSubsystem(DrivetrainSubsystem drivetrainSubsystem) {
        this.drivetrainSubsystem = drivetrainSubsystem;
    }


    public PneumaticSubsystem getPneumaticSubsystem() {
        return pneumaticSubsystem;
    }


    public void setPneumaticSubsystem(PneumaticSubsystem pneumaticSubsystem) {
        this.pneumaticSubsystem = pneumaticSubsystem;
    }


    public OperatorDisplay getOperatorDisplay() {
        return operatorDisplay;
    }


    public void setOperatorDisplay(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;
    }


    public StartingPositionSide getStartingPositionSide() {
        return startingPositionSide;
    }


    public void setStartingPositionSide(StartingPositionSide startingSide) {
        this.startingPositionSide = startingSide;
    }


    public SensorService getSensorService() {
        return sensorService;
    }


    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }
}
