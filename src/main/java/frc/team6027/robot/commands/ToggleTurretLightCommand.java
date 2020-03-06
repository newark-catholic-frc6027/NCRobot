package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team6027.robot.subsystems.MotorDirection;
import frc.team6027.robot.subsystems.Turret;

public class ToggleTurretLightCommand extends CommandBase {
    private final Logger logger = LogManager.getLogger(getClass());
    public boolean executionComplete = false;
    protected boolean isReset = false;
    protected double power = 0;
    protected double maxCounterClockwiseSetpoint = 0;
    protected double maxClockwiseSetpoint = 0;
    protected long executionCount = 0;


    public ToggleTurretLightCommand() {
    }

    @Override
    public void initialize() {
        this.logger.trace("TurretTurnCommand initializing...");
        super.initialize();
        reset();
    }
    
	@Override
	public void cancel() {
		this.isReset = false;
		super.cancel();
	}

    protected void reset() {
        this.isReset = true;
        this.executionComplete = false;
        this.executionCount = 0;

    }

    @Override 
    public void execute() {
        executionCount++;
        if (! executionComplete) {
            if (executionCount % 20 == 0) {
                logger.trace("Executing TurrentTurnCommand");
            }
        }
    }
    
    
    @Override
    public boolean isFinished() {
        return true;
        /*        
        boolean done = curPosition >= this.maxClockwiseSetpoint
            || curPosition <= this.maxCounterClockwiseSetpoint;

        this.executionComplete = done;
        return done;
        */
    }
    
    @Override
    public void end(boolean interrupted) {
        /*
        this.turret.stop();
        // Reset our state for when we run again
        this.executionComplete = false;
        this.isReset = false;
        */
    }

}
