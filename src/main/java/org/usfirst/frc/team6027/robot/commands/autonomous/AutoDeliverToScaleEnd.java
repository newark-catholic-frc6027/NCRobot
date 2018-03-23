package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand;
import org.usfirst.frc.team6027.robot.commands.ElevatorCommand.ElevatorDirection;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToScaleEnd extends CommandGroup {
    
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private DeliverySide deliverySide;


    public AutoDeliverToScaleEnd(DeliverySide deliverySide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, OperatorDisplay operatorDisplay) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.deliverySide = deliverySide;
        this.elevatorSubsystem = elevatorSubsystem;
  
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command elevatorUpCmd = createElevatorCommand();
        Command turnCommand = createTurnCommand();
        Command driveToScaleCmd = createDriveToScaleCommand();
        Command cubeDeliverCmd = createCubeDeliveryCommand();

        this.addSequential(multiLegDriveCmd);
        this.addSequential(elevatorUpCmd);
        this.addSequential(turnCommand);
        this.addSequential(driveToScaleCmd);
        // TODO: need to add command to DROP carriage
        this.addSequential(cubeDeliverCmd);
    }

    protected Command createElevatorCommand() {
        Command cmd = new ElevatorCommand(ElevatorDirection.Up, 1.0, this.getSensorService(), this.getElevatorSubsystem());
        return cmd;
    }
    
    protected Command createCubeDeliveryCommand() {
        Command cmd = new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, this.getPneumaticSubsystem());
        return cmd;
    }


    protected Command createDriveToScaleCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                15.0 /*this.prefs.getDouble("autoDeliverToSwitch.driveDistance", -12.0)*/,
                DriveDistanceMode.DistanceReadingOnEncoder, 
                0.55 /*this.prefs.getDouble("autoDeliverToSwitch.driveToSwitchCmd.power", 0.6)*/
        );

        
        return cmd;
    }


    protected Command createTurnCommand() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.deliverySide == DeliverySide.Left ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = 12.0; //this.prefs.getDouble("leg1.distance", 12.0);
        double leg1Angle = 0.0;     //this.prefs.getDouble("leg1.angle", 0.0);
        double leg2Distance = 47.0; //this.prefs.getDouble("leg2.distance", 47.0);
        double leg2Angle = 30.0 * (this.deliverySide == DeliverySide.Right ? 1.0 : -1.0);// this.prefs.getDouble("leg2.angle", 30.0)
        double leg3Distance = this.prefs.getDouble("leg3.distance", 100.0);
        double leg3Angle = 0.0;     //this.prefs.getDouble("leg3.angle", 0.0);

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

    public ElevatorSubsystem getElevatorSubsystem() {
        return elevatorSubsystem;
    }

    public void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }
}
