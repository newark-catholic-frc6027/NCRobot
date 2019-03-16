package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.sensors.LimitSwitchSensors;
import frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class SlideMastCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    public final static String NAME = "Slide Mast Command";
    public enum SlideMastDirection {
        Forward,
        Backward
    };

    private Preferences prefs = Preferences.getInstance();
    protected static final int LOG_REDUCTION_MOD = 10;
    protected int execCount = 0;
    protected long execStartTime = 0;
    
    
    private SlideMastDirection direction = null;
    private ElevatorSubsystem elevator;
    private LimitSwitchSensors limitSwitches;
    
    private double power = 0.5;
    private long checkMotorAmpsThresholdMillis;
    
    public SlideMastCommand(SlideMastDirection direction, double power, SensorService sensorService, ElevatorSubsystem elevator) {
        this.direction = direction;
        this.elevator = elevator;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        
        this.setName(NAME);
    }

    @Override
    protected void initialize() {
        logger.info("SlideMast Command starting...");
        this.execStartTime = System.currentTimeMillis();
        this.checkMotorAmpsThresholdMillis = this.prefs.getLong("slideMastCommand.checkMotorAmpsThresholdMillis", 1000);
    }
    
    @Override
    protected void end() {
        this.clearRequirements();
    }
    
    protected boolean isForwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.elevator.isForwardMaxAmpsExceeded();
        }
        
        return false;
    }

    protected boolean isBackwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.elevator.isBackwardMaxAmpsExceeded();
        }
        
        return false;
    }
    
    @Override
    protected boolean isFinished() {
         
        boolean backwardSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideBackward);
        boolean forwardSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideForward);

        // Checking isGoingUp/Down may be affecting communication
        boolean done = (this.direction == SlideMastDirection.Forward && this.elevator.isGoingForward() && (forwardSwitchTripped || this.isForwardMaxAmpsExceededWithDelay())) 
                           ||
                       (this.direction == SlideMastDirection.Backward && this.elevator.isGoingBackward() && (backwardSwitchTripped || this.isBackwardMaxAmpsExceededWithDelay()));

        if (done) {
            this.elevator.mastSlideStop();
            logger.info(">>>>> Mast slide command FINISHED. forwardSwitchTripped: {}, backwardSwitchTripped: {}", forwardSwitchTripped, backwardSwitchTripped);
            this.clearRequirements();
        }
        return done;
    }
    
    protected void execute() {
        this.execCount++;
        if (this.direction == SlideMastDirection.Forward) {
            this.elevator.mastForward(power);
        } else if (this.direction == SlideMastDirection.Backward) {
            this.elevator.mastBackward(power);
        } else {
            logger.error("Mast slide Execute stopped!  Direction not set!");
        }
        // Be explicit in order to try reduce motor "Output not updated often enough" warnings
        // this.driveTrain.stopMotor();
    }

    
}
