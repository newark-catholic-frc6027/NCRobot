package frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Drops the carriage horizontal for Delivery or down all the way for Climbing.
 */
public class DropCarriageCommand extends Command {
    /**
     * Elapsed time in teleop before Dropping of the carriage for climbing will be permitted.
     */
    public static final double ALLOW_DROP_FOR_CLIMB_THRESHOLD = -2.0;// 100.0; // secs
    public enum DropFunction {
        DropForDelivery,
        DropForClimb
    }
    
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 250;
    private Preferences prefs = Preferences.getInstance();

    private DropFunction dropFunction;
    public boolean executionComplete = false;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private DriverStation driverStation;
    private long timeStarted;
    private boolean inAutonomous;
    private Field field;
    
    public DropCarriageCommand(DropFunction dropFunction, DriverStation driverStation, PneumaticSubsystem pneumaticSubsystem, Field field) {
        this(dropFunction, driverStation, pneumaticSubsystem, field, true);
    }
    
    public DropCarriageCommand(DropFunction dropFunction, DriverStation driverStation, PneumaticSubsystem pneumaticSubsystem, Field field, boolean inAutonomous) {
        requires(pneumaticSubsystem);
        this.dropFunction = dropFunction;
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.driverStation = driverStation;
        this.field = field;
        this.inAutonomous = inAutonomous;
    }
    
    @Override
    protected void initialize() {
        this.setTimeout(DELAY_TO_OFF_MS);
    }
    
    
    @Override 
    public void execute() {
        if (! executionComplete) {
            logger.trace("Running DropCarriageCommand");
            if (this.dropFunction == DropFunction.DropForDelivery) {
                this.pneumaticSubsystem.activateDropForDeliverySolenoid();
            } else if (this.dropFunction == DropFunction.DropForClimb) {
                
                double elapsedTeleopTime = this.driverStation.getMatchTime();
                if (elapsedTeleopTime >= ALLOW_DROP_FOR_CLIMB_THRESHOLD) {
                    this.pneumaticSubsystem.activateDropForClimbSolenoid();
                } else {
                    logger.warn("Dropping of carriage not permitted until afer {}s mark. Current elapsed time is: {}", ALLOW_DROP_FOR_CLIMB_THRESHOLD, elapsedTeleopTime);
                }
            } else {
                logger.error("Unhandled Drop Function: {}", this.dropFunction);
            }
            timeStarted = System.currentTimeMillis();
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    protected boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("DropCarriageCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        if (this.dropFunction == DropFunction.DropForClimb) {
            this.pneumaticSubsystem.deactivateDropForClimbSolenoid();
        } else if (this.dropFunction == DropFunction.DropForDelivery) {
            this.pneumaticSubsystem.deactivateDropForDeliverySolenoid();
        }
    }

}
