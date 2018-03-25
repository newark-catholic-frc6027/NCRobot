package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class ElevatorSubsystem extends Subsystem {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    

    private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    

    private LimitSwitchSensors limitSwitches;
    private OperatorDisplay operatorDisplay;
    
    private boolean initialized = false;
    
    public ElevatorSubsystem(LimitSwitchSensors limitSwitches, OperatorDisplay operatorDisplay) {
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
            if (this.isGoingUp() && this.limitSwitches.getLimitSwitch(LimitSwitchId.MastTop).get()) {
                this.elevatorStop();
            } else if (this.isGoingDown() && this.limitSwitches.getLimitSwitch(LimitSwitchId.MastBottom).get()) {
                this.elevatorStop();
            }
        }
    }
    
    public boolean isGoingUp() {
        return this.elevatorGearBoxMaster.get() < 0.0;
    }
    
    public boolean isGoingDown() {
        return this.elevatorGearBoxMaster.get() > 0.0;
    }
    
    public boolean isTopLimitSwitchTripped() {
        return this.limitSwitches.getLimitSwitch(LimitSwitchId.MastTop).get();
    }

    public boolean isBottomLimitSwitchTripped() {
        return this.limitSwitches.getLimitSwitch(LimitSwitchId.MastBottom).get();
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
