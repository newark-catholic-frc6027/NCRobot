package frc.team6027.robot.commands.autonomous;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.ResetElevatorEncoderCommand;
import frc.team6027.robot.commands.ResetGyroCommand;
import frc.team6027.robot.commands.ResetMotorEncodersCommand;
import frc.team6027.robot.commands.SlideMastCommand;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.SlideMastCommand.SlideMastDirection;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.field.ObjectSelection;
import frc.team6027.robot.field.OperationSelection;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class AutonomousCommandManager {
    private final Logger logger = LogManager.getLogger(getClass());

    public static final ObjectSelection DEFAULT_OBJECT_SELECTION = ObjectSelection.Hatch;
    public static final OperationSelection DEFAULT_OPERATION_SELECTION = OperationSelection.Deliver;
    public static final LevelSelection DEFAULT_LEVEL_SELECTION = LevelSelection.Lower;

    private static AutonomousCommandManager instance = new AutonomousCommandManager();

    private Field field;
    private AutonomousPreference preferredAutoScenario;
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrain;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Map<String,Command> commandsByName = new HashMap<>();
    private ElevatorSubsystem elevator;

    private boolean initialized = false;
    private KillableAutoCommand currentAutoCommand;
    private Object commandLock = new Object();

    private ObjectSelection objectSelection = DEFAULT_OBJECT_SELECTION;
    private LevelSelection levelSelection = DEFAULT_LEVEL_SELECTION;
    private OperationSelection operationSelection = DEFAULT_OPERATION_SELECTION;

    public static AutonomousCommandManager instance() {
        return instance;    
    }

    private AutonomousCommandManager() {
        this.resetDriverSelections();
    }
    
    public AutonomousCommandManager initialize(AutonomousPreference preferredAutoScenario, Field field, 
            SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, 
            ElevatorSubsystem elevatorSubsystem, OperatorDisplay operatorDisplay) {
        this.preferredAutoScenario = preferredAutoScenario;
        this.field = field;
        if (! this.initialized) {
            this.sensorService = sensorService;
            this.drivetrain = drivetrainSubsystem;
            this.pneumaticSubsystem = pneumaticSubsystem;
            this.elevator = elevatorSubsystem;
            this.operatorDisplay = operatorDisplay;
        }
        
        return this;
    }

    /**
     * Sends a kill signal to current Auto command
     */
    public void killCurrent() {
        String killSentToCommand = null;
        synchronized(this.commandLock) {
            if (this.currentAutoCommand != null) {
                killSentToCommand = this.currentAutoCommand.getClass().getSimpleName();
                this.currentAutoCommand.kill();
            }
        }
        if (killSentToCommand != null) {
            this.logger.warn("Kill signal sent to command {}", killSentToCommand);
        } else {
            this.logger.info("No KillableAutoCommand to kill");
        }

    }

    public void resetDriverSelections() {
        this.objectSelection = DEFAULT_OBJECT_SELECTION;
        this.levelSelection = DEFAULT_LEVEL_SELECTION;
        this.operationSelection = DEFAULT_OPERATION_SELECTION;
    }

    protected KillableAutoCommand getCurrent() {
        return this.currentAutoCommand;
    }

    public String currentCommandId() {
        KillableAutoCommand currCmd = this.getCurrent();
        return currCmd != null ? Integer.toHexString(currCmd.hashCode()) : null;
    }
    
    public void setCurrent(KillableAutoCommand command) {
        synchronized(this.commandLock) {
            this.currentAutoCommand = command;
        }
    }

    public void unsetCurrent(KillableAutoCommand command) {
        synchronized(this.commandLock) {
            if (this.currentAutoCommand == command) {      
                this.currentAutoCommand = null;
            }
        }
    }

    public boolean isAutoCommandRunning() {
        synchronized(this.commandLock) {
            return this.currentAutoCommand != null;
        }
    }

    public boolean isAutoCommandRunning(Class<? extends Command> ofThisType) {
        synchronized(this.commandLock) {
            return this.currentAutoCommand != null && ofThisType == this.currentAutoCommand.getClass();
        }
    }

    
    public static void initAutoScenarioDisplayValues(OperatorDisplay operatorDisplay) {
        operatorDisplay.registerAutoScenario(AutonomousPreference.Rocket.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.CargoSide.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.CargoFrontLeft.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.CargoFrontRight.displayName());
    }

    public Command getCommandByName(String commandName) {
        return this.commandsByName.get(commandName);
    }
    
    public Command chooseCommand() {
        logger.info(">>>>>> Station Position: {}, Scenario: {}", this.getField().getOurStationPosition(), this.preferredAutoScenario);
        
        if (this.field.getOurStationPosition() == null) {
            logger.warn("NO POSITION SELECTED, cannot choose an Autonomous command!");
            return NoOpCommand.getInstance();
        }

        if (this.getPreferredScenario() == null) {
            logger.warn("NO AUTONOMOUS SCENARIO SELECTED, cannot choose an Autonomous command!");
            return NoOpCommand.getInstance();
        }
        
        Command chosenCommand = null;
        
        // If we are positioned in the center station, station 2
        if (this.field.isOurStationCenter()) {
            chosenCommand = chooseCenterStationCommand();
        } else {
            chosenCommand = chooseEndStationCommand();
        }
        
        if (chosenCommand == null) {
            chosenCommand = NoOpCommand.getInstance();
            logger.warn("!!! A command could not be automatically chosen, choosing NoOpCommand");
        } else {
            logger.info(">>> Command <{}> was automatically chosen", chosenCommand.getName()); 
        }
        return chosenCommand;
        
    }
    
    public Command chooseCenterStationCommand() {
        AutonomousPreference autoPreference = this.getPreferredScenario();
        StationPosition position = this.field.getOurStationPosition();
        Command chosenCommand = null;

        switch (autoPreference) {
            case CargoFrontLeft:
                chosenCommand = new AutoDeliverHatchToCargoShipFrontFromCenterPosition(
                    autoPreference, sensorService, drivetrain,
                    pneumaticSubsystem, elevator, operatorDisplay, field
                );
                break;
            case CargoFrontRight:
            default:
                logger.warn("CANNOT DELIVER TO '{}' FROM CENTER STATION, choosing NoOpCommand!", autoPreference);
                chosenCommand = NoOpCommand.getInstance();    
        }

        return chosenCommand;
    }

    public Command chooseEndStationCommand() {
        AutonomousPreference autoPreference = this.getPreferredScenario();
        StationPosition position = this.field.getOurStationPosition();

        Command chosenCommand = null;
        switch (autoPreference) {
            case CargoSide:
                chosenCommand = new AutoDeliverHatchToCargoShipSide(
                    position, sensorService, drivetrain,
                    pneumaticSubsystem, elevator, operatorDisplay, field
                );
                break;
            case Rocket:
                chosenCommand = new AutoDeliverHatchToRocket(
                    position, sensorService, drivetrain,
                    pneumaticSubsystem, elevator, operatorDisplay, field
                );
                break;
            case CargoFrontLeft:
            case CargoFrontRight:
                logger.warn("CANNOT DELIVER TO CARGO FRONT FROM LEFT OR RIGHT STATION, choosing NoOpCommand!");
            default:
                chosenCommand = NoOpCommand.getInstance();    
        }

        return chosenCommand;
    }

    public Command chooseDriverAssistCommand() {
        if (this.getObjectSelection() == ObjectSelection.Ball) {
            return this.chooseBallDriverAssistCommand();
        } else if (this.getObjectSelection() == ObjectSelection.Hatch) {
            return this.chooseHatchDriverAssistCommand();
        } else {
            logger.warn("Cannot select a DriverAssistCommand for object: {}", this.getObjectSelection());
            return NoOpCommand.getInstance();
        }
    }

    protected Command chooseBallDriverAssistCommand() {
        switch (this.getOperationSelection()) {
            case Deliver:
                return new DriverAssistBallDeliveryCommand(this.getLevelSelection(), this.drivetrain, 
                    this.elevator, this.sensorService);
            default:
                logger.warn("Cannot select a BallDriverAssistCommand for operation: {}", this.getOperationSelection());
                return NoOpCommand.getInstance();
        }
    }

    protected Command chooseHatchDriverAssistCommand() {
        switch(this.getOperationSelection()) {
            case Deliver:
                return new DriverAssistHatchDeliveryCommand(this.getLevelSelection(), this.drivetrain, 
                    this.elevator, this.pneumaticSubsystem, this.sensorService, this.operatorDisplay);

            case Pickup:
                return new DriverAssistHatchPickupCommand(this.drivetrain, 
                    this.elevator, this.sensorService, this.operatorDisplay);
                    
            default:
                logger.warn("Cannot select a HatchDriverAssistCommand for operation: {}", this.getOperationSelection());
                return NoOpCommand.getInstance();
        }

    }

    public boolean isNoPreferredScenario() {
        return ! isPreferredScenario();
    }
    public boolean isPreferredScenario() {
        return this.preferredAutoScenario != null && this.preferredAutoScenario != AutonomousPreference.NoPreference;
    }
    
    public AutonomousPreference getPreferredScenario() {
        return this.preferredAutoScenario;
    }

    public void setPreferredScenario(AutonomousPreference preferredAutoScenario) {
        this.preferredAutoScenario = preferredAutoScenario;
    }

    protected Field getField() {
        return field;
    }

    protected void setField(Field field) {
        this.field = field;
    }

    protected SensorService getSensorService() {
        return sensorService;
    }

    protected void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    protected DrivetrainSubsystem getDrivetrainSubsystem() {
        return drivetrain;
    }

    protected void setDrivetrainSubsystem(DrivetrainSubsystem drivetrainSubsystem) {
        this.drivetrain = drivetrainSubsystem;
    }

    protected PneumaticSubsystem getPneumaticSubsystem() {
        return pneumaticSubsystem;
    }

    protected void setPneumaticSubsystem(PneumaticSubsystem pneumaticSubsystem) {
        this.pneumaticSubsystem = pneumaticSubsystem;
    }

    protected OperatorDisplay getOperatorDisplay() {
        return operatorDisplay;
    }

    protected void setOperatorDisplay(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;
    }

    protected ElevatorSubsystem getElevatorSubsystem() {
        return elevator;
    }

    protected void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevator = elevatorSubsystem;
    }
    
	public void initOperatorDisplayCommands() {
        this.getOperatorDisplay().setData("Turn", new TurnCommand("turnButton.angle", this.sensorService, this.drivetrain, this.operatorDisplay, "turnButton.power"));
        this.getOperatorDisplay().setData("Reset Elevator Encoder", new ResetElevatorEncoderCommand(this.sensorService));
        this.getOperatorDisplay().setData("Reset Motor Encoders", new ResetMotorEncodersCommand(this.sensorService));
        this.getOperatorDisplay().setData("Reset Gyro", new ResetGyroCommand(this.sensorService));

        this.getOperatorDisplay().setData("Run Auto", this.chooseCommand());
        this.getOperatorDisplay().setData("Run DriverAssist", 
            new ScheduleCommand<Command>(
                () -> this.chooseDriverAssistCommand(),
                Command.class,
                true
            )
        );

        this.getOperatorDisplay().setData("Vision Turn", 
            new ScheduleCommand<VisionTurnCommand>(
                () -> new VisionTurnCommand(this.sensorService, this.drivetrain, this.operatorDisplay, "visionTurnCommand.power", 1.0), 
                VisionTurnCommand.class,
                true
            )
        );

        this.getOperatorDisplay().setData("Elevator", new ElevatorCommand("elevatorCommand.height", "elevatorCommand.power", this.sensorService, this.elevator));
        this.getOperatorDisplay().setData("Slide Mast", new SlideMastCommand(SlideMastDirection.Forward, 1.0, this.sensorService, this.elevator));

 	}

    public ObjectSelection getObjectSelection() {
        return this.objectSelection;
    }
	public void setObjectSelection(ObjectSelection objectSelection) {
        this.objectSelection = objectSelection;
	}

    public LevelSelection getLevelSelection() {
        return this.levelSelection;
    }

	public void setLevelSelection(LevelSelection levelSelection) {
        this.levelSelection = levelSelection;
	}

    public OperationSelection getOperationSelection() {
        return this.operationSelection;
    }

	public void setOperationSelection(OperationSelection operationSelection) {
        this.operationSelection = operationSelection;
	}
                       
}
