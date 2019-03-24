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
            AutonomousCommandManager mgr = AutonomousCommandManager.instance();
            
            // If we are running just a generic Command object,
            // we have to actually invoke the supplier to get the command
            // in order to know the type.  Otherwise, just use
            // the type of the command we were given on construction.
            if (this.commandClass == Command.class) {
                cmd =  this.commandSupplier.get();
                cmdClass = cmd.getClass();
                doSchedule = ! mgr.isAutoCommandRunning(cmd.getClass());
            } else if (mgr.isAutoCommandRunning(this.commandClass)) {
                doSchedule = false;
            }

            if (! doSchedule) {
                this.logger.info(">>>>>>>>>>>>>>>>>>>> ScheduleCommand NOT RUN since command of type {}({}) is already running in Auto. ", 
                    cmdClass.getSimpleName(), mgr.currentCommandId());
            }
        }
        if (doSchedule) {
            if (cmd == null) {
                try {
                    cmd = this.commandSupplier.get();
                } catch (Exception ex) {
                    logger.error("Failed to supply command of type {}. Error: {},{}", 
                        this.commandClass.getSimpleName(), ex.getClass().getName(), ex.getMessage());
                }
            }
            if (cmd != null) {
                this.logger.info(">>>>>>>>>>>>>>>>>>>> Scheduling Command: {}({})", cmd.getClass().getSimpleName(), Integer.toHexString(cmd.hashCode()));
                Scheduler.getInstance().add(cmd);
            } else {
                this.logger.warn(">>>>>>>>>>>>>>>>>>>> ScheduleCommand NOT RUN since command of type {} was not supplied!", this.commandClass.getSimpleName());
            }
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