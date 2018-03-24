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
        // TODO: drop arm
        this.addSequential(cubeDeliverCmd);
    }

    protected Command createCubeDeliveryCommand() {
        Command cmd = new CubeDeliveryCommand(DeliveryMode.DropThenKick, 10, this.getPneumaticSubsystem());
        return cmd;
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
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = 40.0;//this.prefs.getDouble("leg1.distance", 10.0);
        double leg1Angle = 0.0;//this.prefs.getDouble("leg1.angle", 0.0);
        double leg2Distance = (this.startingPositionSide == StartingPositionSide.Left ? 55.0 : 35.0);//this.prefs.getDouble("leg2.distance", 50.0);
        // Interpreting StartingPostionSide Left here as the side we are delivering to
        double leg2Angle = (this.startingPositionSide == StartingPositionSide.Left ? -1.0 : 1.0) * 90.0;//this.prefs.getDouble("leg2.angle", 0.0);// * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);// this.prefs.getDouble("leg2.angle", 30.0) // 60
        double leg3Distance = 10.0;//this.prefs.getDouble("leg3.distance", 12.0);
        double leg3Angle = 0.0;//this.prefs.getDouble("leg3.angle", 0.0);

        Command straight1Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), leg1Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);

        Command straight2Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), leg2Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);
        Command turn2Command = new TurnCommand(leg2Angle, this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());

        Command straight3Cmd = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), leg3Distance, DriveDistanceMode.DistanceReadingOnEncoder, 0.7);
        Command turn3Command = new TurnCommand(leg3Angle, this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());

        
        CommandGroup group = new CommandGroup();
        group.addSequential(straight1Cmd);
        group.addSequential(turn2Command);
        group.addSequential(straight2Cmd);
        group.addSequential(turn3Command);
        group.addSequential(straight3Cmd);
        
        return group;
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
