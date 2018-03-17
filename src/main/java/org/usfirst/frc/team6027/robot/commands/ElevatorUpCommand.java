package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Raises and lowers the elevator.
 */
public class ElevatorUpCommand extends Command {
   
	private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    DigitalInput limitSwitchBot = new DigitalInput(8);
    DigitalInput limitSwitchTop = new DigitalInput(9);
	double winchSpeedUp = -1;
	
    
  
    
    @Override
    protected void initialize() {
        
    }
    
    
    @Override 
    public void execute() {
       
           logger.trace("Running ElevatorCommand");
           logger.trace("Limit Switch Top:{}, Limit Switch Bottom:{}", limitSwitchTop.get(),limitSwitchBot.get());
          //this.elevatorGearBoxMaster.set(0);
           this.elevatorGearBoxMaster.set(winchSpeedUp);
       
    }
    
    
    @Override
    protected boolean isFinished() {

    	    	
    	if(this.limitSwitchTop.get()) {
    		this.elevatorGearBoxMaster.set(0);
    		return true;
    	}
    	
    		return false;
    	
    		
    	
    }
    
    @Override
    protected void end() {
       
        
    }

}
