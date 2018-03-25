package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand;
import org.usfirst.frc.team6027.robot.commands.PneumaticsInitializationCommand;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
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
    private StartingPositionSide startingPositionSide;


    public AutoDeliverToSwitchFrontFromCenterPosition(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingPositionSide = startingSide;
          
        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));
        
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        Command driveToSwitchCmd = createDriveToSwitchCommand();
        Command cubeDeliverCmd = createCubeDeliveryCommand();

        this.addSequential(multiLegDriveCmd);
        this.addSequential(driveToSwitchCmd);
        this.addSequential(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem));
        this.addSequential(cubeDeliverCmd);
    }

    protected Command createCubeDeliveryCommand() {
        Command cmd = new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, this.getPneumaticSubsystem());
        return cmd;
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
        double leg1Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L1-LC-Switch", 40.0) : // Left
                        this.prefs.getDouble("F-L1-RC-Switch", 40.0)   // Right
        );
        
        double leg2Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L2-LC-Switch", 55.0) : // Left
                        this.prefs.getDouble("F-L2-RC-Switch", 35.0)   // Right
        );
        
        double leg2Angle = (this.startingPositionSide == StartingPositionSide.Left ? -1.0 : 1.0) * 90.0;

        double leg3Distance = (
                this.startingPositionSide == StartingPositionSide.Left ? 
                        this.prefs.getDouble("E-L3-LC-Switch", 10.0) : // Left
                        this.prefs.getDouble("F-L3-RC-Switch", 10.0)   // Right
        );
        double leg3Angle = 0.0;

        Command straight1Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), 
                this.getOperatorDisplay(), leg1Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);

        Command straight2Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), 
                this.getOperatorDisplay(), leg2Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);
        Command turn2Command = new TurnCommand(leg2Angle, this.getSensorService(), this.getDrivetrainSubsystem(), 
                this.getOperatorDisplay());

        Command straight3Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), 
                this.getOperatorDisplay(), leg3Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);
        Command turn3Command = new TurnCommand(leg3Angle, this.getSensorService(), this.getDrivetrainSubsystem(),
                this.getOperatorDisplay());
        
        CommandGroup group = new CommandGroup();
        group.addSequential(straight1Cmd);
        group.addSequential(turn2Command);
        group.addSequential(straight2Cmd);
        group.addSequential(turn3Command);
        group.addSequential(straight3Cmd);
        
        return group;
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
