package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;
import frc.team6027.robot.commands.ResetSensorsCommand;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.TurnWhileDrivingCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.TurnWhileDrivingCommand.TargetVector;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverHatch extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StartingPositionSide startingSide;
    private Field field;


    public AutoDeliverHatch(StartingPositionSide startingSide, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, 
            OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.startingSide = startingSide;
        this.elevatorSubsystem = elevatorSubsystem;
        this.field = field;
  
//        this.addSequential(new PneumaticsInitializationCommand(this.pneumaticSubsystem));
/*
        this.addSequential( new DriveStraightCommand(this.sensorService, this.drivetrainSubsystem, 
            this.operatorDisplay, 36.0, DriveDistanceMode.DistanceFromObject, 0.5)
        );
*/       
        /*
        double leg1Distance = this.prefs.getDouble("A-L1-Storm-Hatch", -48.0);
        double leg1Angle = 0.0;
        double leg2Distance = this.prefs.getDouble("A-L2-Storm-Hatch", 47.0);
        double leg2Angle = this.prefs.getDouble("A-A1-Storm-Hatch", 120.0);//30.0 * (this.startingSide == StartingPositionSide.Right ? 1.0 : -1.0);
        //double leg3Distance = this.prefs.getDouble("A-L3-SS-Scale", 220.0);
        //double leg3Angle = 0.0;

        TargetVector[] targetVectors = new TargetVector[] { 
                new TargetVector(leg1Angle, leg1Distance),
                new TargetVector(leg2Angle, leg2Distance),
                //new TargetVector(leg3Angle, leg3Distance),
                
        };

        this.addSequential(new TurnWhileDrivingCommand(sensorService, drivetrainSubsystem, operatorDisplay, targetVectors, 
            DriveDistanceMode.DistanceReadingOnEncoder, .4));
            */

        // TODO: Initialize Pneumatics
        this.addSequential(new ResetSensorsCommand(this.sensorService));
        this.addSequential(new DriveStraightCommand("A-L1-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "A-P1-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        this.addSequential(new TurnCommand("A-A1-Storm-Hatch", this.sensorService, this.drivetrainSubsystem, this.operatorDisplay, "A-A1P1-Storm-Hatch"));

        this.addSequential(new DriveStraightCommand("A-L2-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "A-P2-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );
        this.addSequential(new TurnCommand("A-A2-Storm-Hatch", this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // TODO: replace this with Vision commands
        this.addSequential(new DriveStraightCommand("A-L3-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "A-P3-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        this.addSequential(new ElevatorCommand(70.0, 0.6, this.sensorService, this.elevatorSubsystem));
        // TODO: Ensure arm isn't blocking camera
        // TODO: Drive to rocket with vision
        // TODO: Raise arm to top of rocket
        // TODO: Deliver Hatch
        // TODO: Lower arm to proper position
/*
        this.addSequential(new Command() {
            long finishTime = -1;
            @Override
            public void start() {
                finishTime = System.currentTimeMillis() + 500;
            }
            @Override
            public void execute() {
                if (finishTime == -1) {
                    finishTime = System.currentTimeMillis() + 500;
                }
            }

            @Override
            protected boolean isFinished() {
                boolean finished = System.currentTimeMillis() >= finishTime;
                if (finished) {
                    finishTime = -1;
                }
                return finished;
            }

        });
        */
        // Back up from rocket
        this.addSequential(new DriveStraightCommand("A-L4-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "A-P4-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );
        this.addSequential(new TurnCommand("A-A3-Storm-Hatch", this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        this.addSequential(new DriveStraightCommand("A-L5-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "A-P5-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );
        this.addSequential(new DriveStraightCommand("A-L6-Storm-Hatch", DriveDistanceMode.DistanceFromObject, "A-P6-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );


        // TODO: Set arm to proper position
        // TODO: Pick up hatch
        
        // -> TODO: Back up using encoder
        // -> TODO: Turn to face rocket
        // -> TODO: Drive to rocket using encoder
        
        // TODO: Ensure arm isn't blocking camera
        // TODO: Drive to rocket with vision
        // TODO: Raise arm to top of rocket
        // TODO: Deliver Hatch

//        Command multiLegDriveCmd = createMultiLegDriveCommand();
//        Command turnCommand = createTurnCommand();
//        Command driveToScaleCmd = createDriveToScaleCommand();
                
//        this.addSequential(multiLegDriveCmd);
//        this.addSequential(turnCommand);  // Turn toward scale
        // Elevator should already be shifted into high gear
        // Drop carriage down
//        this.addParallel(AutoCommandHelper.createDropCarriageForDeliveryCommand(this.pneumaticSubsystem, field));
        // Run elevator back up
//        this.addParallel(AutoCommandHelper.createElevatorUpForDeliveryCommand(this.elevatorSubsystem, this.drivetrainSubsystem, this.getSensorService()));
        // Drive to scale and eject
//        this.addSequential(driveToScaleCmd);
//        this.addSequential(AutoCommandHelper.createCubeDeliveryCommand(this.getPneumaticSubsystem(), field));
    }
/*
    protected Command createDriveToScaleCommand() {
        Command cmd = new DriveStraightCommand(
                this.sensorService, this.drivetrainSubsystem, this.operatorDisplay,
                this.prefs.getDouble("A-L4-SS-Scale", 30.0),
                DriveDistanceMode.DistanceReadingOnEncoder, 
                0.55
        );
        
        return cmd;
    }
*/

    @Override
    public void start() {
        this.logger.info(">>>>>>>>>>>>>>>>>>>> AutoDeliverHatch command starting...");
        super.start();
    }

    protected Command createTurnCommand() {
        // When delivering to the left, need to turn robot to the right.  When delivering to the right, need to turn
        // robot left
        double angle = 90.0 * (this.startingSide == StartingPositionSide.Left ? 1.0 : -1.0);
        
        Command returnCommand = new TurnCommand(angle, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay);
        return returnCommand;
    }
    
    protected Command createMultiLegDriveCommand() {
        double leg1Distance = this.prefs.getDouble("A-L1-Storm-Hatch", -48.0);
        double leg1Angle = 0.0;
        double leg2Distance = this.prefs.getDouble("A-L2-Storm-Hatch", 47.0);
        double leg2Angle = 30.0 * (this.startingSide == StartingPositionSide.Right ? 1.0 : -1.0);
        double leg3Distance = this.prefs.getDouble("A-L3-SS-Scale", 220.0);
        double leg3Angle = 0.0;

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


    public StartingPositionSide getStaringPositionSide() {
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
