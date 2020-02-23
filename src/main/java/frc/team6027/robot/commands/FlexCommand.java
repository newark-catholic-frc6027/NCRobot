package frc.team6027.robot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team6027.robot.subsystems.Drive;

public class FlexCommand extends Command {
    protected final Logger logger = LogManager.getLogger(getClass());

    protected boolean prepared = false;
    protected FlexCommandGroup commandGroup;


    public FlexCommand() {
    }


    protected FlexCommandGroup getCommandGroup() {
        return this.commandGroup;
    }
    
    protected void prepare() {
        this.commandGroup = new FlexCommandGroup();
    }

    @Override
    public void start() {
        prepare();
        this.commandGroup.start();
    }
    
    @Override
    protected boolean isFinished() {
        return this.commandGroup.isFinished();
    }

    @Override
    public void cancel() {
        this.commandGroup.cancel();
    }


    public static class DummyCommand extends Command {
        private final Logger logger = LogManager.getLogger(getClass());

        private String name;

        public DummyCommand(Drive drivetrain, String name) {
//            requires(drivetrain);
            this.name = name;
        }

        @Override
        protected boolean isFinished() {
            return true;
        }

        @Override
        protected void execute() {
            this.logger.debug("Command '{}' executed", this.name);

        }
    }

    public static class DummyCommandGroup extends CommandGroup {
        private final Logger logger = LogManager.getLogger(getClass());


        public DummyCommandGroup() {
        }

        @Override
        public boolean isFinished() {
            return super.isFinished();
        }
    }

}