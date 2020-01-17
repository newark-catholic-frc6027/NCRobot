package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;

public interface KillableAutoCommand {
    public void registerAsKillable();
        /**
     * Implementors should call this method from their
     * start() method.
     */
    default public void default_registerAsKillable() {
        AutonomousCommandManager.instance().setCurrent(this);
    }
    default public Command command() {
        if (this instanceof Command) {
            return (Command) this;
        } else {
            LogManager.getLogger(this.getClass()).error("{} is not a Command object!", this.getClass().getSimpleName());
            return null;
        }

    }

    default public void beforeKill() {}
    default public void afterKill() {}
    public void onComplete();
    default public void default_onComplete() {
        AutonomousCommandManager.instance().unsetCurrent(this);
    }

    default public void kill() {
        if (this instanceof Command) {
            Command cmd = this.command();
            if (cmd.isRunning()) {
                LogManager.getLogger(this.getClass()).warn("Kill invoked on {}, calling cancel...", this.getClass().getSimpleName());
                this.beforeKill();
                cmd.cancel();
                this.afterKill();
            } else {
                LogManager.getLogger(this.getClass()).info("Kill invoked on {}, but the command isn't running.", this.getClass().getSimpleName());
            }
        }

    }
}