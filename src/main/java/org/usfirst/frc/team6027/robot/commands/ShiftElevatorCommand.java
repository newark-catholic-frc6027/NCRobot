package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.StatefulSolenoid;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Toggles the elevator shifter from HIGH to LOW or LOW to HIGH.
 */
public class ShiftElevatorCommand extends Command {
    public enum TargetGear {
        High,
        Low
    }
    
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 250;
    public boolean executionComplete = false;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private long timeStarted;
    private StatefulSolenoid elevatorSolenoid;
    private Value initialElevatorState;
    private TargetGear targetGear;
    private boolean toggled = false;


    public ShiftElevatorCommand(TargetGear targetGear, PneumaticSubsystem pneumaticSubsystem) {
        requires(pneumaticSubsystem);
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.elevatorSolenoid = this.pneumaticSubsystem.getElevatorShifterSolenoid();
        this.initialElevatorState = this.pneumaticSubsystem.getElevatorShifterSolenoid().getState();
        this.targetGear = targetGear;
    }
    
    @Override
    protected void initialize() {
        this.setTimeout(DELAY_TO_OFF_MS);
    }
    
    
    @Override 
    public void execute() {
        if (! executionComplete) {
            logger.trace("Running ShiftElevatorCommand");
            if (this.pneumaticSubsystem.isElevatorShifterInLowGear() && this.targetGear == TargetGear.High) {
                this.pneumaticSubsystem.toggleElevatorShifterSolenoidReverse();
                this.toggled = true;
            } else if (this.pneumaticSubsystem.isElevatorShifterInHighGear() && this.targetGear == TargetGear.Low) {
                this.pneumaticSubsystem.toggleElevatorShifterSolenoidForward();
                this.toggled = true;
            } else {
                logger.info("Elevator already in {} gear, nothing to do", this.targetGear);
            }
            
            if (this.toggled) {
                timeStarted = System.currentTimeMillis();
            }
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    protected boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (this.elevatorSolenoid.get() != this.initialElevatorState || timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("ShiftElevatorCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.pneumaticSubsystem.toggleElevatorShifterSolenoidOff();
    }

}
