package frc.team6027.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public abstract class AutoCommand extends Command {

    protected boolean killed = false;

    @Override
    public void start(){
        AutonomousCommandManager.instance().setCurrent(this);
        super.start();
    }

    public boolean isKilled() {
        return this.killed;
    }

    public void kill() {
        this.killed = true;
    }
}