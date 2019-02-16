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
    

    private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    

    private LimitSwitchSensors limitSwitches;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    
    private boolean initialized = false;
    
    public RearLiftSubsystem(LimitSwitchSensors limitSwitches, OperatorDisplay operatorDisplay) {
        this.limitSwitches = limitSwitches;
        this.operatorDisplay = operatorDisplay;
    }
    
    public void initialize() {
        this.initialized = true;
        this.elevatorGearBoxMaster.stopMotor();
    }
    
    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void periodic() {
        if (this.initialized) {
            if (this.isGoingUp()) {
                if ( this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastTop) ) {
                    this.elevatorStop();
                }
            } else if (this.isGoingDown() && this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastBottom)) {
                this.elevatorStop();
            }
            
            //this.operatorDisplay.setFieldValue("Elevator Motor Amps", this.elevatorGearBoxMaster.getOutputCurrent());
        }
    }
    
    public boolean isUpwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingUp() && ! this.isTopLimitSwitchTripped()) {
            double maxMotorAmps = this.prefs.getDouble("elevatorSubystem.maxMotorAmps", 30.0);
            double currentOutputAmps = this.elevatorGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > maxMotorAmps;
            if (exceeded) {
                logger.error("!!!! Elevator UP exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("Elevator currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }
    
    public boolean isGoingUp() {
        return this.elevatorGearBoxMaster.get() < 0.0;
    }
    
    public boolean isGoingDown() {
        return this.elevatorGearBoxMaster.get() > 0.0;
    }
    
    public boolean isTopLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastTop);
    }

    public boolean isBottomLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastBottom);
    }
    
    public void elevatorUp(double power) {
        double adjustedPower = power > 0.0 ? power * -1 : power;

        logger.trace("ElevatorUP power: {}, adjustedPower: {}, topLimitTripped? {}", power, adjustedPower, this.isTopLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isTopLimitSwitchTripped()) {
            logger.trace("Elevator UP, running motor: {}", adjustedPower);
            this.elevatorGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.ELEVATOR_MAX, "NO");
        } else {
            logger.trace("Elevator UP --> stopping");
            this.operatorDisplay.setFieldValue(OperatorDisplay.ELEVATOR_MAX, "YES");
            this.elevatorStop();
        }
    }
    
    public void elevatorDown(double power) {
        double adjustedPower = power > 0.0 ? power : power * -1;
        logger.trace("Elevator DOWN power: {}, adjustedPower: {}, bottomLimitTripped? {}", power, adjustedPower, this.isBottomLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isBottomLimitSwitchTripped()) {
            logger.trace("Elevator DOWN, running motor: {}", adjustedPower);
            this.elevatorGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.ELEVATOR_MIN, "NO");
        } else {
            logger.trace("ElevatorDown --> stopping");
            this.elevatorStop();
            this.operatorDisplay.setFieldValue(OperatorDisplay.ELEVATOR_MIN, "YES");
        }
    }
    
    public void elevatorStop() {
        this.elevatorGearBoxMaster.stopMotor();
    }

    public double getPower() {
        return this.elevatorGearBoxMaster.get();
    }
}
