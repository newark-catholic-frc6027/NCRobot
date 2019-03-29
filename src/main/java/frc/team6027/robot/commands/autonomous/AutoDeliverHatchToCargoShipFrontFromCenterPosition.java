package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.SlideMastCommand;
import frc.team6027.robot.commands.ToggleKickHatchCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.commands.SlideMastCommand.SlideMastDirection;
import frc.team6027.robot.commands.autonomous.AutonomousPreference;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDeliverHatchToCargoShipFrontFromCenterPosition extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Field field;


    public AutoDeliverHatchToCargoShipFrontFromCenterPosition(AutonomousPreference cargoShipSide,
            SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, 
            OperatorDisplay operatorDisplay, Field field) {
        
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        this.elevatorSubsystem = elevatorSubsystem;
        this.field = field;

        AutoCommandHelper.addAutoInitCommands(this, drivetrainSubsystem, pneumaticSubsystem, sensorService);

        // Slide mast forward
        this.addParallel(new SlideMastCommand(SlideMastDirection.Forward, 1.0, this.sensorService, this.elevatorSubsystem), 5.0);

        // Off ramp forward
        this.addSequential(new DriveStraightCommand("D-L1-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P1-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // Run Elevator down
        this.addSequential(new ElevatorCommand("rocketHatch.lowerLevel", "rocketHatch.elevator.power", this.sensorService, this.elevatorSubsystem));

        // vision toward cargo ship
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Travel toward cargo ship
        this.addSequential(new DriveStraightCommand("D-L2-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P2-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // vision toward cargo ship
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Travel toward cargo ship
        this.addSequential(new DriveStraightCommand("D-L3-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P3-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        // vision toward cargo ship
        this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));

        // Travel toward cargo ship
        this.addSequential(new DriveStraightCommand("D-L4-Storm-Hatch", DriveDistanceMode.DistanceFromObject, "D-P4-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay)
        );

        
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));
        this.addSequential(new DriveStraightCommand("D-L5-Storm-Hatch", DriveDistanceMode.DistanceReadingOnEncoder, "D-P5-Storm-Hatch", 
            null, this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));
        this.addSequential(new ToggleKickHatchCommand(this.pneumaticSubsystem));
    }

    
    @Override
    public void start() {
        registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }


    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void end() {
        // When it ends peacefully, clean up the Killable command
        this.default_onComplete();
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
