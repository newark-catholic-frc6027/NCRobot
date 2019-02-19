package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.sensors.LimitSwitchSensors;
import frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.RearLiftSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class RearLiftCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    public final static String NAME = "Rear Lift Command";
    private Preferences prefs = Preferences.getInstance();
    protected static final int LOG_REDUCTION_MOD = 10;
    protected int execCount = 0;
    protected long execStartTime = 0;

    
    public enum RearLiftDirection {
        Up,
        Down
    };
    
    private RearLiftDirection direction = null;
    private RearLiftSubsystem rearLift;
    private LimitSwitchSensors limitSwitches;
    private DrivetrainSubsystem driveTrain;
    
    private double power = 0.5;
    private long checkMotorAmpsThresholdMillis;
    
    public RearLiftCommand(RearLiftDirection direction, double power, SensorService sensorService, RearLiftSubsystem rearLift, DrivetrainSubsystem drivetrain) {
        this.direction = direction;
        this.rearLift = rearLift;
        this.driveTrain = drivetrain;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        
        this.setName(NAME);
        requires(rearLift);
    }

    @Override
    protected void initialize() {
        logger.info("RearLift Command starting...");
        this.execStartTime = System.currentTimeMillis();
        this.checkMotorAmpsThresholdMillis = this.prefs.getLong("rearLiftCommand.checkMotorAmpsThresholdMillis", 1000);
    }
    
    @Override
    protected void end() {
        this.clearRequirements();
    }
    
    // TODO: do we need a downward check also?
    protected boolean isUpwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.rearLift.isUpwardMaxAmpsExceeded();
        }
        
        return false;
    }

    protected boolean isDownwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.rearLift.isDownwardMaxAmpsExceeded();
        }
        
        return false;
    }
    
    @Override
    protected boolean isFinished() {
         
        boolean downSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.RearLiftDown);
        boolean upSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.RearLiftUp);

        // Checking isGoingUp/Down may be affecting communication
        boolean done = (this.direction == RearLiftDirection.Up && this.rearLift.isGoingUp() && (upSwitchTripped || this.isUpwardMaxAmpsExceededWithDelay())) 
                           ||
                       (this.direction == RearLiftDirection.Down && this.rearLift.isGoingDown() && (downSwitchTripped || this.isDownwardMaxAmpsExceededWithDelay()));

        if (done) {
            this.rearLift.stopMotor();
            logger.info(">>>>> RearLift command FINISHED. upSwitch: {}, downSwitch: {}", upSwitchTripped, downSwitchTripped);
            this.clearRequirements();
        }
        return done;
    }
    
    protected void execute() {
        this.execCount++;
        if (this.direction == RearLiftDirection.Up) {
            this.rearLift.rearLiftUp(power);
        } else if (this.direction == RearLiftDirection.Down) {
            this.rearLift.rearLiftDown(power);
        } else {
            logger.error("RearLift Execute stopped!  Direction not set!");
        }
        // Be explicit in order to try reduce motor "Output not updated often enough" warnings
        this.driveTrain.stopMotor();
    }

    
}
