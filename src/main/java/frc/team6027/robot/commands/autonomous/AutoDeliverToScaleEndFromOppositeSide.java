package frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.ShiftGearCommand;
import frc.team6027.robot.commands.ShiftGearCommand.ShiftGearMode;
import frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToScaleEndFromOppositeSide extends CommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StartingPositionSide startingSide;

    private Field field;


    public AutoDeliverToScaleEndFromOppositeSide(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingSide = startingSide;
        this.elevatorSubsystem = elevatorSubsystem;
        this.field = field;
  
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));
        
        Command shiftHighCmd1 = new ShiftGearCommand(this.pneumaticSubsystem, ShiftGearMode.High);
        Command shiftHighCmd2 = new ShiftGearCommand(this.pneumaticSubsystem, ShiftGearMode.High);
        Command shiftLowCmd1 = new ShiftGearCommand(this.pneumaticSubsystem, ShiftGearMode.Low);
        Command shiftLowCmd2 = new ShiftGearCommand(this.pneumaticSubsystem, ShiftGearMode.Low);
        Command multiLegDriveCmd1 = createMultiLegDriveCommand1(1.0);  
        Command multiLegDriveCmd2 = createMultiLegDriveCommand2(1.0);  
        Command multiLegDriveCmd3 = createMultiLegDriveCommand3(1.0);  
        Command turnCommand1 = createTurnCommand1();
        Command turnCommand2 = createTurnCommand2();
        Command driveToScaleCmd = createDriveToScaleCommand();
        
        this.addSequential(shiftHighCmd1);
        this.addSequential(multiLegDriveCmd1);
        this.addSequential(shiftLowCmd1);  // new
        this.addSequential(turnCommand1);
        this.addSequential(shiftHighCmd2);  // new
        this.addSequential(multiLegDriveCmd2);
        this.addSequential(shiftLowCmd2);  // new
        this.addSequential(multiLegDriveCmd3);
        this.addSequential(turnCommand2);
        this.addParallel(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem, this.field));
        this.addParallel(AutoCommandHelper.createElevatorUpForDeliveryCommand(this.elevatorSubsystem, this.drivetrainSubsystem, this.getSensorService()));
        this.addSequential(driveToScaleCmd);
        this.addSequential(AutoCommandHelper.createCubeDeliveryCommand(this.getPneumaticSubsystem(), this.field));
    }


    protected Command createDriveToScaleCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                this.prefs.getDouble("B-L4-XS-Scale", 15.0),
                DriveDistanceMode.DistanceReadingOnEncoder, 
                0.55
        );

        
        return cmd;
    }

    protected Command createTurnCommand1() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? -1.0 : 1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }

    protected Command createTurnCommand2() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand1(double power) {
        double leg1Distance = this.prefs.getDouble("B-L1-XS-Scale", 200.0);
        double leg1Angle = 0.0;

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, power
        );
        
        return cmd;
    }


    protected Command createMultiLegDriveCommand2(double power) {
        double leg2Distance = this.prefs.getDouble("B-L2-XS-Scale", 220.0);
        double leg2Angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? -1.0 : 1.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg2Angle, leg2Distance)
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, power
        );
        
        return cmd;
    }
    
    protected Command createMultiLegDriveCommand3(double power) {
        double leg3Distance = this.prefs.getDouble("B-L3-XS-Scale", 70.0);
        double leg3Angle = 0.0;

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg3Angle, leg3Distance),
                
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, power
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


    public void setStartingPositionSide(StartingPositionSide startingSide) {
        this.startingSide = startingSide;
    }


    public SensorService getSensorService() {
        return sensorService;
    }


    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    public ElevatorSubsystem getElevatorSubsystem() {
        return elevatorSubsystem;
    }

    public void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }
}
