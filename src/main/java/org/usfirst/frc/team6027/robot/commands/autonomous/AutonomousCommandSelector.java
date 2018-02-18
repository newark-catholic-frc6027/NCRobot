package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.field.Field.PlatePosition;

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
            return chooseCenterStationCommand();
        } else if (this.field.isOurStationLeft()) {
            return chooseLeftStationCommand();
        } else { // Our Station is on Right
            return chooseRightStationCommand();
        }
    }
    
    private Command chooseRightStationCommand() {
        Command chosenCommand = null;
        
        if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchRight)) {
            if (this.isNoPreferredCommand()) {
                // TODO: deliver END RIGHT
            } else {
                // TODO: check that the preferred command doesn't contradict with our assignment.  If it does, just 
                // deliver END RIGHT.  If it doesn't just return the preferred command.
            }
            
        } else { // Assigned Switch Plate is on left
            // TODO:
            // Either Charge Straight ahead to cross the line -- OR --
            // Deliver to the Scale if right plate is assigned to us
        }
        
        return chosenCommand;
    }

    private Command chooseLeftStationCommand() {
        Command chosenCommand = null;

        if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchLeft)) {
            if (this.isNoPreferredCommand()) {
                // TODO: deliver END LEFT
            } else {
                // TODO: check that the preferred command doesn't contradict with our assignment.  If it does, just 
                // deliver END LEFT.  If it doesn't just return the preferred command.
            }
            
        } else { // Assigned Switch Plate is on right
            // TODO:
            // Either Charge Straight ahead to cross the line -- OR --
            // Deliver to the Scale if left plate is assigned to us
        }
        
        return chosenCommand;
    }

    private Command chooseCenterStationCommand() {
        Command chosenCommand = null;
        logger.info("Team Position: CENTER ({})", this.field.getOurStationPosition());
        // If driver's didn't select a preferred command, automatically select one
        if (this.isNoPreferredCommand()) {
            if (this.field.isPlateAssignedToUs(PlatePosition.OurSwitchLeft)) {
                // TODO: 
                // deliver front left
            } else {
                // TODO:
                // deliver front right
            }
        } else {
            // TODO: check that the preferred command doesn't contradict with our assignment.  If it does, just 
            // get across the line.  If it doesn't just return the preferred command.
        }
        
        return chosenCommand;
    }

    protected boolean isNoPreferredCommand() {
        return this.preferredAutoCommand instanceof NoOpCommand;
    }
}
