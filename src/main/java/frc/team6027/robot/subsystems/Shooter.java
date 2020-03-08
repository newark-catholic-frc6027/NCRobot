package frc.team6027.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.IdleMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team6027.robot.RobotConfigConstants;

public class Shooter extends SubsystemBase {
    private final Logger logger = LogManager.getLogger(getClass());
    public final static String SHOOTER_MAX_RPM_KEY = "shooter.maxRPM";

    private CANSparkMax mainMotor = new CANSparkMax(RobotConfigConstants.SHOOTER_MOTOR_CIM_ID, MotorType.kBrushless);
    
    private Preferences prefs = Preferences.getInstance();

    private Double maxRpm = null;

//    private OperatorInterface operatorInterface;

    public Shooter() {
//        this.operatorInterface = operatorInterface;
        this.initialize();
        
    }

    protected void initialize() {
        this.stopMotor();
    }

    public void stop() {
        this.stopMotor();
    }
/*
   public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }
*/
    public void stopMotor() {
        if (mainMotor != null) {
            this.mainMotor.set(0);           
            this.mainMotor.stopMotor();
        }
        this.maxRpm = null;
    }

    public void spin(double power, MotorDirection spinDirection) {
        if (mainMotor != null) {
            logger.debug("Setting motor power to: {}, direction: {}", power, spinDirection);
            this.mainMotor.set(spinDirection == MotorDirection.Reverse ? power : -1 * power);
        }
    }

    public Double getMaxRPM() {
        if (maxRpm == null) {
            this.maxRpm = prefs.getDouble(SHOOTER_MAX_RPM_KEY, 5700);
        }

        return this.maxRpm;
    }

    public boolean isNotRunning() {
        return ! isRunning();
    }

    public boolean isRunning() {
        return getCurrentRPM() != 0.0;
    }

    public Double getCurrentRPM() {
        if (mainMotor != null) {
            return Math.abs(this.mainMotor.getEncoder().getVelocity());
        } else {
            return 0.0;
        }
    }
    public boolean isAtMaxRPM() {
        if (mainMotor != null) {
            return getCurrentRPM() >= getMaxRPM();
        } else {
            return false;
        }
    }

}