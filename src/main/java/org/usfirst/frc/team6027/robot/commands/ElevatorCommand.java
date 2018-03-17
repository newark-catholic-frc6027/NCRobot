package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public final static String NAME = "Elevator Command";
    protected static final int LOG_REDUCTION_MOD = 10;
    protected int execCount = 0;

    
    public enum ElevatorDirection {
        Up,
        Down
    };
    
    private ElevatorDirection direction = null;
    private ElevatorSubsystem elevator;
    private LimitSwitchSensors limitSwitches;
    private double power = 0.5;
    
    public ElevatorCommand(ElevatorDirection direction, double power, SensorService sensorService, ElevatorSubsystem elevator) {
        this.direction = direction;
        this.elevator = elevator;
        this.power = power;
        this.limitSwitches = sensorService.getLimitSwitchSensors();
        this.setName(NAME);
        requires(elevator);
    }

    @Override
    protected void end() {
        this.clearRequirements();
    }
    
    @Override
    protected boolean isFinished() {
        boolean bottomSwitchTripped = this.limitSwitches.getLimitSwitch(LimitSwitchId.MastBottom).get();
        boolean topSwitchTripped = this.limitSwitches.getLimitSwitch(LimitSwitchId.MastTop).get();

        boolean done = (this.direction == ElevatorDirection.Up && this.elevator.isGoingUp() && topSwitchTripped) 
                           ||
                       (this.direction == ElevatorDirection.Down && this.elevator.isGoingDown() && bottomSwitchTripped);
              
        if (done) {
            logger.info(">>>>> Elevator command finished. topSwitch: {}, bottomSwitch: {}", topSwitchTripped, bottomSwitchTripped);
            this.elevator.elevatorStop();
            this.clearRequirements();
        }
        return done;
    }
    
    protected void execute() {
        this.execCount++;
        if (execCount % LOG_REDUCTION_MOD == 0) {
            logger.trace("Elevator direction: {}, power: {}", this.elevator.isGoingUp() ? "UP" : (this.elevator.isGoingDown() ? "DOWN" : "N/A"), 
                    this.elevator.getPower() );
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
