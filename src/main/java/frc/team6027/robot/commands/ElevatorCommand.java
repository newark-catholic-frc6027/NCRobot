package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.sensors.LimitSwitchSensors;
import frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class ElevatorCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    public final static String NAME = "Elevator Command";
    private Preferences prefs = Preferences.getInstance();
    protected static final int LOG_REDUCTION_MOD = 10;
    protected int execCount = 0;
    protected long execStartTime = 0;

    
    public enum ElevatorDirection {
        Up,
        Down
    };
    
    private ElevatorDirection direction = null;
    private ElevatorSubsystem elevator;
    private LimitSwitchSensors limitSwitches;
    private SensorService sensorService;
    private Double targetHeight = null;
    private String elevatorHeightPrefName = null;
    private String powerPrefName = null;

    private double power = 0.5;
    private long checkMotorAmpsThresholdMillis;
    protected boolean isReset = false;

    /**
     * Runs elevator all the way up or all the way down.
     */
    public ElevatorCommand(ElevatorDirection direction, double power, SensorService sensorService, ElevatorSubsystem elevator) {
        this.direction = direction;
        this.elevator = elevator;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        this.sensorService = sensorService;
        this.setName(NAME);
        requires(elevator);
    }
    /**
     * Runs elevator to given height.
     */
    public ElevatorCommand(double height, double power, SensorService sensorService, ElevatorSubsystem elevator) {
        this.targetHeight = height;
        this.elevator = elevator;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        this.sensorService = sensorService;
        
        this.setName(NAME);
        requires(elevator);
    }

    /**
     * Runs elevator to given height.
     */
    public ElevatorCommand(String heightPrefName, String powerPrefName, SensorService sensorService, ElevatorSubsystem elevator) {
        this.elevator = elevator;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        this.sensorService = sensorService;
        this.elevatorHeightPrefName = heightPrefName;
        this.powerPrefName = powerPrefName;

        this.setName(NAME);
        requires(elevator);
    }


    @Override
    protected void initialize() {
        reset();
    }
    
    @Override
    protected void end() {
        this.clearRequirements();
    }
    
    protected boolean isUpwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.elevator.isUpwardMaxAmpsExceeded();
        }
        
        return false;
    }

    protected boolean isDownwardMaxAmpsExceededWithDelay() {
        long elapsedTime = System.currentTimeMillis() - this.execStartTime;
        
        if (elapsedTime > this.checkMotorAmpsThresholdMillis) {
            return this.elevator.isDownwardMaxAmpsExceeded();
        }
        
        return false;
    }
    
    @Override
    protected boolean isFinished() {
        boolean bottomSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastBottom);
        boolean topSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastTop);

        // Checking isGoingUp/Down may be affecting communication
        boolean minOrMaxReached = (this.direction == ElevatorDirection.Up && this.elevator.isGoingUp() && (topSwitchTripped || this.isUpwardMaxAmpsExceededWithDelay())) 
                           ||
                       (this.direction == ElevatorDirection.Down && this.elevator.isGoingDown() && (bottomSwitchTripped || this.isDownwardMaxAmpsExceededWithDelay()));

        logger.info("isFinished invoked. minOrMaxReached: {}, targetHeight: {}, currentHeight: {}",
             minOrMaxReached, this.targetHeight,this.sensorService.getElevatorHeightInches() ); 

        if (minOrMaxReached) {
            this.elevator.elevatorStop();
            logger.info(">>>>> Elevator command FINISHED. topSwitch: {}, bottomSwitch: {}", topSwitchTripped, bottomSwitchTripped);
            this.clearRequirements();
            this.isReset = false;

            return true;
        }

        if (this.targetHeight != null) {
            boolean done = false;
            double currentHeight = this.sensorService.getElevatorHeightInches();
            if (this.direction == ElevatorDirection.Up) {
                if (currentHeight >= this.targetHeight) {
                    done = true;
                }
            } else {
                if (currentHeight <= this.targetHeight) {
                    done = true;
                }
            }
            if (done) {
                logger.info(">>>>> Elevator command FINISHED. Reached target height of {}. topSwitch: {}, bottomSwitch: {}", 
                    this.targetHeight, topSwitchTripped, bottomSwitchTripped);
                this.clearRequirements();
                this.isReset = false;
                return true;
            }
        }

        return false;
    }
    
	@Override
	public void cancel() {
        logger.info(">>> Elevator Command canceled");
		this.isReset = false;
		super.cancel();
	}

	protected void reset() {
        if (isReset) {
            this.logger.info("Not reset since reset has already been run");
            return;
		}
		this.isReset = true;

        this.execCount = 0;
        this.execStartTime = System.currentTimeMillis();
        this.checkMotorAmpsThresholdMillis = this.prefs.getLong("elevatorCommand.checkMotorAmpsThresholdMillis", 1000);

        if (this.elevatorHeightPrefName != null) {
            this.targetHeight = this.prefs.getDouble(this.elevatorHeightPrefName, 0.0);
        }

        if (this.targetHeight != null) {
            // When targetHeight has been established, the Direction will be determined 
            // on first execute when we have a specific target height to go to
            // Reset direction in this case.
            this.direction = null;
        }

        if (this.powerPrefName != null) {
            this.power = this.prefs.getDouble(this.powerPrefName, 0.5);
        }
	}

	@Override
	public void start() {
		this.reset();
		logger.info(">>> Elevator Command starting. Elevator direction: {}, target height: {}, power: {}", this.direction, this.targetHeight, this.power);
		super.start();
	}

    protected void execute() {
        logger.info("execute invoked. targetHeight: {}, currentHeight: {}, direction: {}",
           this.targetHeight,this.sensorService.getElevatorHeightInches(), this.direction ); 

        this.execCount++;
        if (this.targetHeight != null) {
            // If we have been given a specific height to go to, the direction will be null on the first
            if (this.direction == null) {
                double currentHeight = this.sensorService.getElevatorHeightInches();
                if (this.targetHeight >= currentHeight) {
                    this.direction = ElevatorDirection.Up;
                } else {
                    this.direction = ElevatorDirection.Down;
                }
                this.logger.info("Current height is: {}, target height is: {}, moving: {}", currentHeight, this.targetHeight, this.direction);
            }
        }

        if (this.direction == ElevatorDirection.Up) {
            this.elevator.elevatorUp(power);
        } else if (this.direction == ElevatorDirection.Down) {
            this.elevator.elevatorDown(power);
        } else {
            logger.error("Elevator Execute stopped!  Direction not set!");
        }
    }

    
}
