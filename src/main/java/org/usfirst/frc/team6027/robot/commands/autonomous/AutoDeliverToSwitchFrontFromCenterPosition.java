package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand;
import org.usfirst.frc.team6027.robot.commands.PneumaticsInitializationCommand;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverToSwitchFrontFromCenterPosition extends CommandGroup {
    
    
    
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private DeliverySide deliverySide;


    public AutoDeliverToSwitchFrontFromCenterPosition(DeliverySide deliverySide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.deliverySide = deliverySide;
          
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));
        
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();
        Command cubeDeliverCmd = createCubeDeliveryCommand();


        this.addSequential(multiLegDriveCmd);
        this.addSequential(driveToSwitchCmd);
        // TODO: add drop carriage command
        this.addSequential(cubeDeliverCmd);
    }

    protected Command createCubeDeliveryCommand() {
        Command cmd = new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, this.getPneumaticSubsystem());
        return cmd;
    }

    protected Command createDriveToSwitchCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                -12.0, //this.prefs.getDouble("autoDeliverToSwitch.driveDistance", -12.0),
                DriveDistanceMode.DistanceFromObject, 
                0.55   //this.prefs.getDouble("autoDeliverToSwitch.driveToSwitchCmd.power", 0.6)
        );

        
        return cmd;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = this.prefs.getDouble("leg1.distance", 10.0); // 10
        double leg1Angle = this.prefs.getDouble("leg1.angle", 0.0); // 0
        double leg2Distance = this.prefs.getDouble("leg2.distance", 50.0); // 70
        double leg2Angle = (this.deliverySide == DeliverySide.Left ? 1.0 : -1.0) * this.prefs.getDouble("leg2.angle", 0.0);// * (this.deliverySide == DeliverySide.Left ? 1.0 : -1.0);// this.prefs.getDouble("leg2.angle", 30.0) // 60
        double leg3Distance = this.prefs.getDouble("leg3.distance", 12.0);
        double leg3Angle = this.prefs.getDouble("leg3.angle", 0.0);

        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                new TargetVector(leg2Angle, leg2Distance),
                new TargetVector(leg3Angle, leg3Distance),
                
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.6
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
