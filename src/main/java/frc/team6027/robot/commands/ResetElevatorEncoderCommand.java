package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import edu.wpi.first.wpilibj.command.Command;

public class ResetElevatorEncoderCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public ResetElevatorEncoderCommand(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }

    protected void execute() {
        logger.info("{} running...", this.getClass().getSimpleName());

        this.sensorService.getEncoderSensors().getElevatorEncoder().reset();
    }

}
