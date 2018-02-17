package org.usfirst.frc.team6027.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public class NoOpCommand  extends Command {
    public NoOpCommand() {
        this.setName("No Selection");
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }
    
}
