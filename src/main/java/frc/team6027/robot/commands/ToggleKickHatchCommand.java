package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class ToggleKickHatchCommand extends Command {
    
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 350;

    private Preferences prefs = Preferences.getInstance();

    public boolean executionComplete = false;
    
    private final Logger logger = LogManager.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private long timeStarted = 0;
    protected boolean isReset = false;
    protected boolean retracted = false;
    
    public ToggleKickHatchCommand(PneumaticSubsystem pneumaticSubsystem) {
        this(pneumaticSubsystem, true);
    }
    
    public ToggleKickHatchCommand(PneumaticSubsystem pneumaticSubsystem, boolean inAutonomous) {
        requires(pneumaticSubsystem);
        this.pneumaticSubsystem = pneumaticSubsystem;
    }
    
    @Override
    protected void initialize() {
        reset();
        this.setTimeout(DELAY_TO_OFF_MS);
    }
    
	@Override
	public void cancel() {
		this.isReset = false;
		super.cancel();
	}


    @Override
    public void start() {
        this.reset();
        logger.trace("Running KickHatchCommand");
        super.start();
    }
    
    protected void reset() {
        this.timeStarted = 0;
        this.isReset = true;
        this.retracted = false;
    }
    @Override 
    public void execute() {
        if (! executionComplete) {
            this.pneumaticSubsystem.toggleHatchSolenoid();
            this.timeStarted = System.currentTimeMillis();
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    protected boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("KickHatchCommand finished");
            this.isReset = false;
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.isReset = false;
        this.pneumaticSubsystem.toggleHatchSolenoidOff();
        this.retracted = false;
    }

}
