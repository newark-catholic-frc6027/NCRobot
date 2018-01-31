package org.usfirst.frc.team6027.robot.subsystems;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
 

public class PneumaticSubsystem extends Subsystem {

    private DoubleSolenoid solenoid;
    
    public PneumaticSubsystem() {
        this.solenoid = new DoubleSolenoid(RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);
    }
    
    @Override
    protected void initDefaultCommand() {
        // TODO Auto-generated method stub
        
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

}
