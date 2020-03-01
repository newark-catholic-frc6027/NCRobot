package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.subsystems.Pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class PneumaticsInitializationCommand extends CommandBase {
    private final Logger logger = LogManager.getLogger(getClass());

    private boolean initialized = false;
    
    private boolean driveSolenoidToggled = false;
    private boolean driveSolenoidInitialized = false;
    
    private boolean ballLatchSolenoidToggled = false;
    private boolean ballLatchSolenoidInitialized = false;
    
    
    private Pneumatics pneumatics;
    public PneumaticsInitializationCommand(Pneumatics subsys) {
        this.pneumatics = subsys;
        this.addRequirements(subsys);
    }
            
    @Override
    public void initialize() {
    }
    
    @Override
    public void execute() {
        if (this.initialized) {
            logger.info("Pneumatics already initialized, nothing to do.");
            return;
        }
        
        
        if (! driveSolenoidToggled) {
            logger.trace("Initializing Drive Solenoid...");
            this.pneumatics.toggleDriveSolenoidForward();
            this.driveSolenoidToggled = true;
        } else {
            if (this.pneumatics.getDriveSolenoid().get() == DoubleSolenoid.Value.kForward) {
                driveSolenoidInitialized = true;
                this.pneumatics.toggleDriveSolenoidOff();
                logger.trace("Drive Solenoid initialized.");
            } else {
                logger.trace("Drive Solenoid not initialized yet");
            }
        }
        

        if (! ballLatchSolenoidToggled) {
            logger.trace("Initializing ball latch Solenoid...");
            this.pneumatics.toggleBallLatchSolenoidIn();
            this.ballLatchSolenoidToggled = true;
        } else {
            if (this.pneumatics.getBallLatchSolenoid().get() == DoubleSolenoid.Value.kReverse) {
                ballLatchSolenoidInitialized = true;
                this.pneumatics.toggleBallLatchSolenoidOff();
                logger.trace("Ball latch Solenoid initialized.");
            } else {
                logger.trace("Ball latch Solenoid not initialized yet");
            }
        }
        
        this.initialized = driveSolenoidInitialized && ballLatchSolenoidInitialized;
    }
    
    @Override
    public boolean isFinished() {
        return this.initialized;
    }
    
    protected void end() {
        logger.info("Pneumatics end method called");
        this.initialized = 
            this.driveSolenoidInitialized = this.driveSolenoidToggled = 
            this.ballLatchSolenoidInitialized = this.ballLatchSolenoidToggled = false;
    }
    
}
