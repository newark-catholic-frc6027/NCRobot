package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;

public class KillCurrentAutoCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override 
    protected void execute() {
        this.logger.info("Attempting to kill current autonomous command...");
        AutonomousCommandManager.instance().killCurrent();
    }

}
