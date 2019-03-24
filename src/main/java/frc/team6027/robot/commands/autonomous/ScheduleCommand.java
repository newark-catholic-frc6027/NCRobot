package frc.team6027.robot.commands.autonomous;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * Provides a means to call back to a provided command for scheduling.
 * Can be used for dynamically creating and scheduling a command instead
 * as opposed to creating a command at compile time.  Also supports
 * ignoring a command if the command is already running in Autonomous.
 */
public class ScheduleCommand<T extends Command> extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    protected Supplier<T> commandSupplier;
    protected Class<T> commandClass = null;
    protected boolean skipIfAlreadyRunningInAuto = false;

    public ScheduleCommand(Supplier<T> commandSupplier, Class<T> commandClass) {
        this(commandSupplier, commandClass, false);
    }

    public ScheduleCommand(Supplier<T> commandSupplier, Class<T> commandClass, boolean ifNotAlreadyRunningInAuto) {
        this.commandSupplier = commandSupplier;
        this.commandClass = commandClass;
        this.skipIfAlreadyRunningInAuto = ifNotAlreadyRunningInAuto;
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void execute() {
        boolean doSchedule = true;
        Command cmd = null;
        if (this.skipIfAlreadyRunningInAuto) {
            Class<?> cmdClass = this.commandClass;
            
            // If we are running just a generic Command object,
            // we have to actually invoke the supplier to get the command
            // in order to know the type.  Otherwise, just use
            // the type of the command we were given on construction.
            if (this.commandClass == Command.class) {
                cmd =  this.commandSupplier.get();
                cmdClass = cmd.getClass();
                doSchedule = ! AutonomousCommandManager.instance().isAutoCommandRunning(cmd.getClass());
            } else if (AutonomousCommandManager.instance().isAutoCommandRunning(this.commandClass)) {
                doSchedule = false;
            }

            if (! doSchedule) {
                this.logger.info(">>>>>>>>>>>>>>>>>>>> ScheduleCommand NOT RUN since command of type {} is already running in Auto", 
                    cmdClass.getSimpleName());
            }
        }
        if (doSchedule) {
            if (cmd == null) {
                cmd = this.commandSupplier.get();
            }
            this.logger.info(">>>>>>>>>>>>>>>>>>>> Scheduling Command: {}", cmd.getClass().getSimpleName());
            Scheduler.getInstance().add(cmd);
        }
    }

/*
    public boolean isTypeMatch(Object o, Class<?> ofThisType) {
        return o != null && ofThisType == o.getClass();
    }

    private ScheduleCommand() {

    }

    public static void main(String[] args) {
        ScheduleCommand s = new ScheduleCommand();
        System.out.println(s.isTypeMatch(null, null));
    }
*/
}