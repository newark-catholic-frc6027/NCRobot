package frc.team6027.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotShutdownHook extends Thread {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public void run() {
        // TODO: Copy robot log somewhere safe and give it a timestamp in the filename
        logger.info("Robot is shutting down.");
    }
}