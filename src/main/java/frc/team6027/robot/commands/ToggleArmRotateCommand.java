package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

public class ToggleArmRotateCommand extends Command {
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 250;
    public boolean executionComplete = false;
    private Preferences prefs = Preferences.getInstance();
    
    private final Logger logger = LogManager.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private DoubleSolenoid armRotateSolenoid;
    
    private long timeStarted;
    private Value initialArmRotateState;
    
    public ToggleArmRotateCommand(PneumaticSubsystem pneumaticSubsystem) {
        requires(pneumaticSubsystem);
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.armRotateSolenoid = this.pneumaticSubsystem.getArmRotateSolenoid();
        this.initialArmRotateState = this.armRotateSolenoid.get();
    }
    
    @Override
    protected void initialize() {
        this.setTimeout(DELAY_TO_OFF_MS);
    }
    
    
    @Override 
    public void execute() {
        if (! executionComplete) {
            logger.trace("Running ToggleArmRotateCommand");
            this.pneumaticSubsystem.toggleArmRotateSolenoid();
            timeStarted = System.currentTimeMillis();
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    protected boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (this.armRotateSolenoid.get() != this.initialArmRotateState || timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("ToggleArmRotateCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.pneumaticSubsystem.toggleArmRotateSolenoidOff();
    }

}
