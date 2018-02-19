package org.usfirst.frc.team6027.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public class NoOpCommand  extends Command {
    private static NoOpCommand instance = null;
    
    public synchronized static final NoOpCommand getInstance() {
        if (instance == null) {
            instance = new NoOpCommand();
        }
        
        return instance;
    }
    
    private NoOpCommand() {
        this.setName("No Command");
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }
    
}
