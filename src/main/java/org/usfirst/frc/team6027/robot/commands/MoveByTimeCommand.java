package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;

public class MoveByTimeCommand extends Command {

//	 private CANTalon frontRight = new CANTalon(RobotConfigConstants.FRONT_RIGHT_CANTALON_DRIVE_ID);
//	 private CANTalon backRight = new CANTalon(RobotConfigConstants.REAR_RIGHT_CANTALON_DRIVE_ID);
//	 private CANTalon frontLeft = new CANTalon(RobotConfigConstants.FRONT_LEFT_CANTALON_DRIVE_ID);
//	 private CANTalon backLeft = new CANTalon(RobotConfigConstants.REAR_LEFT_CANTALON_DRIVE_ID);

//	 private RobotDrive robotDrive = new RobotDrive(frontLeft,backLeft,frontRight,backRight);
	 private long startTimeMillis;
	 public MoveByTimeCommand() {
		 startTimeMillis = System.currentTimeMillis();		
	 }
	 
	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		long timeElapsed = System.currentTimeMillis()-startTimeMillis;
		if (timeElapsed >= 2000) {
			return true;
		}
		return false;
	}
	
	@Override 
	protected void execute() {
//		this.robotDrive.drive(0.5, 0);
	}
	 
}
