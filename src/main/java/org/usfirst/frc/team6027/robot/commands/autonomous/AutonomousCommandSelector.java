package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.field.Field;

import edu.wpi.first.wpilibj.command.Command;

public class AutonomousCommandSelector {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Field field;
    private Command preferredAutoCommand;
    
    public AutonomousCommandSelector(Field field, Command preferredAutoCommand) {
        this.field = field;
        this.preferredAutoCommand = preferredAutoCommand;
    }
    
    public Command chooseCommand() {
        // If we are positioned in the center station, station 2
        if (this.field.isOurStationCenter()) {
            logger.info("Team Position: CENTER ({})", this.field.getOurStationPosition());
        } else {
            
        }
        
        return null;
    }
    
    protected boolean isNoPreferredCommand() {
        return this.preferredAutoCommand instanceof NoOpCommand;
    }
}
