package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;

import edu.wpi.first.wpilibj.command.Command;

public class MoveByEncoderCommand extends Command {


	 private EncoderSensors encoderSensors;

	 public MoveByEncoderCommand(EncoderSensors encoderSensors) {
		 	this.encoderSensors = encoderSensors;
		 	
	 }
	 
	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return true;
		
	}
	
	@Override 
	protected void execute() {

	}
	 
}
