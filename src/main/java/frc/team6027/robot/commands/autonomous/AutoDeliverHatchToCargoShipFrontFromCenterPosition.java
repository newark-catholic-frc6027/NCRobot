package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ToggleKickHatchCommand;
import frc.team6027.robot.commands.TurnWhileDrivingCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.TurnWhileDrivingCommand.TargetVector;
import frc.team6027.robot.commands.autonomous.AutonomousPreference;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.VisionDataConstants;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverHatchToCargoShipFrontFromCenterPosition extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StationPosition stationPosition;
    private Field field;


    public AutoDeliverHatchToCargoShipFrontFromCenterPosition(AutonomousPreference cargoShipSide,
            SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, 
            OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.stationPosition = stationPosition;
        this.elevatorSubsystem = elevatorSubsystem;
        this.field = field;
  
        /*
        double "/Preferences/D-L1-Storm-Hatch"=67
double "/Preferences/D-L2-Storm-Hatch"=38
double "/Preferences/D-L3-Storm-Hatch"=17
double "/Preferences/D-L4-Storm-Hatch"=-2
double "/Preferences/D-P1-Storm-Hatch"=0.4
double "/Preferences/D-P2-Storm-Hatch"=0.4
double "/Preferences/D-P3-Storm-Hatch"=0.4
double "/Preferences/D-P4-Storm-Hatch"=0.4

            */

        AutoCommandHelper.addAutoInitCommands(this, drivetrainSubsystem, pneumaticSubsystem, sensorService);

        // Off ramp forward
        this.addSequential(new DriveStraightCommand("D-L1-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P1-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // vision toward cargo ship
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Travel toward cargo ship
        this.addSequential(new DriveStraightCommand("D-L2-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P2-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // vision toward cargo ship
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Travel toward cargo ship
        this.addSequential(new DriveStraightCommand("D-L3-Storm-Hatch", DriveDistanceMode.DistanceFromObject, "D-P3-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));
        this.addSequential(new DriveStraightCommand("D-L4-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P4-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));
        // Last leg to rocket 
        /*
        Command multiLegDriveCmd = createMultiLegDriveCommand();
        this.addSequential(multiLegDriveCmd);
        */
//        this.addSequential(new ElevatorCommand(70.0, 0.6, this.sensorService, this.elevatorSubsystem));
        // TODO: Raise arm to top of rocket
        // TODO: Deliver Hatch
        // TODO: Lower arm to proper position
        // Back up from rocket
/*        
        this.addSequential(new DriveStraightCommand("A-L6-Storm-Hatch", DriveDistanceMode.DistanceFromObject, "A-P6-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );


        // TODO: Set arm to proper position
        // TODO: Pick up hatch
*/        
        

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
        registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

    protected Command makeDelayCommand(int delayMs) {
        Command cmd = new Command() {
            Long elapsedTime = null;
            Long startTime = null;
            @Override
            protected boolean isFinished() {
                boolean finished = false;
                if (this.elapsedTime >= delayMs) {
                    finished = true;
                    this.elapsedTime = null;
                }
                return finished;
            }       

            @Override
            protected void execute() {
                if (elapsedTime == null) {
                    startTime = System.currentTimeMillis();
                }
                elapsedTime = System.currentTimeMillis() - this.startTime;
            }
        };
        return cmd;
    }
    protected Command makeVisionDistanceCommand() {
        Command cmd = new Command() {
            Datahub visionData = DatahubRegistry.instance().get(VisionDataConstants.VISION_DATA_KEY);
            Preferences prefs = Preferences.getInstance();
            Long elapsedTime = null;
            Long startTime = null;
            Double visionDist = null;
            @Override
            protected boolean isFinished() {
                boolean finished = false;
                if (this.elapsedTime >= 350) {
                    finished = true;
                } else {
                    if (this.visionDist >= 0.0) {
                        finished = true;
                    }
                }

                if (finished) {
                    this.prefs.putDouble(VisionDataConstants.TARGET_DISTANCE_KEY, this.visionDist);
                    AutoDeliverHatchToCargoShipFrontFromCenterPosition.this.logger.info("Vision Distance to be used for driving to target: {}", this.visionDist);
                    this.visionDist = null;
                    this.elapsedTime = null;
                    this.startTime = null;
                }
                return finished;
            }

            @Override
            protected void execute() {
                if (elapsedTime == null) {
                    startTime = System.currentTimeMillis();
                }
                elapsedTime = System.currentTimeMillis() - this.startTime;

                this.visionDist = visionData.getDouble(VisionDataConstants.TARGET_DISTANCE_KEY, -1.0);
            }

        };
        return cmd;
    }
    
    protected Command createMultiLegDriveCommand() {
        TargetVector[] turnVectors = new TargetVector[] { 
                new TargetVector(null, "A-L3-1-Storm-Hatch", "A-P3-1-Storm-Hatch"),
                new TargetVector(null, "A-L3-2-Storm-Hatch", "A-P3-2-Storm-Hatch"),
        };
        
        Command cmd = new TurnWhileDrivingCommand(
                this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                turnVectors,
                DriveDistanceMode.DistanceReadingOnEncoder, 0.5
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


    public StationPosition getStationPosition() {
        return stationPosition;
    }


    public void setStationPosition(StationPosition stationPosition) {
        this.stationPosition = stationPosition;
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

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
        this.default_onComplete();
    }

    @Override
    public void end() {
        this.onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }

}
