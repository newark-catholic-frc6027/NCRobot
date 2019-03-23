package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;

public interface KillableAutoCommand {
    /**
     * Implementors should call this method from their
     * start() method.
     */
    default public void registerAsKillable() {
        AutonomousCommandManager.instance().setCurrent(this);
    }
    default public void beforeKill() {}
    default public void afterKill() {}
    default public void onComplete() {
        AutonomousCommandManager.instance().setCurrent(null);
    }

    default public void kill() {
        if (this instanceof Command) {
            if (((Command)this).isRunning()) {
                LogManager.getLogger(this.getClass()).warn("Kill invoked on {}, calling cancel...", this.getClass().getSimpleName());
                this.beforeKill();
                ((Command)this).cancel();
                this.afterKill();
            } else {
                LogManager.getLogger(this.getClass()).info("Kill invoked on {}, but the command isn't running.", this.getClass().getSimpleName());
            }
        }

    }
}