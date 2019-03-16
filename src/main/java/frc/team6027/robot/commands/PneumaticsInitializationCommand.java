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
    
    private boolean hatchSolenoidToggled = false;
    private boolean hatchSolenoidInitialized = false;
    
    
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

        if (! hatchSolenoidToggled) {
            logger.trace("Initializing Hatch Solenoid...");
            this.pneumaticSubsystem.toggleHatchSolenoidIn();
            this.hatchSolenoidToggled = true;
        } else {
            if (this.pneumaticSubsystem.getHatchSolenoid().get() == DoubleSolenoid.Value.kForward) {
                hatchSolenoidInitialized = true;
                this.pneumaticSubsystem.toggleHatchSolenoidOff();
                logger.trace("Hatch Solenoid initialized.");
            } else {
                logger.trace("Hatch Solenoid not initialized yet");
            }
        }
        
        this.initialized = driveSolenoidInitialized && hatchSolenoidInitialized;
    }
    
    @Override
    protected boolean isFinished() {
        return this.initialized;
    }
    
    protected void end() {
        logger.info("Pneumatics end method called");
        this.initialized = 
            this.driveSolenoidInitialized = this.driveSolenoidToggled = 
            this.hatchSolenoidInitialized = this.hatchSolenoidToggled = false;
    }
    
}
