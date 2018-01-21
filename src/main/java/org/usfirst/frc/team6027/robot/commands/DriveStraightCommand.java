package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.command.Command;

public class DriveStraightCommand extends Command {
    
   
    
    private EncoderSensors encoderSensors;
    private DrivetrainSubsystem drivetrainSubsystem;
    private OperatorDisplay operatorDisplay;
    private double driveDistance;
    private double totalDistance = 0;
    private double currentDistance = 0;
    double diff = 0; //Creating variable for the difference between right encoder distance and left encoder distance
    int leftcount = 0;
    int rightcount = 0;
    int centercount = 0;
    
    public DriveStraightCommand(EncoderSensors encoderSensors, DrivetrainSubsystem drivetrainSubsystem, OperatorDisplay operatorDisplay, double driveDistance) {
        this.encoderSensors = encoderSensors;
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
        this.operatorDisplay = operatorDisplay;
    }

    
    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        return true;
        
//        if(Math.abs(totalDistance)>= this.driveDistance) {
//            this.encoderSensors.getRightEncoder().reset();
//            this.encoderSensors.getLeftEncoder().reset();
//            return true;
//        } else {
//            return false;
//        }
    }
    
    @Override 
    protected void execute() {
//        double diff=this.encoderSensors.getRightEncoder().getDistance() - this.encoderSensors.getLeftEncoder().getDistance();
//        totalDistance = totalDistance +  this.encoderSensors.getRightEncoder().getDistance();
//        /*
//        if(this.encoderSensors.getRightEncoder().getDistance()<=1 || this.encoderSensors.getRightEncoder().getDistance()>=-5) {
//            this.drivetrainSubsystem.getRobotDrive().drive(-0.3, 0);
//        }*/
//        
//       
//        if(diff>0.05) {
//            rightcount = rightcount + 1;
//            this.drivetrainSubsystem.getRobotDrive().drive(-0.3, 0);
//        } else if(diff<-0.05) {
//            leftcount++;
//            if(leftcount % 4 == 0) {  
//                this.drivetrainSubsystem.getRobotDrive().drive(-0.3, -0.01); 
//                
//            }
//          
//        } else if (diff<=0.05 || diff>=-0.05) {
//            this.encoderSensors.getRightEncoder().reset();
//            this.encoderSensors.getLeftEncoder().reset();
//            centercount++;
//            this.drivetrainSubsystem.getRobotDrive().drive(-0.3, 0);
//        }
//   this.operatorDisplay.setNumericFieldValue("leftcount",leftcount);
//   this.operatorDisplay.setNumericFieldValue("rightcount",rightcount);
//   this.operatorDisplay.setNumericFieldValue("centercount", centercount);
   
    }
    
}
