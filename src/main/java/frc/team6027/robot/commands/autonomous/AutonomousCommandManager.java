package frc.team6027.robot.commands.autonomous;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.field.Field;
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
    private static AutonomousCommandManager instance = new AutonomousCommandManager();
    private boolean initialized = false;
    private KillableAutoCommand currentAutoCommand;

    public enum AutonomousPreference {
        NoPreference("NO SELECTION"),
        Rocket("Rocket"), 
        CargoFront("Cargo Front"),        
        CargoSide("Cargo Side")
        ;
        
        private String displayName;
        
        private AutonomousPreference() {
        }
        
        private AutonomousPreference(String displayName) {
            this.displayName = displayName;
        }
        
        public String displayName() {
            if (this.displayName == null ) {
                return this.name();
            } else {
                return this.displayName;
            }
        }
        
        public static AutonomousPreference fromDisplayName(String displayName) {
            return Arrays.asList(AutonomousPreference.values()).stream().filter(a -> displayName.equals(a.displayName())).findFirst().orElse(null);
        }
        
    }
    
    private Field field;
    private AutonomousPreference preferredAutoScenario;
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Map<String,Command> commandsByName = new HashMap<>();
    private ElevatorSubsystem elevatorSubsystem;
    
    public static AutonomousCommandManager instance() {
        return instance;    
    }

    private AutonomousCommandManager() {
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
        if (this.currentAutoCommand != null) {
            this.logger.warn("Kill signal set on command {}", this.currentAutoCommand.getClass().getSimpleName());
            this.currentAutoCommand.kill();
        }
    }

    public void setCurrent(KillableAutoCommand command) {
        this.currentAutoCommand = command;
    }

    
    public static void initAutoScenarioDisplayValues(OperatorDisplay operatorDisplay) {
        operatorDisplay.registerAutoScenario(AutonomousPreference.Rocket.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.CargoSide.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.CargoFront.displayName());
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

        if (autoPreference == AutonomousPreference.CargoFront) {
            chosenCommand = new AutoDeliverHatchToCargoShipFront(
                position, sensorService, drivetrainSubsystem,
                pneumaticSubsystem, elevatorSubsystem, operatorDisplay, field
            );
        } else {
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
            case CargoFront:
                logger.warn("CANNOT DELIVER TO CARGO FRONT FROM LEFT OR RIGHT STATION, choosing NoOpCommand!");
            default:
                chosenCommand = NoOpCommand.getInstance();    
        }

        return chosenCommand;
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
        this.getOperatorDisplay().setData("Turn", new TurnCommand("turnButton.angle", this.sensorService, this.drivetrainSubsystem, this.operatorDisplay));
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

 	}
                       
}
