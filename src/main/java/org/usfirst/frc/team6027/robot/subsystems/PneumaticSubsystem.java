package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PneumaticSubsystem extends Subsystem {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private OperatorDisplay operatorDisplay;

	private DoubleSolenoid driveSolenoid;
	private DoubleSolenoid.Value driveSolenoidState;

    private DoubleSolenoid gripperSolenoid;
    private DoubleSolenoid.Value gripperSolenoidState;
	
	public PneumaticSubsystem(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;
		this.driveSolenoid = new DoubleSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
				RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);
		this.toggleDriveSolenoidForward();
		
        this.gripperSolenoid = new DoubleSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
                RobotConfigConstants.SOLENOID_2_PORT_A, RobotConfigConstants.SOLENOID_2_PORT_B);
		this.toggleGripperSolenoidForward();
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

	public DoubleSolenoid getDriveSolenoid() {
		return driveSolenoid;
	}

    public DoubleSolenoid getGripperSolenoid() {
        return gripperSolenoid;
    }

	public void toggleDriveSolenoid() {
        this.operatorDisplay.setFieldValue("Solenoid State", this.driveSolenoidState.name());

        if (this.driveSolenoidState == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleSolenoidForward");
            toggleDriveSolenoidForward();
        } else {
            logger.trace("Calling toggleSolenoidReverse");
            toggleDriveSolenoidReverse();
        }
	}

    public void toggleGripperSolenoid() {
        this.operatorDisplay.setFieldValue("Gripper Solenoid State", this.gripperSolenoidState.name());

        if (this.gripperSolenoidState == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleGripperSolenoidForward");
            toggleGripperSolenoidForward();
        } else {
            logger.trace("Calling toggleGripperSolenoidReverse");
            toggleGripperSolenoidReverse();
        }
    }
	
	public void toggleDriveSolenoidReverse() {
		logger.trace("Running toggleDriveSolenoidReverse");
		this.driveSolenoid.set(DoubleSolenoid.Value.kReverse);
		this.operatorDisplay.setFieldValue("Speed", "HIGH");
		this.driveSolenoidState = DoubleSolenoid.Value.kReverse;
	}

	public void toggleDriveSolenoidForward() {
		logger.trace("Running toggleDriveSolenoidForward");
		this.driveSolenoid.set(DoubleSolenoid.Value.kForward);
		this.operatorDisplay.setFieldValue("Speed", "LOW");
		this.driveSolenoidState = DoubleSolenoid.Value.kForward;
	}
	
	public void toggleDriveSolenoidOff() {
		logger.trace("Running toggleDriveSolenoidOff");
		this.driveSolenoid.set(DoubleSolenoid.Value.kOff);
	}

    
    public void toggleGripperSolenoidReverse() {
        logger.trace("Running toggleGripperSolenoidReverse");
        this.gripperSolenoid.set(DoubleSolenoid.Value.kReverse);
        this.operatorDisplay.setFieldValue("Gripper", "OPENED");
        this.gripperSolenoidState = DoubleSolenoid.Value.kReverse;
    }

    public void toggleGripperSolenoidForward() {
        logger.trace("Running toggleGripperSolenoidForward");
        this.gripperSolenoid.set(DoubleSolenoid.Value.kForward);
        this.operatorDisplay.setFieldValue("Gripper", "CLOSED");
        this.gripperSolenoidState = DoubleSolenoid.Value.kForward;
    }
    
    public void toggleGripperSolenoidOff() {
        logger.trace("Running toggleGripperSolenoidOff");
        this.gripperSolenoid.set(DoubleSolenoid.Value.kOff);
    }

	
}
