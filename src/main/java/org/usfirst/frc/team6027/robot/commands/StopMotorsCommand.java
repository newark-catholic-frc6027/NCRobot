package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class StopMotorsCommand extends Command {

    private ElevatorSubsystem elevatorSubsystem;
    private DrivetrainSubsystem drivetrain;
    boolean done = false;

    public StopMotorsCommand(ElevatorSubsystem elevatorSubsystem, DrivetrainSubsystem drivetrain) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.drivetrain = drivetrain;
    }
    
    @Override
    protected boolean isFinished() {
        return done;
    }
    
    protected void execute() {
        this.drivetrain.stopMotor();
        this.elevatorSubsystem.elevatorStop();
        this.done = true;
    }

}
