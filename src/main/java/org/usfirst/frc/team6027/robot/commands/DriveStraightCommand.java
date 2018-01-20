package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DriveStraightCommand extends Command {
    
    private EncoderSensors encoderSensors;
    private DrivetrainSubsystem drivetrainSubsystem;
    private double driveDistance;
    
    
    public DriveStraightCommand(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem, double driveDistance) {
        this.encoderSensors = encoderSensors;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
    }

    
    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        if(Math.abs(this.encoderSensors.getRightEncoder().getDistance()) >= this.driveDistance) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override 
    protected void execute() {
        this.drivetrainSubsystem.getRobotDrive().drive(0.5, 0);
    }
    
}
