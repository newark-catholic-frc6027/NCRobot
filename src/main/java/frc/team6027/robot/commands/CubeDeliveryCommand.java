package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.commands.autonomous.AutoCommandHelper;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Toggles the Kicker cylinder in and out.
 */
public class CubeDeliveryCommand extends Command {
    public enum DeliveryMode {
        KickThenDrop,
        DropThenKick
    };
    
    public static final long DEFAULT_DROPKICK_DELAY_MS = 10;
    /** The delay in milliseconds before we allow the command to finish.  This builds in a small delay to allow the
     * solenoid to finish toggling before we turn it back off. */
    public final static int DELAY_TO_OFF_MS = 1000;
    public final static int DELAY_TO_RETRACT_KICKER_MS = 350;
    
    protected boolean executionComplete = false;
    protected boolean kickerAskedToRetract = false;
    private final Logger logger = LogManager.getLogger(getClass());

    private PneumaticSubsystem pneumaticSubsystem;
    private long timeStarted;
    private long timeGrippersOpened = -1;
    private long timeKicked = -1;
    private long timeKickStarted = -1;
    private DeliveryMode deliveryMode = DeliveryMode.DropThenKick;
    private long dropKickMillisDelay = DEFAULT_DROPKICK_DELAY_MS;
    private Preferences prefs = Preferences.getInstance();
    private boolean inAutonomous = false;
    private Field field;
    private boolean commandAborted = false;

    public CubeDeliveryCommand(DeliveryMode deliveryMode, long dropKickMillisDelay, PneumaticSubsystem pneumaticSubsystem) {
        this(deliveryMode, dropKickMillisDelay, pneumaticSubsystem, null, false);
    }

    public CubeDeliveryCommand(DeliveryMode deliveryMode, long dropKickMillisDelay, PneumaticSubsystem pneumaticSubsystem, Field field) {
        this(deliveryMode, dropKickMillisDelay, pneumaticSubsystem, field, false);
    }
    
    public CubeDeliveryCommand(DeliveryMode deliveryMode, long dropKickMillisDelay, PneumaticSubsystem pneumaticSubsystem, Field field, boolean inAutonomous) {
        requires(pneumaticSubsystem);
        this.pneumaticSubsystem = pneumaticSubsystem;
        this.deliveryMode = deliveryMode;
        this.dropKickMillisDelay = dropKickMillisDelay;
        this.field = field;
        this.inAutonomous = inAutonomous;
    }

    @Override
    protected void initialize() {
        logger.trace("CubeDeliveryCommand deliveryMode: {}, dropKickMillsDelay: {}", this.deliveryMode, this.dropKickMillisDelay);
        this.setTimeout(DELAY_TO_OFF_MS);
        this.timeGrippersOpened = -1;
        this.timeKicked = -1;
        this.timeKickStarted = -1;
    }
    
    
    @Override 
    public void execute() {
        if (this.inAutonomous && AutoCommandHelper.hasFieldDataChangedSinceAutoStart(this.field)) {
            logger.error("Won't execute CubeDelivery command because field game data has changed!");
            this.commandAborted = true;
        }
        
        if (! this.commandAborted) {
            if (this.deliveryMode == DeliveryMode.DropThenKick) {
                executeDropThenKick();
            } else if (this.deliveryMode == DeliveryMode.KickThenDrop) {
                executeKickThenDrop();
            }
        }
    }
    
    
    private void executeKickThenDrop() {
        if (! executionComplete) {
            if (this.timeKicked < 0) {
                timeStarted = System.currentTimeMillis();
                logger.trace("Running CubeKickerCommand - KICK THEN DROP");
                this.pneumaticSubsystem.toggleKickerSolenoid();
                this.timeKicked = System.currentTimeMillis();
                this.executionComplete = false;
            } else {
                long timeElapsedSinceKicked = System.currentTimeMillis() - this.timeKicked;
                if (timeElapsedSinceKicked >= this.dropKickMillisDelay) {
                    this.pneumaticSubsystem.toggleGripperSolenoid();
                    
                    if (this.pneumaticSubsystem.isKickerOut()) {
                        this.pneumaticSubsystem.toggleKickerSolenoid();
                        this.executionComplete = true;
                    }
                }
            }
        }
        
    }

    protected void executeDropThenKick() {
        if (! executionComplete) {
            if (this.timeGrippersOpened < 0) {
                timeStarted = System.currentTimeMillis();
                logger.trace("Running CubeKickerCommand - DROP THEN KICK");
                this.pneumaticSubsystem.toggleGripperSolenoid();
                this.timeGrippersOpened = System.currentTimeMillis();
                this.executionComplete = false;
            } else {
                long timeElapsedSinceGrippersOpened = System.currentTimeMillis() - this.timeGrippersOpened;
                if (timeElapsedSinceGrippersOpened >= this.dropKickMillisDelay) {
                    
                    if (this.timeKicked < 0) {
                        this.timeKicked = System.currentTimeMillis();
                        this.pneumaticSubsystem.toggleKickerSolenoid();
                    } else {
                        long timeElapsedSinceKicked = System.currentTimeMillis() - this.timeKicked;
                        if (timeElapsedSinceKicked >= DELAY_TO_RETRACT_KICKER_MS) {
                            this.pneumaticSubsystem.toggleKickerSolenoid();
                            this.executionComplete = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void interrupted() {
        super.interrupted();
        
        logger.warn("!!!!!!!!!! CubeDeliveryCommand interrupted!!");
    }
    
    @Override
    protected boolean isFinished() {
        if (this.commandAborted) {
            logger.trace("CubeKickerCommand finished due to command abort");
            return true;
        }
        
        long timeElapsedMs = System.currentTimeMillis() - this.timeStarted;
        if (this.executionComplete && ! this.pneumaticSubsystem.isKickerOut() && timeElapsedMs >= DELAY_TO_OFF_MS) {
            logger.trace("CubeKickerCommand finished");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void end() {
        // Reset our state for when we run again
        this.executionComplete = false;
        this.kickerAskedToRetract = false;
        this.pneumaticSubsystem.toggleKickerSolenoidOff();
        this.pneumaticSubsystem.toggleGripperSolenoidOff();
    }

}