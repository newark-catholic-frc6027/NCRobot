package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PneumaticSubsystem extends Subsystem {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private OperatorDisplay operatorDisplay;

	private DoubleSolenoid driveSolenoid;
	private DoubleSolenoid.Value driveSolenoidState;

    private DoubleSolenoid gripperSolenoid;
    private DoubleSolenoid.Value gripperSolenoidState;
    
    private DoubleSolenoid kickerSolenoid;
    private DoubleSolenoid.Value kickerSolenoidState;
	
    private PneumaticsInitializationCommand pneumaticInitializationCommand;
    
	public PneumaticSubsystem(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;
        
        this.driveSolenoid = new DoubleSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
                RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);

        this.gripperSolenoid = new DoubleSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
                RobotConfigConstants.SOLENOID_2_PORT_A, RobotConfigConstants.SOLENOID_2_PORT_B);

        this.kickerSolenoid = new DoubleSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
                RobotConfigConstants.SOLENOID_3_PORT_A, RobotConfigConstants.SOLENOID_3_PORT_B);
     
	}

	@Override
	public void initDefaultCommand() {
	    this.pneumaticInitializationCommand = new PneumaticsInitializationCommand(this);
	    this.setDefaultCommand(this.pneumaticInitializationCommand);

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
    
    public DoubleSolenoid getKickerSolenoid() {
        return kickerSolenoid;
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
    
    public boolean isKickerOut() {
        return this.kickerSolenoid.get() == DoubleSolenoid.Value.kReverse;
    }
    
    public void toggleKickerSolenoid() {
        this.operatorDisplay.setFieldValue("Kicker Solenoid State", this.kickerSolenoidState.name());

        if (this.kickerSolenoidState == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleKickerSolenoidForward");
            toggleKickerSolenoidForward();
        } else {
            logger.trace("Calling toggleKickerSolenoidReverse");
            toggleKickerSolenoidReverse();
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
    
    public void toggleKickerSolenoidReverse() {
        logger.trace("Running toggleKickerSolenoidReverse");
        this.kickerSolenoid.set(DoubleSolenoid.Value.kReverse);
        this.operatorDisplay.setFieldValue("Kicker", "FORWARD");
        this.kickerSolenoidState = DoubleSolenoid.Value.kReverse;
    }

    public void toggleKickerSolenoidForward() {
        logger.trace("Running toggleKickerSolenoidForward");
        this.kickerSolenoid.set(DoubleSolenoid.Value.kForward);
        this.operatorDisplay.setFieldValue("Kicker", "Retracted");
        this.kickerSolenoidState = DoubleSolenoid.Value.kForward;
    }
    
    public void toggleKickerSolenoidOff() {
        logger.trace("Running toggleKickerSolenoidOff");
        this.kickerSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    class PneumaticsInitializationCommand extends Command {
        boolean initialized = false;
        
        boolean driveSolenoidToggled = false;
        boolean driveSolenoidInitialized = false;
        
        boolean gripperSolenoidToggled = false;
        boolean gripperSolenoidInitialized = false;
        
        boolean kickerSolenoidToggled = false;
        boolean kickerSolenoidInitialized = false;
        
        public PneumaticsInitializationCommand(PneumaticSubsystem subsys) {
            requires(subsys);
        }
        
        @Override
        protected void execute() {
            if (this.initialized) {
                return;
            }
            
            if (! driveSolenoidToggled) {
                logger.trace("Initializing Drive Solenoid...");
                PneumaticSubsystem.this.toggleDriveSolenoidForward();
                this.driveSolenoidToggled = true;
            } else {
                if (PneumaticSubsystem.this.driveSolenoid.get() == DoubleSolenoid.Value.kForward) {
                    driveSolenoidInitialized = true;
                    PneumaticSubsystem.this.toggleDriveSolenoidOff();
                    logger.trace("Drive Solenoid initialized.");
                } else {
                    logger.trace("Drive Solenoid not initialized yet");
                }
            }
            
            if (driveSolenoidInitialized) {
                if (! gripperSolenoidToggled) {
                    logger.trace("Initializing Gripper Solenoid...");
                    PneumaticSubsystem.this.toggleGripperSolenoidForward();
                    this.gripperSolenoidToggled = true;
                } else {
                    if (PneumaticSubsystem.this.gripperSolenoid.get() == DoubleSolenoid.Value.kForward) {
                        gripperSolenoidInitialized = true;
                        PneumaticSubsystem.this.toggleGripperSolenoidOff();
                        logger.trace("Gripper Solenoid initialized.");
                    } else {
                        logger.trace("Gripper Solenoid not initialized yet");
                    }
                }
            }
            

            if (driveSolenoidInitialized && gripperSolenoidInitialized) {
                if (! kickerSolenoidToggled) {
                    logger.trace("Initializing Kicker Solenoid...");
                    PneumaticSubsystem.this.toggleKickerSolenoidForward();
                    this.kickerSolenoidToggled = true;
                } else {
                    if (PneumaticSubsystem.this.kickerSolenoid.get() == DoubleSolenoid.Value.kForward) {
                        kickerSolenoidInitialized = true;
                        PneumaticSubsystem.this.toggleKickerSolenoidOff();
                        logger.trace("Kicker Solenoid initialized.");
                    } else {
                        logger.trace("Kicker Solenoid not initialized yet");
                    }
                }
            }
            
            this.initialized = driveSolenoidInitialized && gripperSolenoidInitialized && kickerSolenoidInitialized;
        }
        
        @Override
        protected boolean isFinished() {
            return this.initialized;
        }
        
    }
	
}
