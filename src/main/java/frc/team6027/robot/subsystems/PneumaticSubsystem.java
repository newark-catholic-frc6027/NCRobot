package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PneumaticSubsystem extends Subsystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private OperatorDisplay operatorDisplay;

    private StatefulSolenoid driveSolenoid;
    private StatefulSolenoid armRotateSolenoid;
    private Solenoid hatchDeliverySolenoid;

    private PneumaticsInitializationCommand pneumaticsInitializationCommand = null;

    public PneumaticSubsystem(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;

        this.driveSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);

        this.armRotateSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
           RobotConfigConstants.SOLENOID_2_PORT_A, RobotConfigConstants.SOLENOID_2_PORT_B);

        this.hatchDeliverySolenoid = new Solenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_3_PORT);

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

    public StatefulSolenoid getArmRotateSolenoid() {
        return armRotateSolenoid;
    }

    public Solenoid getHatchDeliverySolenoid() {
        return hatchDeliverySolenoid;
    }

    public void toggleDriveSolenoid() {
        if (this.driveSolenoid.getState() == null) {
            logger.warn("Don't know drive solenoid state yet, can't toggle drive solenoid");
            return;
        }

        this.operatorDisplay.setFieldValue("Solenoid State", this.driveSolenoid.getState().name());

        if (this.driveSolenoid.getState() == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleSolenoidForward");
            toggleDriveSolenoidForward();
        } else {
            logger.trace("Calling toggleSolenoidReverse");
            toggleDriveSolenoidReverse();
        }
    }

    public void toggleArmRotateSolenoid() {
        if (this.armRotateSolenoid.getState() == null) {
            logger.warn("Don't know arm rotate solenoid state yet, can't toggle arm rotate solenoid");
            return;
        }

        this.operatorDisplay.setFieldValue("Arm Rotate Solenoid State", this.armRotateSolenoid.getState().name());

        if (this.armRotateSolenoid.getState() == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleArmRotateSolenoidRotate-CW");
            toggleArmRotateSolenoidForward();
        } else {
            logger.trace("Calling toggleArmRotateSolenoidRotate-CCW");
            toggleArmRotateSolenoidReverse();
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

    public void toggleArmRotateSolenoidReverse() {
        logger.trace("Running toggleArmRotateSolenoidReverse");
        this.armRotateSolenoid.toggleReverse();
        this.operatorDisplay.setFieldValue("Arm Rotate", "OPENED");
    }

    public void toggleArmRotateSolenoidForward() {
        logger.trace("Running toggleArmRotateSolenoidForward");
        this.armRotateSolenoid.toggleForward();
        this.operatorDisplay.setFieldValue("Arm Rotate", "CLOSED");
    }

    public void toggleArmRotateSolenoidOff() {
        logger.trace("Running toggleArmRotateSolenoidOff");
        this.armRotateSolenoid.toggleOff();
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
