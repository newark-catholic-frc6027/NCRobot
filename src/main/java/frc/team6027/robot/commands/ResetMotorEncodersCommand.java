package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import edu.wpi.first.wpilibj.command.Command;

public class ResetMotorEncodersCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    boolean done = false;

    public ResetMotorEncodersCommand(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
        logger.info("{} running...", this.getClass().getSimpleName());
        this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft).reset();
        this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight).reset();
}

}
