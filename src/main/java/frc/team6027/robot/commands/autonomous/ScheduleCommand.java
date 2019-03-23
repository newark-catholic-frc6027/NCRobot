package frc.team6027.robot.commands.autonomous;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class ScheduleCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    protected Supplier<Command> commandSupplier;

    public ScheduleCommand(Supplier<Command> commandSupplier) {
        this.commandSupplier = commandSupplier;
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void execute() {
        Command cmd = this.commandSupplier.get();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> Scheduling Command: {}", cmd.getClass().getSimpleName());

        Scheduler.getInstance().add(cmd);
    }
/*
    @Override
    public void cancel() {
        if (this.killable != null) {
            Command cmd = killable.command();
            if (cmd.isRunning()) {
                cmd.cancel();
            }
        }
        super.cancel();
    }
    */
}