package org.usfirst.frc.team6027.robot.commands.autonomous;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.field.Field.PlatePosition;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class AutonomousCommandManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum DontDoOption {
        NoPreference("NO SELECTION"),
        DontCrossField("Don't Cross the Field");
        
        private String displayName;
        
        private DontDoOption() {
        }
        
        private DontDoOption(String displayName) {
            this.displayName = displayName;
        }
        
        public String displayName() {
            if (this.displayName == null ) {
                return this.name();
            } else {
                return this.displayName;
            }
        }
        
        public static DontDoOption fromDisplayName(String displayName) {
            return Arrays.asList(DontDoOption.values()).stream().filter(a -> displayName.equals(a.displayName())).findFirst().orElse(null);
        }
    }
    
    public enum AutonomousPreference {
        NoPreference("NO SELECTION"),
        CrossLine("Cross the Line"),
        DeliverToSwitch("Deliver to Switch"),
        DeliverToScaleEnd("Deliver to Scale END");
        
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
    private DontDoOption dontDoOption = DontDoOption.NoPreference;
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Map<String,Command> commandsByName = new HashMap<>();
    private ElevatorSubsystem elevatorSubsystem;
    
    // SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
    // OperatorDisplay operatorDisplay
    public AutonomousCommandManager(AutonomousPreference preferredAutoScenario, Field field, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevatorSubsystem, OperatorDisplay operatorDisplay) {
        this.preferredAutoScenario = preferredAutoScenario;
        this.field = field;
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.operatorDisplay = operatorDisplay;
    }
    
    public static void initAutoScenarioDisplayValues(OperatorDisplay operatorDisplay) {
        operatorDisplay.registerAutoScenario(AutonomousPreference.CrossLine.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToSwitch.displayName());
//        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToSwitchFront.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToScaleEnd.displayName());
    }

    public static void initDontDoOptionDisplayValues(OperatorDisplay operatorDisplay) {
        operatorDisplay.registerDontDoOption(DontDoOption.DontCrossField.displayName());
    }

    public Command chooseEndStationCommand(int stationPosition) {

        // If override = NO SELECTION AND dontDoOption = NO SELECTION
        //   FULL AUTO: 1 -> Scale our side
        //              2 -> Switch our side
        //              3 -> Scale opposite side
        //              4 -> Switch opposite side   // unreachable
        //              5 -> Cross line             // unreachable
        
        // If override = SCALE AND dontDoOption  = NO SELECTION
        //              1 -> Scale our side
        //              2 -> Scale opposite side
        //              3 -> Switch our side        // unreachable
        //              4 -> Switch opposite side   // unreachable
        //              5 -> Cross line             // unreachable
        
        // If override = SCALE AND dontDoOption = DONT CROSS
        //              1 -> Scale our side
        //              2 -> Switch our side
        //              3 -> Cross line
        
        // If override = SWITCH AND dontDoOption  = NO SELECTION
        //              1 -> Switch our side
        //              2 -> Switch opposite side
        //              3 -> Scale our side         // unreachable
        //              4 -> Scale opposite side    // unreachable
        //              5 -> Cross line             // unreachable

        // If override = SWITCH AND dontDoOption  = DONT CROSS
        //              1 -> Switch our side
        //              2 -> Scale our side
        //              3 -> Cross line

        // If override = CROSS LINE
        //              1 -> Cross the line
        
        Command chosenCommand = null;
        if (stationPosition != 1 && stationPosition != 3) {
            logger.error("Position {} is not a valid position for chooseEndStationCommand!  No command will be chosen.");
            return null;
        }
        
        AutonomousPreference autoPreference = this.getPreferredScenario();
        DontDoOption dontDoOption = this.getDontDoOption();
        boolean scaleIsOnOurSide = (this.getField().isPlateAssignedToUs(PlatePosition.ScaleLeft) && stationPosition == 1) 
                               || (this.getField().isPlateAssignedToUs(PlatePosition.ScaleRight) && stationPosition == 3);
        boolean switchIsOnOurSide = (this.getField().isPlateAssignedToUs(PlatePosition.OurSwitchLeft) && stationPosition == 1) 
                || (this.getField().isPlateAssignedToUs(PlatePosition.OurSwitchRight) && stationPosition == 3);
        
        
        StartingPositionSide startingSide = stationPosition == 1 ? StartingPositionSide.Left : StartingPositionSide.Right;

        logger.info("Choosing a {} station command...", startingSide);
        
        logger.info("Scale on our side? {}, switch on our side? {}, startingSide: {}", scaleIsOnOurSide, switchIsOnOurSide, startingSide);
        logger.info("autonomousPreference: {}, dontDoOption: {}", autoPreference, dontDoOption);
        
        if (autoPreference == AutonomousPreference.NoPreference && dontDoOption == DontDoOption.NoPreference) {
            if (scaleIsOnOurSide) {
                logger.info("Delivering to SCALE on OUR SIDE (1)...");
                chosenCommand = this.makeDeliverToScaleEndCommand(startingSide);
            } else if (switchIsOnOurSide) {
                logger.info("Delivering to SWITCH on OUR SIDE (1)...");
                chosenCommand = this.makeDeliverToSwitchEndCommand(startingSide);
            } else { // going with opposite scale
                logger.info("Delivering to SCALE on OPPOSITE SIDE (1)...");
                chosenCommand = this.makeDeliverToScaleEndFromOppositeSideCommand(startingSide);
            } 
        } else if (autoPreference == AutonomousPreference.DeliverToScaleEnd && dontDoOption == DontDoOption.NoPreference) {
            if (scaleIsOnOurSide) {
                logger.info("Delivering to SCALE on OUR SIDE (2)...");
                chosenCommand = this.makeDeliverToScaleEndCommand(startingSide);
            } else { // going with opposite scale
                logger.info("Delivering to SCALE on OPPOSITE SIDE (2)...");
                chosenCommand = this.makeDeliverToScaleEndFromOppositeSideCommand(startingSide);
            } 
            
        } else if (autoPreference == AutonomousPreference.DeliverToScaleEnd && dontDoOption == DontDoOption.DontCrossField) {
            if (scaleIsOnOurSide) {
                logger.info("Delivering to SCALE on OUR SIDE (3)...");
                chosenCommand = this.makeDeliverToScaleEndCommand(startingSide);
            } else if (switchIsOnOurSide) {
                logger.info("Delivering to SWITCH on OUR SIDE (3)...");
                chosenCommand = this.makeDeliverToSwitchEndCommand(startingSide);
            } else { // cross line
                logger.info("CROSSING LINE (3)...");
                chosenCommand = this.makeCrossLineFromEndPositionCommand();
            } 
            
        } else if (autoPreference == AutonomousPreference.DeliverToSwitch && dontDoOption == DontDoOption.NoPreference) {
            if (switchIsOnOurSide) {
                logger.info("Delivering to SWITCH on OUR SIDE (4)...");
                chosenCommand = this.makeDeliverToSwitchEndCommand(startingSide);
            } else { // switch opposite side
                logger.info("Delivering to SWITCH on OPPOSITE SIDE (4)...");
                chosenCommand = this.makeDeliverToSwitchEndFromOppositeSideCommand(startingSide);
            } 
            
        } else if (autoPreference == AutonomousPreference.DeliverToSwitch && dontDoOption == DontDoOption.DontCrossField) {
            if (switchIsOnOurSide) {
                logger.info("Delivering to SWITCH on OUR SIDE (5)...");
                chosenCommand = this.makeDeliverToSwitchEndCommand(startingSide);
            } else if (scaleIsOnOurSide) {
                logger.info("Delivering to SCALE on OUR SIDE (5)...");
                chosenCommand = this.makeDeliverToScaleEndCommand(startingSide);
            } else { // cross line
                logger.info("CROSSING LINE (5)...");
                chosenCommand = this.makeCrossLineFromEndPositionCommand();
            } 
            
        } else if (autoPreference == AutonomousPreference.CrossLine) {
            logger.info("CROSSING LINE (6)...");
            chosenCommand = this.makeCrossLineFromEndPositionCommand();
        } else {
            logger.error("What happened? Should never get here. No command selected.");
        }
        
        return chosenCommand;
    }
    

    public Command chooseCenterStationCommand() {
        logger.info("Choosing a CENTER station command...");
        Command chosenCommand = null;
        if (this.getField().getOurStationPosition() != 2) {
            logger.error("Position {} is not a valid position for chooseCenterStationCommand!  No command will be chosen.");
            return null;
        }
        
        boolean ourSwitchAssignmentIsOnRight = this.getField().isPlateAssignedToUs(PlatePosition.OurSwitchRight);
        
        // If Override != CROSS LINE
        //              1 -> Switch Right
        //              2 -> Switch Left

        // If Override = CROSS LINE
        //              1 -> Cross Line
        AutonomousPreference autoPreference = this.getPreferredScenario();
        
        logger.info("Our switch assignment: {}", ourSwitchAssignmentIsOnRight ? "RIGHT" : "LEFT");
        logger.info("autonomousPreference: {}, dontDoOption: {}", autoPreference, dontDoOption);
        
        if (autoPreference != AutonomousPreference.CrossLine) {
            if (ourSwitchAssignmentIsOnRight) {
                chosenCommand = this.makeDeliverToSwitchFromCenterCommand(StartingPositionSide.Right);
            } else {
                chosenCommand = this.makeDeliverToSwitchFromCenterCommand(StartingPositionSide.Left);
            }
        } else {
            chosenCommand = this.makeCrossLineFromCenterCommand();
        }
        
        return chosenCommand;
    }

    /* Experimental code, delete soon
    protected void createAutonomousCommands() {
        
        // TODO: May want to defer creating these until we know which command we are going to need to run
        // based on other inputs
        Command driveStraightCmd =  new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), 
            this.getOperatorDisplay(), this.prefs.getDouble("driveStraightCommand.driveDistance", 12.0), 
            DriveDistanceMode.DistanceReadingOnEncoder, 0.5);
        this.commandsByName.put(driveStraightCmd.getName(), driveStraightCmd);
        
        Command turnCommand = new TurnCommand(this.prefs.getDouble("turnCommand.targetAngle", 90.0), 
            this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());
        this.commandsByName.put(turnCommand.getName(), turnCommand);

        
    }
*/
    public Command getCommandByName(String commandName) {
        return this.commandsByName.get(commandName);
    }
    
    public Command chooseCommand() {
        
        logger.info(">>>>>> Station Position: {}, Scenario: {}", this.getField().getOurStationPosition(), this.preferredAutoScenario);
        
        if (this.field.getOurStationPosition() <= 0) {
            logger.warn("NO POSITION SELECTED, cannot choose an Autonomous command!");
            return NoOpCommand.getInstance();
        }
        
        Command chosenCommand = null;
        
        // If we are positioned in the center station, station 2
        if (this.field.isOurStationCenter()) {
            chosenCommand = chooseCenterStationCommand();
        } else {
            chosenCommand = chooseEndStationCommand(this.getField().getOurStationPosition());
        }
        
        if (chosenCommand == null) {
            chosenCommand = NoOpCommand.getInstance();
            logger.warn("!!! A command could not be automatically chosen, choosing NoOpCommand");
        } else {
            logger.info(">>> Command <{}> was automatically chosen", chosenCommand.getName()); 
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

    public DontDoOption getDontDoOption() {
        return dontDoOption;
    }

    public void setDontDoOption(DontDoOption dontDoOption) {
        this.dontDoOption = dontDoOption;
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
    
    private Command makeDeliverToScaleEndCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToScaleEnd(
                startingSide,
                this.sensorService, this.getDrivetrainSubsystem(), this.getPneumaticSubsystem(),
                this.getElevatorSubsystem(),
                this.operatorDisplay
        );    
    }

    /**
     * Used for crossing the line from left or right positions (position 1 and 3)
     * @return
     */
    private Command makeCrossLineFromEndPositionCommand() {
        return new AutoCrossLineStraightAhead(250.0, .80, this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());
    }
    
    private Command makeDeliverToSwitchFrontCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToSwitchFront(startingSide, this.getSensorService(), this.getDrivetrainSubsystem(),
                this.getPneumaticSubsystem(), this.getOperatorDisplay());
    }

    private Command makeDeliverToSwitchEndFromOppositeSideCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToSwitchEndFromOppositeSide(startingSide, this.getSensorService(),
                this.getDrivetrainSubsystem(), this.getPneumaticSubsystem(), this.getOperatorDisplay());
    }
    
    private Command makeDeliverToSwitchEndCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToSwitchEnd(
                startingSide,
                this.sensorService, this.getDrivetrainSubsystem(), this.getPneumaticSubsystem(),
                this.getElevatorSubsystem(),
                this.operatorDisplay
        );
    }
    
    private Command makeDeliverToScaleEndFromOppositeSideCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToScaleEndFromOppositeSide(startingSide, 
                this.sensorService, this.getDrivetrainSubsystem(), 
                this.getPneumaticSubsystem(), 
                this.getElevatorSubsystem(), this.getOperatorDisplay()
        );
    }    
    private Command makeDeliverToSwitchFromCenterCommand(StartingPositionSide startingSide) {
        return new AutoDeliverToSwitchFrontFromCenterPosition(startingSide, this.getSensorService(), this.getDrivetrainSubsystem(), this.getPneumaticSubsystem(), this.getOperatorDisplay());
    }

    private Command makeCrossLineFromCenterCommand() {
        return new AutoCrossLineFromCenterCommand(this.getSensorService(), this.getDrivetrainSubsystem(), 
                this.getPneumaticSubsystem(), this.getOperatorDisplay()); 
    }
    
}
