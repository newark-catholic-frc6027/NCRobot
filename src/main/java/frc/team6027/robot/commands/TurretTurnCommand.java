package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team6027.robot.subsystems.MotorDirection;
import frc.team6027.robot.subsystems.Turret;

public class TurretTurnCommand extends CommandBase {
    private final Logger logger = LogManager.getLogger(getClass());

    public enum TurretTurnDirection {
        Clockwise,
        CounterClockwise
    }

    private Preferences prefs = Preferences.getInstance();
    protected boolean isReset = false;
    protected double power = 0;
    protected double maxCounterClockwiseSetpoint = 0;
    protected double maxClockwiseSetpoint = 0;
    protected long executionCount = 0;

    private Turret turret;
    protected TurretTurnDirection direction;

    public TurretTurnCommand(Turret turret, TurretTurnDirection direction) {
        this.turret = turret;
        this.direction = direction;
    }

    @Override
    public void initialize() {
        this.logger.trace("TurretTurnCommand initializing...");
        super.initialize();
        reset();
        this.power = this.prefs.getDouble(Turret.TURRET_POWER, 0.4);
        this.maxCounterClockwiseSetpoint = this.prefs.getDouble(Turret.TURRET_MAX_CCW_KEY, 0);
        this.maxClockwiseSetpoint = this.prefs.getDouble(Turret.TURRET_MAX_CW_KEY, 0);
        this.logger.info("TurretTurnCommand initialized. direction: {}, power: {}, maxCcwSetpoint: {}, maxCwSetpoint: {}", 
            this.direction, this.power, this.maxCounterClockwiseSetpoint, this.maxClockwiseSetpoint
        );
    }
    
	@Override
	public void cancel() {
		this.isReset = false;
		super.cancel();
	}

    protected void reset() {
        this.isReset = true;
        this.executionCount = 0;

    }

    @Override 
    public void execute() {
        executionCount++;

        if (! isLimitExceeded()) {
            if (executionCount % 3 == 0) {
                logger.trace("Executing TurrentTurnCommand, position: {}", this.turret.getEncoder().getPosition());
            }
            this.turret.turn(this.power, direction == TurretTurnDirection.Clockwise ? MotorDirection.Forward : MotorDirection.Reverse);
        }
    }
    
    private boolean isLimitExceeded() {
        //return false;
        
        double curPosition = this.turret.getEncoder().getPosition();
        boolean done = (curPosition >= this.maxClockwiseSetpoint && this.direction == TurretTurnDirection.Clockwise)
            || (curPosition <= this.maxCounterClockwiseSetpoint && this.direction == TurretTurnDirection.CounterClockwise);

        if (executionCount % 20 == 0) {
            logger.trace("curPosition: {}, done? {}", curPosition, done);
        }
        if (done) {
            logger.info("Limit exceeded!  Current position: {}", curPosition);
        }
        return done;
    }
    
    @Override
    public boolean isFinished() {
        boolean limitExceeded = this.isLimitExceeded();
        if (limitExceeded) {
            if (turret.isManualOverrideAllowed()) {
                logger.info("Limit exceeded, but manual override is allowed. Not terminating command.");
                return false;
            }
            return true;
        }

        return false;
    }
    
    @Override
    public void end(boolean interrupted) {
        this.turret.stop();
        // Reset our state for when we run again
        this.isReset = false;
    }

}
