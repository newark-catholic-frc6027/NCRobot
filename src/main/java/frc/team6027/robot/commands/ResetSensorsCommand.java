package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import edu.wpi.first.wpilibj.command.Command;

public class ResetSensorsCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public ResetSensorsCommand(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
        logger.info("ResetSensorsCommand running...");
        this.sensorService.resetAll();
    }

}
