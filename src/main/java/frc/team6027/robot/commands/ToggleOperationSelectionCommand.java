package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import edu.wpi.first.wpilibj.command.Command;

public class ToggleOperationSelectionCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    

    @Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
        logger.info("{} running...", getClass().getSimpleName());
        AutonomousCommandManager mgr = AutonomousCommandManager.instance();
        
        mgr.setOperationSelection(mgr.getOperationSelection().toggle());
    }

}
