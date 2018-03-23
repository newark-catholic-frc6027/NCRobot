package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;

public class PneumaticsInitializationCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean initialized = false;
    
    private boolean driveSolenoidToggled = false;
    private boolean driveSolenoidInitialized = false;
    
    private boolean gripperSolenoidToggled = false;
    private boolean gripperSolenoidInitialized = false;
    
    private boolean kickerSolenoidToggled = false;
    private boolean kickerSolenoidInitialized = false;
    
    private PneumaticSubsystem pneumaticSubsystem;
    public PneumaticsInitializationCommand(PneumaticSubsystem subsys) {
        this.pneumaticSubsystem = subsys;
        requires(subsys);
    }
            
    @Override
    protected void initialize() {
        logger.info("Pneumatics initialize method called, but initialize doesn't do anything.");
    }
    
    @Override
    protected void execute() {
        if (this.initialized) {
            logger.info("Pneumatics already initialized, nothing to do.");
            return;
        }
        
        if (! driveSolenoidToggled) {
            logger.trace("Initializing Drive Solenoid...");
            this.pneumaticSubsystem.toggleDriveSolenoidForward();
            this.driveSolenoidToggled = true;
        } else {
            if (this.pneumaticSubsystem.getDriveSolenoid().get() == DoubleSolenoid.Value.kForward) {
                driveSolenoidInitialized = true;
                this.pneumaticSubsystem.toggleDriveSolenoidOff();
                logger.trace("Drive Solenoid initialized.");
            } else {
                logger.trace("Drive Solenoid not initialized yet");
            }
        }
        
        if (driveSolenoidInitialized) {
            if (! gripperSolenoidToggled) {
                logger.trace("Initializing Gripper Solenoid...");
                this.pneumaticSubsystem.toggleGripperSolenoidForward();
                this.gripperSolenoidToggled = true;
            } else {
                if (this.pneumaticSubsystem.getGripperSolenoid().get() == DoubleSolenoid.Value.kForward) {
                    gripperSolenoidInitialized = true;
                    this.pneumaticSubsystem.toggleGripperSolenoidOff();
                    logger.trace("Gripper Solenoid initialized.");
                } else {
                    logger.trace("Gripper Solenoid not initialized yet");
                }
            }
        }
        

        if (driveSolenoidInitialized && gripperSolenoidInitialized) {
            if (! kickerSolenoidToggled) {
                logger.trace("Initializing Kicker Solenoid...");
                this.pneumaticSubsystem.toggleKickerSolenoidForward();
                this.kickerSolenoidToggled = true;
            } else {
                if (this.pneumaticSubsystem.getKickerSolenoid().get() == DoubleSolenoid.Value.kForward) {
                    kickerSolenoidInitialized = true;
                    this.pneumaticSubsystem.toggleKickerSolenoidOff();
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
    
    protected void end() {
        logger.info("Pneumatics end method called");
        this.initialized = 
            this.driveSolenoidInitialized = this.driveSolenoidToggled = 
            this.gripperSolenoidInitialized = this.gripperSolenoidToggled = 
            this.kickerSolenoidInitialized = this.kickerSolenoidToggled = false;
        
    }
    
}
