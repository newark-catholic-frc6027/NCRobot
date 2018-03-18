package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Toggles the drivetain shifter from HIGH to LOW or LOW to HIGH.
 */
public class ShiftGearCommand extends Command {
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 250;
    public boolean executionComplete = false;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private long timeStarted;
    private DoubleSolenoid shifterSolenoid;
    private Value initialShifterState;


    public ShiftGearCommand(PneumaticSubsystem pneumaticSubsystem) {
        requires(pneumaticSubsystem);
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.shifterSolenoid = this.pneumaticSubsystem.getDriveSolenoid();
        this.initialShifterState = this.shifterSolenoid.get();

    }
    
    @Override
    protected void initialize() {
        this.setTimeout(DELAY_TO_OFF_MS);
    }
    
    
    @Override 
    public void execute() {
        if (! executionComplete) {
            logger.trace("Running ShiftGearCommand");
            this.pneumaticSubsystem.toggleDriveSolenoid();
            timeStarted = System.currentTimeMillis();
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    protected boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (this.shifterSolenoid.get() != this.initialShifterState || timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("ShiftGearCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.pneumaticSubsystem.toggleDriveSolenoidOff();
    }

}
