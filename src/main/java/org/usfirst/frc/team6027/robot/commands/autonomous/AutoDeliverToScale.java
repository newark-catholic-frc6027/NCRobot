package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToScale extends CommandGroup {
    
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private DeliverySide deliverySide;


    public AutoDeliverToScale(DeliverySide deliverySide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.deliverySide = deliverySide;
  
// TODO: Not sure which of these commands we'll need
//        Command driveStraightCmd = createDriveStraightCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();

//        this.addSequential(driveStraightCmd);
        this.addSequential(driveToSwitchCmd);
    }


    protected Command createDriveToSwitchCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                this.prefs.getDouble("autoDeliverToSwitch.driveDistance", -12.0),
                DriveDistanceMode.DistanceFromObject, 
                this.prefs.getDouble("autoDeliverToSwitch.driveToSwitchCmd.power", 0.6), // power
                this.prefs.getDouble("autoDeliverToSwitch.driveToSwitchCmd.pidCutoverPercent", 0.8) // Percent of distance from object before cutting over to distance PID control
        );

        
        return cmd;
    }




    protected Command createDriveStraightCommand() {
        double leg1Distance = this.prefs.getDouble("leg1.distance", 95.0);

        double leg1Angle = this.prefs.getDouble("leg1.angle", 0.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance)
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.7
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


    public DeliverySide getDeliverySide() {
        return deliverySide;
    }


    public void setDeliverySide(DeliverySide deliverySide) {
        this.deliverySide = deliverySide;
    }


    public SensorService getSensorService() {
        return sensorService;
    }


    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }
}
