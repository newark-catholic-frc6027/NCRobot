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

/**
 * This is fallback code for delivering to the switch, in case we find that delivering to the back side does not work
 * well.
 *
 */
public class AutoDeliverToSwitchEndFromOppositeSide extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StartingPositionSide startingSide;

    private Field field;


    public AutoDeliverToSwitchEndFromOppositeSide(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingSide = startingSide;
        this.field = field;
        
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));

        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command turnCommand = createTurnCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();

        this.addSequential(multiLegDriveCmd);
        this.addSequential(turnCommand);
        this.addSequential(driveToSwitchCmd);
        this.addSequential(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem, this.field));
        this.addSequential(AutoCommandHelper.createCubeDeliveryCommand(this.getPneumaticSubsystem(), this.field));
    }

    protected Command createDriveToSwitchCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                -12.0,
                DriveDistanceMode.DistanceFromObject, 
                0.55
        );

        
        return cmd;
    }


    protected Command createTurnCommand() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = 200.0;
        double leg1Angle = 0.0;
        double leg2Distance = 215.0;
        double leg2Angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? 1.0 : -1.0);
        double leg3Distance = 70.0;
        double leg3Angle = 180.0 * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                new TargetVector(leg2Angle, leg2Distance),
                new TargetVector(leg3Angle, leg3Distance),
                
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.9
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
        return startingSide;
    }


    public void setStartingPositionSide(StartingPositionSide startingPosition) {
        this.startingSide = startingPosition;
    }


    public SensorService getSensorService() {
        return sensorService;
    }


    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }
}