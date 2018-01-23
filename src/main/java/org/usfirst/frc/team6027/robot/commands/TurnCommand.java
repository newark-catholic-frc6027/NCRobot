package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class TurnCommand extends Command {

    private Gyro gyro;
    private DrivetrainSubsystem drivetrain;
    private double angle;
    
   public TurnCommand (Gyro gyro, DrivetrainSubsystem drivetrain, double angle) {
       this.gyro = gyro;
       this.drivetrain = drivetrain;
       this.angle = angle;
   }
    
    @Override
    protected boolean isFinished() {
       if (this.gyro.getAngle() >= this.angle) {
           return true;
       }
        return false;
    }
    protected void execute() {
        this.drivetrain.getRobotDrive().drive (0.2, 0.5);
    }
}
