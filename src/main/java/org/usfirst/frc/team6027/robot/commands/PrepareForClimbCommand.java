package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import org.usfirst.frc.team6027.robot.commands.ShiftElevatorCommand.TargetGear;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class PrepareForClimbCommand extends CommandGroup {

    public PrepareForClimbCommand(PneumaticSubsystem pneumaticSubsystem) {
        
        this.addSequential(new ShiftElevatorCommand(TargetGear.Low, pneumaticSubsystem));
        this.addSequential(new DropCarriageCommand(DropFunction.DropForClimb, DriverStation.getInstance(), pneumaticSubsystem));
    }
}
