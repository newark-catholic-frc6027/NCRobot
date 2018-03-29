package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.PneumaticsInitializationCommand;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

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
        
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command turnCommand = createTurnCommand();
        Command driveToScaleCmd = createDriveToScaleCommand();
        
        this.addSequential(multiLegDriveCmd);
        this.addSequential(turnCommand);
        this.addSequential(AutoCommandHelper.createElevatorDownForDeliveryCommand(this.elevatorSubsystem, this.drivetrainSubsystem, this.getSensorService()));
        this.addSequential(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem, this.field));
        this.addSequential(AutoCommandHelper.createElevatorUpForDeliveryCommand(this.elevatorSubsystem, this.drivetrainSubsystem, this.getSensorService()));
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


    protected Command createTurnCommand() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = this.prefs.getDouble("B-L1-XS-Scale", 200.0);
        double leg1Angle = 0.0;
        double leg2Distance = this.prefs.getDouble("B-L2-XS-Scale", 220.0);
        double leg2Angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? -1.0 : 1.0);
        double leg3Distance = this.prefs.getDouble("B-L3-XS-Scale", 70.0);
        double leg3Angle = 0.0;

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
