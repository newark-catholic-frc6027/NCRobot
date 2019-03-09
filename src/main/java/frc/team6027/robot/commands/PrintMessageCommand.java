package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.command.Command;

public class PrintMessageCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private String message;

    public PrintMessageCommand(String myMessage) {
        this.message = myMessage;
    }
    
    @Override
    protected boolean isFinished() {
       return true;
    }
    
    protected void execute() {
        logger.info(message);
        
    }

}
//it's a me mario lets a go