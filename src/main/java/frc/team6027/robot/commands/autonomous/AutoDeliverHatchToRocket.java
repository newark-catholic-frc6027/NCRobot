package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.SlideMastCommand;
import frc.team6027.robot.commands.ToggleKickHatchCommand;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.SlideMastCommand.SlideMastDirection;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverHatchToRocket extends CommandGroup implements KillableAutoCommand  {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private StationPosition stationPosition;
    private Field field;


    public AutoDeliverHatchToRocket(StationPosition stationPosition, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, 
            OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.stationPosition = stationPosition;
        this.elevatorSubsystem = elevatorSubsystem;
        this.field = field;
  
        AutoCommandHelper.addAutoInitCommands(this, drivetrainSubsystem, pneumaticSubsystem, sensorService);

        // Slide mast forward
        this.addParallel(new SlideMastCommand(SlideMastDirection.Forward, 1.0, this.sensorService, this.elevatorSubsystem), 5.0);

        // Off ramp forward
        this.addSequential(new DriveStraightCommand("B-L1-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "B-P1-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // Turn toward rocket
        String turnPrefName = this.stationPosition == StationPosition.Left ? "B-A1-Storm-Hatch" : "B-A1-Right-Storm-Hatch";
        //String turnPrefName = "pit.turnCommand.angle";
        this.addSequential(new TurnCommand(turnPrefName, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay, 
          "B-A1P-Storm-Hatch"));

        // Run Elevator down
        this.addSequential(new ElevatorCommand("rocketHatch.lowerLevel", "rocketHatch.elevator.power", this.sensorService, this.elevatorSubsystem));

        // Travel toward rocket
        this.addSequential(new DriveStraightCommand("B-L2-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "B-P2-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // Let Vison settle down
        this.addSequential(new DelayCommand(500));

        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay), 1.0);

        // Travel toward rocket
        this.addSequential(new DriveStraightCommand("B-L3-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "B-P3-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay), 1.0);
        // Travel toward rocket
        this.addSequential(new DriveStraightCommand("B-L4-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "B-P4-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay), 1.0);

        //Approach rocket
        this.addSequential(new DriveStraightCommand("B-L5-Storm-Hatch", 
            DriveStraightCommand.DriveDistanceMode.DistanceFromObject, 
            "B-P5-Storm-Hatch", null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Kick the hatch
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));
        // Back up from rocket
        this.addSequential(new DriveStraightCommand("B-L6-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "B-P6-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));
        // Retract kickers
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));

    }
    
    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

    protected void reset() {
    }

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
        this.reset();
//        this.default_onComplete();
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

}
