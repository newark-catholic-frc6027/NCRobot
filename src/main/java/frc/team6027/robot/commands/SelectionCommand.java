package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.field.ObjectSelection;
import frc.team6027.robot.field.OperationSelection;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.command.Command;

public class SelectionCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());
    
    private ObjectSelection objectSelection;
    private LevelSelection levelSelection;
    private OperationSelection operationSelection;
    
    public SelectionCommand(ObjectSelection desiredSelection) {
        this.objectSelection = desiredSelection;
        this.levelSelection = null;
        this.operationSelection = null;
    }

    public SelectionCommand(LevelSelection desiredSelection) {
        this.levelSelection = desiredSelection;
        this.objectSelection = null;
        this.operationSelection = null;
    }

    public SelectionCommand(OperationSelection desiredSelection) {
        this.operationSelection = desiredSelection;
        this.objectSelection = null;
        this.levelSelection = null;
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
    
    protected void execute() {
        logger.info("{} running...", getClass().getSimpleName());
        if (this.objectSelection != null) {
            AutonomousCommandManager.instance().setObjectSelection(this.objectSelection);
        }

        if (this.levelSelection != null) {
            AutonomousCommandManager.instance().setLevelSelection(this.levelSelection);
        }

        if (this.operationSelection != null) {
            AutonomousCommandManager.instance().setOperationSelection(this.operationSelection);
        }

    }

}
