package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DriveStraightCommand extends Command {
    
    private EncoderSensors encoderSensors;
    private DrivetrainSubsystem drivetrainSubsystem;
    private double driveDistance;
    double diff = 0; //Creating variable for the difference between right encoder distance and left encoder distance
    
    
    public DriveStraightCommand(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem, double driveDistance) {
        this.encoderSensors = encoderSensors;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
    }

    
    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        
        
        if(Math.abs(this.encoderSensors.getRightEncoder().getDistance()) >= this.driveDistance) {
            this.encoderSensors.getRightEncoder().reset();
            this.encoderSensors.getLeftEncoder().reset();
            return true;
        } else {
            return false;
        }
    }
    
    @Override 
    protected void execute() {
        double diff=this.encoderSensors.getRightEncoder().getDistance() - this.encoderSensors.getLeftEncoder().getDistance();
        if(this.encoderSensors.getRightEncoder().getDistance()<=1 || this.encoderSensors.getRightEncoder().getDistance()>=-5) {
            this.drivetrainSubsystem.getRobotDrive().drive(0.3, -0.7);
        }
        if(diff>2) {
            this.drivetrainSubsystem.getRobotDrive().drive(0.3, -0.7);
        } else if(diff<-2) {
            this.drivetrainSubsystem.getRobotDrive().drive(0.3, 0.4);
        } else if (diff<=2 || diff>=-2) {
            this.drivetrainSubsystem.getRobotDrive().drive(0.3, 0 );
        }
    }
    
}
