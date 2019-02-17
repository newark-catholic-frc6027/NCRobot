package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.sensors.LimitSwitchSensors;
import frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RearLiftSubsystem extends Subsystem {
    private final Logger logger = LogManager.getLogger(getClass());
    

    private WPI_TalonSRX rearLiftGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.REAR_LIFT_GEARBOX_MINICIM_ID);    

    private LimitSwitchSensors limitSwitches;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    
    private boolean initialized = false;
    protected double maxMotorAmps = 10.0;
    
    public RearLiftSubsystem(LimitSwitchSensors limitSwitches, OperatorDisplay operatorDisplay) {
        this.limitSwitches = limitSwitches;
        this.operatorDisplay = operatorDisplay;
    }
    
    public void initialize() {
        this.initialized = true;
        this.rearLiftGearBoxMaster.stopMotor();
        this.maxMotorAmps = this.prefs.getDouble("rearLiftSubsystem.maxMotorAmps", 10.0);
    }
    
    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void periodic() {
        if (this.initialized) {
            if (this.isGoingUp() && 
                (this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.RearLiftUp) || this.isUpwardMaxAmpsExceeded())) {
                this.stopMotor();
            } else if (this.isGoingDown() && 
                (this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.RearLiftDown) || this.isDownwardMaxAmpsExceeded())) {
                this.stopMotor();
            }
            
            //this.operatorDisplay.setFieldValue("Elevator Motor Amps", this.elevatorGearBoxMaster.getOutputCurrent());
        }
    }
    
    public boolean isUpwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingUp() && ! this.isUpLimitSwitchTripped()) {
            double currentOutputAmps = this.rearLiftGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxMotorAmps;
            if (exceeded) {
                logger.error("!!!! REAR LIFT UP exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("REAR LIFT currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }

    public boolean isDownwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingDown() && ! this.isDownLimitSwitchTripped()) {
            double currentOutputAmps = this.rearLiftGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxMotorAmps;
            if (exceeded) {
                logger.error("!!!! REAR LIFT DOWN exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("REAR LIFT currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }
    
    public boolean isGoingUp() {
        return this.rearLiftGearBoxMaster.get() < 0.0;
    }
    
    public boolean isGoingDown() {
        return this.rearLiftGearBoxMaster.get() > 0.0;
    }
    
    public boolean isUpLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.RearLiftUp);
    }

    public boolean isDownLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastBottom);
    }
    
    public void rearLiftUp(double power) {
        double adjustedPower = power > 0.0 ? power * -1 : power;

        logger.trace("REAR LIFT UP power: {}, adjustedPower: {}, topLimitTripped? {}", power, adjustedPower, this.isUpLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isUpLimitSwitchTripped()) {
            logger.trace("REAR LIFT UP, running motor: {}", adjustedPower);
            this.rearLiftGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.REAR_LIFT_MAX, "NO");
        } else {
            logger.trace("REAR LIFT UP --> stopping");
            this.operatorDisplay.setFieldValue(OperatorDisplay.REAR_LIFT_MAX, "YES");
            this.stopMotor();
        }
    }
    
    public void rearLiftDown(double power) {
        double adjustedPower = power > 0.0 ? power : power * -1;
        logger.trace("REAR LIFT DOWN power: {}, adjustedPower: {}, downLimitTripped? {}", power, adjustedPower, this.isDownLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isDownLimitSwitchTripped()) {
            logger.trace("REAR LIFT DOWN, running motor: {}", adjustedPower);
            this.rearLiftGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.REAR_LIFT_MIN, "NO");
        } else {
            logger.trace("RearLiftDown --> stopping");
            this.stopMotor();
            this.operatorDisplay.setFieldValue(OperatorDisplay.REAR_LIFT_MIN, "YES");
        }
    }
    
    public void stopMotor() {
        this.rearLiftGearBoxMaster.stopMotor();
    }

    public double getPower() {
        return this.rearLiftGearBoxMaster.get();
    }
}
