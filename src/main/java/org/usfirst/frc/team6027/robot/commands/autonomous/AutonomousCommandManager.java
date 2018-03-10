package org.usfirst.frc.team6027.robot.commands.autonomous;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.commands.autonomous.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.autonomous.TurnWhileDrivingCommand.TargetVector;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.field.Field.PlatePosition;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class AutonomousCommandManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    
    public enum AutonomousPreference {
        NoPreference("NO SELECTION"),
        CrossLine("Cross the Line"),
        DeliverToSwitchEnd("Deliver to Switch END"),
        DeliverToSwitchFront("Deliver to Switch FRONT"),
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
    private SensorService sensorService;
    private DrivetrainSubsystem drivetrainSubsystem;
    private PneumaticSubsystem pneumaticSubsystem;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private Map<String,Command> commandsByName = new HashMap<>();
    
    // SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
    // OperatorDisplay operatorDisplay
    public AutonomousCommandManager(AutonomousPreference preferredAutoScenario, Field field, SensorService sensorService, 
            DrivetrainSubsystem drivetrainSubsystem, PneumaticSubsystem pneumaticSubsystem, OperatorDisplay operatorDisplay) {
        this.preferredAutoScenario = preferredAutoScenario;
        this.field = field;
        this.sensorService = sensorService;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.operatorDisplay = operatorDisplay;
        
        createAutonomousCommands();
    }
    
    public static void initAutoScenarioDisplayValues(OperatorDisplay operatorDisplay) {
        operatorDisplay.registerAutoScenario(AutonomousPreference.CrossLine.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToSwitchEnd.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToSwitchFront.displayName());
        operatorDisplay.registerAutoScenario(AutonomousPreference.DeliverToScaleEnd.displayName());
    }
    
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

        
        
//        this.commandsByName.put(turnWhileDriveCmd.getName(), turnWhileDriveCmd);
        
    }

    public Command getCommandByName(String commandName) {
        return this.commandsByName.get(commandName);
    }
    
    public Command chooseCommand() {
        
        logger.info(">>>>>> Station Position: {}, Scenario: {}", this.getField().getOurStationPosition(), this.getPreferredScenario());
        
        if (this.field.getOurStationPosition() <= 0) {
            logger.warn("NO POSITION SELECTED, cannot choose an Autonomous command!");
            return NoOpCommand.getInstance();
        }
        
        Command chosenCommand = null;
        
        // If we are positioned in the center station, station 2
        if (this.field.isOurStationCenter()) {
            chosenCommand = chooseCenterStationCommand();
        } else if (this.field.isOurStationLeft()) {
            chosenCommand = chooseLeftStationCommand();
        } else { // Our Station is on Right
            chosenCommand = chooseRightStationCommand();
        }
        
        if (chosenCommand == null) {
            chosenCommand = NoOpCommand.getInstance();
            logger.warn("!!! A command could not be automatically chosen, choosing NoOpCommand");
        } else {
            logger.info(">>> Command <{}> was automatically chosen", chosenCommand.getName()); 
        }
        
        return chosenCommand;
    }
    
    protected Command chooseRightStationCommand() {
        logger.info("Team Position: RIGHT ({}), scenario: ", this.field.getOurStationPosition(), this.getPreferredScenario());
        Command chosenCommand = null;
        
        if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchRight)) {
            if (this.isNoPreferredScenario()) {
                logger.trace("DELIVER END RIGHT");
                // TODO: deliver END RIGHT
            } else {
                if (this.getPreferredScenario() == AutonomousPreference.CrossLine){
                    
                    // TODO: REMOVE after done testing
                    double leg1Distance = this.prefs.getDouble("leg1.distance", 48.0);
                    double leg2Distance = this.prefs.getDouble("leg2.distance", 48.0);
                    double leg3Distance = this.prefs.getDouble("leg3.distance", 48.0);

                    double leg1Angle = this.prefs.getDouble("leg1.angle", 0.0);
                    double leg2Angle = this.prefs.getDouble("leg2.angle", 10.0);
                    double leg3Angle = this.prefs.getDouble("leg3.angle", -30.0);

                    // TODO: Remove after done testing
                    TargetVector[] turnVectors = new TargetVector[] { 
                            new TargetVector(leg1Angle, leg1Distance), 
                            new TargetVector(leg2Angle, leg2Distance), 
                            new TargetVector(leg3Angle, leg3Distance) 
                    };
                    
                    chosenCommand = new TurnWhileDrivingCommand(
                            this.sensorService, this.getDrivetrainSubsystem(), this.operatorDisplay, 
                            turnVectors,
                            DriveDistanceMode.DistanceReadingOnEncoder, 0.5
                    );
                    
                } else {
                    logger.trace("Don't yet know how to handle <{}>", this.getPreferredScenario().displayName());
                }
                // TODO: check that the preferred command doesn't contradict with our assignment.  If it does then just
                // drive straight to get across the line (this is safest because the robot may not be positioned correctly
                // for the other commands).  If it doesn't just return the preferred command.
            }
            
        } else { // Assigned Switch Plate is on left
            if (this.getPreferredScenario() == AutonomousPreference.CrossLine) {
                chosenCommand = new AutoCrossLineStraightAhead(250.0, .80, this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());
            } else {
                logger.error( "No command configured, don't know what to do, so I will do nothing.");
            }

            // TODO:
            // Either Charge Straight ahead to cross the line -- OR --
            // Deliver to the Scale if right plate is assigned to us
        }
        
        return chosenCommand;
    }

    protected Command chooseLeftStationCommand() {
        logger.info("Team Position: LEFT ({}), scenario: ", this.field.getOurStationPosition(), this.getPreferredScenario());
        Command chosenCommand = null;

        if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchLeft)) {
            if (this.isNoPreferredScenario()) {
                // TODO: deliver END LEFT
            } else {
                if (this.getPreferredScenario() == AutonomousPreference.DeliverToSwitchEnd) {
                    
                    chosenCommand = new AutoDeliverToSwitchEnd(DeliverySide.Left, this.getSensorService(), this.getDrivetrainSubsystem(), 
                            this.getPneumaticSubsystem(), this.getOperatorDisplay());
                }
                // TODO: check that the preferred command doesn't contradict with our assignment.  If it does then just
                // drive straight to get across the line (this is safest because the robot may not be positioned correctly
                // for the other commands).  If it doesn't just return the preferred command.
            }
            
        } else { // Assigned Switch Plate is on right
            if (this.getPreferredScenario() == AutonomousPreference.CrossLine) {
                // Drive straight ahead for a fixed distance at 80% power
                chosenCommand = new AutoCrossLineStraightAhead(250.0, .80, this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay());
            } else {
                logger.error( "No command configured, don't know what to do, so I will do nothing.");
            }
            // TODO:
            // Either Charge Straight ahead to cross the line -- OR --
            // Deliver to the Scale if left plate is assigned to us
        }
        
        return chosenCommand;
    }

    protected Command chooseCenterStationCommand() {
        Command chosenCommand = null;
        logger.info("Team Position: CENTER ({}), scenario: ", this.field.getOurStationPosition(), this.getPreferredScenario());
        // If driver's didn't select a preferred command, automatically select one
        if (this.isNoPreferredScenario()) {
            if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchLeft)) {
                // TODO: 
                // deliver front left -- Mr. Nelson says no, we should go right
            } else {
                // TODO:
                // deliver front right
            }
        } else {
            if (this.getPreferredScenario() == AutonomousPreference.CrossLine) {
                // Drive straight ahead and stop when we are 12 inches from the switch
                chosenCommand = new DriveStraightCommand(this.getSensorService(), this.getDrivetrainSubsystem(), this.getOperatorDisplay(), 
                        -12.0, DriveDistanceMode.DistanceFromObject, .40); 
            } else {
                logger.error( "No command configured, don't know what to do, so I will do nothing.");
            }
            // TODO: check that the preferred command doesn't contradict with our assignment.  If it does then just
            // drive straight to get across the line (this is safest because the robot may not be positioned correctly
            // for the other commands).  If it doesn't just return the preferred command.
        }
        
        return chosenCommand;
    }

    public boolean isNoPreferredScenario() {
        return this.preferredAutoScenario == AutonomousPreference.NoPreference;
    }

    public AutonomousPreference getPreferredScenario() {
        return preferredAutoScenario;
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
}
