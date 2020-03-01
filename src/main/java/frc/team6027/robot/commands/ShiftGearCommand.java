package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.Pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Toggles the drivetain shifter from HIGH to LOW or LOW to HIGH.
 */
public class ShiftGearCommand extends CommandBase {
    public enum ShiftGearMode {
        Low,
        High
    }
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static double DELAY_TO_OFF_MS = .250;
    public boolean executionComplete = false;
    
    private final Logger logger = LogManager.getLogger(getClass());

    private Pneumatics pneumatics;
    private long timeStarted;
    private DoubleSolenoid shifterSolenoid;
    private Value initialShifterState;
    private ShiftGearMode mode;


    public ShiftGearCommand(Pneumatics pneumatics) {
        this.addRequirements(pneumatics);
        this.pneumatics = pneumatics;
        this.shifterSolenoid = this.pneumatics.getDriveSolenoid();
        this.initialShifterState = this.shifterSolenoid.get();

    }
    
    public ShiftGearCommand(Pneumatics pneumaticSubsystem, ShiftGearMode mode) {
        this(pneumaticSubsystem);
        this.mode = mode;
    }
    
    @Override
    public void initialize() {
        // cannot set timeout in command initialization since command framework was rewritten
        // this.setTimeout(DELAY_TO_OFF_MS);
    }
    
    
    @Override 
    public void execute() {
        if (! executionComplete) {
            logger.trace("Running ShiftGearCommand");
            if (this.mode == null) {
                this.pneumatics.toggleDriveSolenoid();
            } else if (this.mode == ShiftGearMode.High) {
                this.pneumatics.toggleDriveSolenoidReverse();
            } else if (this.mode == ShiftGearMode.Low) {
                this.pneumatics.toggleDriveSolenoidForward();
            } else {
                logger.warn("Shift Gear doing nothing");
            }
        }
        timeStarted = System.currentTimeMillis();
        // We only want to run once, so keep a boolean to make sure we don't run again until 
        // the delay period has expired
        this.executionComplete = true;
    }
    
    
    @Override
    public boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (this.shifterSolenoid.get() != this.initialShifterState || timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("ShiftGearCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void end(boolean interrupted) {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.pneumatics.toggleDriveSolenoidOff();
    }

}
