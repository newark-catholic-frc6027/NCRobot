package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.PneumaticsInitializationCommand;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToSwitchEndFromOppositeSide extends CommandGroup {
    
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StartingPositionSide startingSide;


    public AutoDeliverToSwitchEndFromOppositeSide(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingSide = startingSide;
        
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));

        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command turnCommand = createTurnCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();

        this.addSequential(multiLegDriveCmd);
        this.addSequential(turnCommand);
        this.addSequential(driveToSwitchCmd);
    }


    protected Command createDriveToSwitchCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                10.0,//this.prefs.getDouble("autoDeliverToSwitch.driveDistance", -12.0),
                DriveDistanceMode.DistanceReadingOnEncoder, 
                0.55 //this.prefs.getDouble("autoDeliverToSwitch.driveToSwitchCmd.power", 0.6)
        );

        
        return cmd;
    }


    protected Command createTurnCommand() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 180.0 * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = this.prefs.getDouble("leg1.distance", 200.0);
        double leg1Angle = 0.0;//this.prefs.getDouble("leg1.angle", 0.0);
        double leg2Distance = this.prefs.getDouble("leg2.distance", 170.0);
        double leg2Angle = 90.0 * (this.startingSide == StartingPositionSide.Right ? -1.0 : 1.0);//this.prefs.getDouble("leg2.angle", 30.0) * (this.startingSide == StartingPosition.Right ? 1.0 : -1.0);
//        double leg3Distance = this.prefs.getDouble("leg3.distance", 10.0);
//        double leg3Angle = 180.0 * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);//this.prefs.getDouble("leg3.angle", 0.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                new TargetVector(leg2Angle, leg2Distance)
//                new TargetVector(leg3Angle, leg3Distance),
                
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
