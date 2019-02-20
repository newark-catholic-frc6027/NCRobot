package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import frc.team6027.robot.commands.ShiftElevatorCommand.TargetGear;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class PrepareForClimbCommand extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    public PrepareForClimbCommand(PneumaticSubsystem pneumaticSubsystem) {
        
        this.addSequential(new ShiftElevatorCommand(TargetGear.Low, pneumaticSubsystem));
        this.addSequential(new DropCarriageCommand(DropFunction.DropForClimb, DriverStation.getInstance(), pneumaticSubsystem, null, false));
    }
    
    @Override
    public void execute() {
        logger.info("Running PrepareForClimbCommand...");
        super.execute();
    }
}
