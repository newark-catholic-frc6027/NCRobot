package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.Pneumatics;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ToggleBallLatchCommand extends CommandBase {
    
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static double DELAY_TO_OFF_S = .350;

    private Preferences prefs = Preferences.getInstance();

    public boolean executionComplete = false;
    
    private final Logger logger = LogManager.getLogger(getClass());

    private Pneumatics pneumatics;
    private long timeStarted = 0;
    protected boolean isReset = false;
    protected boolean retracted = false;
    
    public ToggleBallLatchCommand(Pneumatics pneumaticSubsystem) {
        this(pneumaticSubsystem, true);
    }
    
    public ToggleBallLatchCommand(Pneumatics pneumatics, boolean inAutonomous) {
        this.addRequirements(pneumatics);
        this.pneumatics = pneumatics;
    }
    
    @Override
    public void initialize() {
        reset();
        // cannot set timeout in command initialization since command framework was rewritten
        // this.withTimeout(DELAY_TO_OFF_S);
    }
    
	@Override
	public void cancel() {
		this.isReset = false;
		super.cancel();
	}

    protected void reset() {
        this.timeStarted = 0;
        this.isReset = true;
        this.retracted = false;
    }

    @Override 
    public void execute() {
        if (! executionComplete) {
            this.pneumatics.toggleBallLatchSolenoid();
            this.timeStarted = System.currentTimeMillis();
            // We only want to run once, so keep a boolean to make sure we don't run again until 
            // the delay period has expired
            this.executionComplete = true;
        }
    }
    
    
    @Override
    public boolean isFinished() {
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (timeElapsedMs >= DELAY_TO_OFF_S * 1000) {
            logger.trace("BallLatchCommand finished");
            this.isReset = false;
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void end(boolean interrupted) {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.isReset = false;
        this.pneumatics.toggleBallLatchSolenoidOff();
        this.retracted = false;
    }

}
