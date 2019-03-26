package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;

public class DelayCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    protected int delayMs;
    protected Long startTime;
    protected Long finishTime = null;

    public DelayCommand(int delayMs) {

        this.setName("No Command");
        this.delayMs = delayMs;

    }

    @Override
    protected void execute() {
        if (this.finishTime == null) {
            this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
            this.startTime = System.currentTimeMillis();
            this.finishTime = this.startTime + this.delayMs;
        }

    }

    @Override
    protected boolean isFinished() {
        if (System.currentTimeMillis() >= this.finishTime) {
            this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command FINISHED", this.getClass().getSimpleName());
            this.finishTime = null;
            this.startTime = null;
            return true;
        } else {
            return false;
        }
    }
}