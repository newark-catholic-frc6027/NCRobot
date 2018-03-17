package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Raises and lowers the elevator.
 */
public class ElevatorDownCommand extends Command {
   
	private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

	double winchSpeedDown = 1;
	
    
  
    
    @Override
    protected void initialize() {
        
    }
    
    
    @Override 
    public void execute() {
       
            logger.trace("Running ElevatorCommand");
            this.elevatorGearBoxMaster.set(0);
            //this.elevatorGearBoxMaster.set(winchSpeedDown);
       
    }
    
    
    @Override
    protected boolean isFinished() {

    	//When bottom limit switch is made
    	
    	return false;
    }
    
    @Override
    protected void end() {
       
        
    }

}
