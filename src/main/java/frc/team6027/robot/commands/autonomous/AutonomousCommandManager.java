package frc.team6027.robot.commands.autonomous;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.ToggleDrivetrainModeCommand;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.field.ObjectSelection;
import frc.team6027.robot.field.OperationSelection;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
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
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Map<String,Command> commandsByName = new HashMap<>();
    private ElevatorSubsystem elevatorSubsystem;

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
            this.drivetrainSubsystem = drivetrainSubsystem;
            this.pneumaticSubsystem = pneumaticSubsystem;
            this.elevatorSubsystem = elevatorSubsystem;
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
                this.currentAutoCommand.kill();
                killSentToCommand = this.currentAutoCommand.getClass().getSimpleName();
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

    public void setCurrent(KillableAutoCommand command) {
        synchronized(this.commandLock) {
            this.currentAutoCommand = command;
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
            case CargoFrontRight:
                chosenCommand = new AutoDeliverHatchToCargoShipFront(
                    autoPreference, position, sensorService, drivetrainSubsystem,
                    pneumaticSubsystem, elevatorSubsystem, operatorDisplay, field
                );
                break;
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
                    position, sensorService, drivetrainSubsystem,
                    pneumaticSubsystem, elevatorSubsystem, operatorDisplay, field
                );
                break;
            case Rocket:
                chosenCommand = new AutoDeliverHatchToRocket(
                    position, sensorService, drivetrainSubsystem,
                    pneumaticSubsystem, elevatorSubsystem, operatorDisplay, field
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
                return new DriverAssistBallDeliveryCommand(this.getLevelSelection(), this.drivetrainSubsystem, 
                    this.elevatorSubsystem, this.sensorService);
            default:
                logger.warn("Cannot select a BallDriverAssistCommand for operation: {}", this.getOperationSelection());
                return NoOpCommand.getInstance();
        }
    }

    protected Command chooseHatchDriverAssistCommand() {
        switch(this.getOperationSelection()) {
            case Deliver:
                return new DriverAssistHatchDeliveryCommand(this.getLevelSelection(), this.drivetrainSubsystem, 
                    this.elevatorSubsystem, this.pneumaticSubsystem, this.sensorService);

            case Pickup:
                return new DriverAssistHatchPickupCommand(this.drivetrainSubsystem, 
                    this.elevatorSubsystem, this.sensorService);
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
        return drivetrainSubsystem;
    }

    protected void setDrivetrainSubsystem(DrivetrainSubsystem drivetrainSubsystem) {
        this.drivetrainSubsystem = drivetrainSubsystem;
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
        return elevatorSubsystem;
    }

    protected void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }
    
	public void initOperatorDisplayCommands() {
        this.getOperatorDisplay().setData("Turn", new TurnCommand("turnButton.angle", this.sensorService, this.drivetrainSubsystem, this.operatorDisplay, "turnButton.power"));
        this.getOperatorDisplay().setData("Reset Elevator Encoder", new Command() {

            @Override
            protected boolean isFinished() {
                return true;
            }

            protected void execute() {
                AutonomousCommandManager.this.getSensorService().getEncoderSensors().getElevatorEncoder().reset();
            }
        });

        this.getOperatorDisplay().setData("Drive Str8 w/Ult", 
            new DriveStraightCommand(this.sensorService, this.drivetrainSubsystem, 
                this.operatorDisplay, 12.0, DriveDistanceMode.DistanceFromObject, 0.7)
        );

        this.getOperatorDisplay().setData("Reset Motor Encoders", new Command() {
            @Override
            protected boolean isFinished() {
                return true;
            }

            protected void execute() {
                AutonomousCommandManager.this.getSensorService().getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft).reset();
                AutonomousCommandManager.this.getSensorService().getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight).reset();
            }
        });

        this.getOperatorDisplay().setData("Hatch", new AutoDeliverHatchToRocketUsingBackwardDeparture(StationPosition.Left, this.sensorService, this.drivetrainSubsystem, 
           this.pneumaticSubsystem, this.elevatorSubsystem, this.operatorDisplay, this.field));

        this.getOperatorDisplay().setData("Vision Turn", new VisionTurnCommand(this.sensorService, this.drivetrainSubsystem, this.operatorDisplay, "visionTurnCommand.power"));

        this.getOperatorDisplay().setData("Elevator", new ElevatorCommand("elevatorCommand.height", "elevatorCommand.power", this.sensorService, this.elevatorSubsystem));
        this.getOperatorDisplay().setData("Toggle Drivetrain Mode", new ToggleDrivetrainModeCommand(this.drivetrainSubsystem));

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
