package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.commands.PneumaticsInitializationCommand;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Pneumatics extends SubsystemBase {
    private final Logger logger = LogManager.getLogger(getClass());

    private OperatorDisplay operatorDisplay;

    private StatefulSolenoid driveSolenoid;
    private StatefulSolenoid ballLatchSolenoid;

    private PneumaticsInitializationCommand pneumaticsInitializationCommand = null;

    public Pneumatics(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;

        this.driveSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_1_PORT_A, RobotConfigConstants.SOLENOID_1_PORT_B);

        this.ballLatchSolenoid = new StatefulSolenoid(RobotConfigConstants.PCM_1_ID_NUMBER,
            RobotConfigConstants.SOLENOID_2_PORT_A, RobotConfigConstants.SOLENOID_2_PORT_B);
    }


    public StatefulSolenoid getDriveSolenoid() {
        return driveSolenoid;
    }

    public StatefulSolenoid getBallLatchSolenoid() {
        return ballLatchSolenoid;
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

    public void toggleBallLatchSolenoidIn() {
        logger.trace("Running toggleBallLatchSolenoidIn");
        this.ballLatchSolenoid.toggleReverse();
        this.operatorDisplay.setFieldValue("Ball Latch", "IN");
    }

    public void toggleBallLatchSolenoidOut() {
        logger.trace("Running toggleBallLatchSolenoidOut");
        this.ballLatchSolenoid.toggleForward();
        this.operatorDisplay.setFieldValue("Ball Latch", "OUT");
    }

    public void toggleBallLatchSolenoidOff() {
        logger.trace("Running toggleBallLatchSolenoidOff");
        this.ballLatchSolenoid.toggleOff();
    }

    public void toggleBallLatchSolenoid() {
        if (this.ballLatchSolenoid.getState() == null) {
            logger.warn("Don't know ball latch solenoid state yet, can't toggle ball latch solenoid");
            return;
        }

        this.operatorDisplay.setFieldValue("Ball Latch Sol. State", this.ballLatchSolenoid.getState().name());

        if (this.ballLatchSolenoid.getState() == DoubleSolenoid.Value.kReverse) {
            logger.trace("Calling toggleBallLatchSolenoidOut");
            toggleBallLatchSolenoidOut();
        } else {
            logger.trace("Calling toggleBallLatchSolenoidIn");
            toggleBallLatchSolenoidIn();
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
    
    public void reset() {
        if (this.pneumaticsInitializationCommand == null) {
            pneumaticsInitializationCommand = new PneumaticsInitializationCommand(this);
        }

        CommandScheduler.getInstance().schedule(pneumaticsInitializationCommand);
    }

}
