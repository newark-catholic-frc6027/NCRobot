package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;

public class PneumaticsInitializationCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private boolean initialized = false;
    
    private boolean driveSolenoidToggled = false;
    private boolean driveSolenoidInitialized = false;
    
    private boolean armRotateSolenoidToggled = false;
    private boolean armRotateSolenoidInitialized = false;
    
    
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
            if (! armRotateSolenoidToggled) {
                logger.trace("Initializing Arm rotate Solenoid...");
                this.pneumaticSubsystem.toggleArmRotateSolenoidForward();
                this.armRotateSolenoidToggled = true;
            } else {
                if (this.pneumaticSubsystem.getArmRotateSolenoid().get() == DoubleSolenoid.Value.kForward) {
                    armRotateSolenoidInitialized = true;
                    this.pneumaticSubsystem.toggleArmRotateSolenoidOff();
                    logger.trace("Arm Rotate Solenoid initialized.");
                } else {
                    logger.trace("Arm Rotate Solenoid not initialized yet");
                }
            }
        }
        
        this.initialized = driveSolenoidInitialized && armRotateSolenoidInitialized;
    }
    
    @Override
    protected boolean isFinished() {
        return this.initialized;
    }
    
    protected void end() {
        logger.info("Pneumatics end method called");
        this.initialized = 
            this.driveSolenoidInitialized = this.driveSolenoidToggled = 
            this.armRotateSolenoidInitialized = this.armRotateSolenoidToggled = false;
    }
    
}
