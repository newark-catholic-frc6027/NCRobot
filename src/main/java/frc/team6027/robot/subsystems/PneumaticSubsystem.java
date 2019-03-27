package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PneumaticSubsystem extends Subsystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private OperatorDisplay operatorDisplay;

    private StatefulSolenoid driveSolenoid;
    private StatefulSolenoid hatchSolenoid;

    private PneumaticsInitializationCommand pneumaticsInitializationCommand = null;

    public PneumaticSubsystem(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;

        this.driveSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);

        this.hatchSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_2_PORT_A, RobotConfigConstants.SOLENOID_2_PORT_B);
    }

    @Override
    public void initDefaultCommand() {
    }

    /**
     * When the run method of the scheduler is called this method will be
     * called.
     */
    @Override
    public void periodic() {
    }

    public StatefulSolenoid getDriveSolenoid() {
        return driveSolenoid;
    }

    public StatefulSolenoid getHatchSolenoid() {
        return hatchSolenoid;
    }

    public void toggleDriveSolenoid() {
        if (this.driveSolenoid.getState() == null) {
            logger.warn("Don't know drive solenoid state yet, can't toggle drive solenoid");
            return;
        }

        this.operatorDisplay.setFieldValue("Drive Sol. State", this.driveSolenoid.getState().name());

        if (this.driveSolenoid.getState() == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleSolenoidForward");
            toggleDriveSolenoidForward();
        } else {
            logger.trace("Calling toggleSolenoidReverse");
            toggleDriveSolenoidReverse();
        }
    }

    public void toggleHatchSolenoidIn() {
        logger.trace("Running toggleHatchSolenoidIn");
        this.hatchSolenoid.toggleForward();
        this.operatorDisplay.setFieldValue("Hatch", "IN");
    }

    public void toggleHatchSolenoidOut() {
        logger.trace("Running toggleHatchSolenoidOut");
        this.hatchSolenoid.toggleReverse();
        this.operatorDisplay.setFieldValue("Hatch", "OUT");
    }

    public void toggleHatchSolenoidOff() {
        logger.trace("Running toggleHatchSolenoidOff");
        this.hatchSolenoid.toggleOff();
    }

    public void toggleHatchSolenoid() {
        if (this.hatchSolenoid.getState() == null) {
            logger.warn("Don't know hatch solenoid state yet, can't toggle hatch solenoid");
            return;
        }

        this.operatorDisplay.setFieldValue("Hatch Sol. State", this.hatchSolenoid.getState().name());

        if (this.hatchSolenoid.getState() == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleHatchSolenoidIn");
            toggleHatchSolenoidIn();
        } else {
            logger.trace("Calling toggleHatchSolenoidOut");
            toggleHatchSolenoidOut();
        }
    }

    public void toggleDriveSolenoidReverse() {
        logger.trace("Running toggleDriveSolenoidReverse");
        this.driveSolenoid.toggleReverse();
        this.operatorDisplay.setFieldValue("Speed", "HIGH");
    }

    public void toggleDriveSolenoidForward() {
        logger.trace("Running toggleDriveSolenoidForward");
        this.driveSolenoid.toggleForward();
        this.operatorDisplay.setFieldValue("Speed", "LOW");
    }

    public void toggleDriveSolenoidOff() {
        logger.trace("Running toggleDriveSolenoidOff");
        this.driveSolenoid.toggleOff();
    }

    public boolean isReset() {
        return this.pneumaticsInitializationCommand != null && !this.pneumaticsInitializationCommand.isRunning()
                && this.pneumaticsInitializationCommand.isCompleted();
    }

    public void reset() {
        if (this.pneumaticsInitializationCommand == null) {
            pneumaticsInitializationCommand = new PneumaticsInitializationCommand(this);
        }

        pneumaticsInitializationCommand.start();
    }

}
