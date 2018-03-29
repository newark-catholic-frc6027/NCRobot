package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class ElevatorCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(getClass());
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
    private DrivetrainSubsystem driveTrain;
    
    private double power = 0.5;
    private long checkMotorAmpsThresholdMillis;
    
    public ElevatorCommand(ElevatorDirection direction, double power, SensorService sensorService, ElevatorSubsystem elevator, DrivetrainSubsystem drivetrain) {
        this.direction = direction;
        this.elevator = elevator;
        this.driveTrain = drivetrain;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        
        this.setName(NAME);
        requires(elevator);
    }

    @Override
    protected void initialize() {
        logger.info("Elevator Command starting...");
        this.execStartTime = System.currentTimeMillis();
        this.checkMotorAmpsThresholdMillis = this.prefs.getLong("elevatorCommand.checkMotorAmpsThresholdMillis", 1000);
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
    
    @Override
    protected boolean isFinished() {
         
        boolean bottomSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastBottom);
        boolean topSwitchTripped = this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastTop);

        // Checking isGoingUp/Down may be affecting communication
        boolean done = (this.direction == ElevatorDirection.Up && this.elevator.isGoingUp() && (topSwitchTripped || this.isUpwardMaxAmpsExceededWithDelay())) 
                           ||
                       (this.direction == ElevatorDirection.Down && this.elevator.isGoingDown() && bottomSwitchTripped);

        if (done) {
            this.elevator.elevatorStop();
            logger.info(">>>>> Elevator command FINISHED. topSwitch: {}, bottomSwitch: {}", topSwitchTripped, bottomSwitchTripped);
            this.clearRequirements();
        }
        return done;
    }
    
    protected void execute() {
        this.execCount++;
        if (this.direction == ElevatorDirection.Up) {
            this.elevator.elevatorUp(power);
        } else if (this.direction == ElevatorDirection.Down) {
            this.elevator.elevatorDown(power);
        } else {
            logger.error("Elevator Execute stopped!  Direction not set!");
        }
        // Be explicit in order to try reduce motor "Output not updated often enough" warnings
        this.driveTrain.stopMotor();
    }

    
}
