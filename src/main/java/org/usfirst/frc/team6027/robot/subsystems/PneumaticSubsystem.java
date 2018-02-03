package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
 

public class PneumaticSubsystem extends Subsystem {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DoubleSolenoid solenoid;
    private OperatorDisplay operatorDisplay;
    private DoubleSolenoid.Value solenoidState;
    
    public PneumaticSubsystem(OperatorDisplay operatorDisplay) {
        this.solenoid = new DoubleSolenoid(RobotConfigConstants.SOLENOID_1_MODULE_NUMBER, RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);
        this.operatorDisplay = operatorDisplay;
        
        this.toggleSolenoidForward();
    }
    
    @Override
    protected void initDefaultCommand() {
        
    }

    /**
     * When the run method of the scheduler is called this method will be called.
     */
    @Override
    public void periodic() {
    }
    
    public DoubleSolenoid getSolenoid() {
        return solenoid;
    }

    public void setSolenoid(DoubleSolenoid solenoid) {
        this.solenoid = solenoid;
    }
    
    public void toggle() {
        this.operatorDisplay.setFieldValue("Solenoid State", this.solenoidState.name());
        
        if (this.solenoidState == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleSolenoidForward");
            toggleSolenoidForward();
        } else {
            logger.trace("Calling toggleSolenoidReverse");
            toggleSolenoidReverse();
        }
    }
    
    public void toggleSolenoidReverse() {
        logger.trace("Running toggleSolenoidReverse");
        this.solenoid.set(DoubleSolenoid.Value.kReverse);
        this.operatorDisplay.setFieldValue("Speed", "HIGH");
        this.solenoidState = DoubleSolenoid.Value.kReverse;
    }
    
    public void toggleSolenoidForward() {
        logger.trace("Running toggleSolenoidForward");
        this.solenoid.set(DoubleSolenoid.Value.kForward);
        this.operatorDisplay.setFieldValue("Speed", "LOW");
        this.solenoidState = DoubleSolenoid.Value.kForward;
    }
    
    public void toggleSolenoidOff() {
        logger.trace("Running toggleSolenoidOff");
        this.solenoid.set(DoubleSolenoid.Value.kOff);
    }

}
