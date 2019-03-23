package frc.team6027.robot.commands;
import frc.team6027.robot.commands.TeleopManager;
import frc.team6027.robot.sensors.SensorService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.command.Command;

public class ResetGyroCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;

    public ResetGyroCommand(SensorService sensorService) {
        this.sensorService = sensorService;
        
    }
    
    @Override
    protected boolean isFinished() {
       return true; 
    }
    
    @Override    
    protected void execute() {
        logger.info("Gyro Reset");
        sensorService.getGyroSensor().reset();
    }

}