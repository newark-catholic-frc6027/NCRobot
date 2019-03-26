package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.sensors.LimitSwitchSensors;
import frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ElevatorSubsystem extends Subsystem {
    private final Logger logger = LogManager.getLogger(getClass());
    
    private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    
    private WPI_TalonSRX mastSlideGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.MAST_SLIDE_GEARBOX_CIM_1_ID);    

    private LimitSwitchSensors limitSwitches;
    private OperatorDisplay operatorDisplay;
    private Preferences prefs = Preferences.getInstance();
    private double maxMotorAmps = 30.0;
    private double maxSlideMotorAmps = 10.0;
    
    private boolean initialized = false;
    
    public ElevatorSubsystem(LimitSwitchSensors limitSwitches, OperatorDisplay operatorDisplay) {
        this.limitSwitches = limitSwitches;
        this.operatorDisplay = operatorDisplay;
    }
    
    public void initialize() {
        this.initialized = true;
        this.maxMotorAmps = this.prefs.getDouble("elevatorSubystem.maxMotorAmps", 30.0);
        this.maxSlideMotorAmps = this.prefs.getDouble("elevatorSubystem.maxSlideMotorAmps", 10.0);

        this.elevatorGearBoxMaster.setNeutralMode(NeutralMode.Brake);
        this.elevatorGearBoxMaster.stopMotor();
        this.mastSlideGearBoxMaster.setNeutralMode(NeutralMode.Brake);
        this.mastSlideGearBoxMaster.stopMotor();

    }
    
    @Override
    protected void initDefaultCommand() {
    }

    /*
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

            if (this.isGoingForward()) {
                if ( this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideForward) ) {
                    this.mastSlideStop();
                }
            } else if (this.isGoingBackward() && this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideBackward)) {
                this.mastSlideStop();
            }
           
            //this.operatorDisplay.setFieldValue("Elevator Motor Amps", this.elevatorGearBoxMaster.getOutputCurrent());
        }
    }
    */
    public boolean isUpwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingUp() && ! this.isTopLimitSwitchTripped()) {
            double currentOutputAmps = this.elevatorGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxMotorAmps;
            if (exceeded) {
                logger.error("!!!! Elevator UP exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("Elevator currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }

    public boolean isDownwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingDown() && ! this.isBottomLimitSwitchTripped()) {
            double currentOutputAmps = this.elevatorGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxMotorAmps;
            if (exceeded) {
                logger.error("!!!! Elevator UP exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("Elevator currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }

    public boolean isForwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingForward() && ! this.isForwardLimitSwitchTripped()) {
            double currentOutputAmps = this.mastSlideGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxSlideMotorAmps;
            if (exceeded) {
                logger.error("!!!! Mast Slide FORWARD exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("Mast Slide currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }

    public boolean isBackwardMaxAmpsExceeded() {
        boolean exceeded = false;
        if (this.isGoingBackward() && ! this.isBackwardLimitSwitchTripped()) {
            double currentOutputAmps = this.mastSlideGearBoxMaster.getOutputCurrent();
            exceeded = currentOutputAmps > this.maxSlideMotorAmps;
            if (exceeded) {
                logger.error("!!!! Mast Slide BACK exceeded maxMotorAmps value of {}, currentOutputAmps: {}", maxMotorAmps, currentOutputAmps);
            } else {
                logger.trace("Mast Slide currentOutputAmps: {}, maxMotorAmps: {}", currentOutputAmps, maxMotorAmps);
            }
        }
        
        return exceeded;
    }

    // TODO: check this
    public boolean isGoingForward() {
        return this.mastSlideGearBoxMaster.get() > 0.0;
    }

    // TODO: check this
    public boolean isGoingBackward() {
        return this.mastSlideGearBoxMaster.get() < 0.0;
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

    public boolean isForwardLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideForward);
    }

    public boolean isBackwardLimitSwitchTripped() {
        return this.limitSwitches.isLimitSwitchTripped(LimitSwitchId.MastSlideBackward);
    }

    public void mastForward(double power) {
        double adjustedPower = power > 0.0 ? power * -1 : power;

        logger.trace("MastForward power: {}, adjustedPower: {}, forwardLimitTripped? {}", power, adjustedPower, this.isForwardLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isForwardLimitSwitchTripped()) {
            logger.trace("Mast FORWARD, running motor: {}", adjustedPower);
            this.mastSlideGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.MAST_FORWARD_MAX, "NO");
        } else {
            logger.trace("Mast FORWARD --> stopping");
            this.operatorDisplay.setFieldValue(OperatorDisplay.MAST_FORWARD_MAX, "YES");
            this.mastSlideStop();
        }
    }
    
    public void mastBackward(double power) {
        double adjustedPower = power > 0.0 ? power : power * -1;
        logger.trace("MastBackward power: {}, adjustedPower: {}, backLimitTripped? {}", power, adjustedPower, this.isBackwardLimitSwitchTripped());
        if (Math.abs(adjustedPower) > .05 && ! this.isBottomLimitSwitchTripped()) {
            logger.trace("Mast BACKWARD, running motor: {}", adjustedPower);
            this.mastSlideGearBoxMaster.set(adjustedPower);
            this.operatorDisplay.setFieldValue(OperatorDisplay.MAST_BACKWARD_MAX, "NO");
        } else {
            logger.trace("Mast BACKWARD --> stopping");
            this.mastSlideStop();
            this.operatorDisplay.setFieldValue(OperatorDisplay.MAST_BACKWARD_MAX, "YES");
        }
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

    public void mastSlideStop() {
        this.mastSlideGearBoxMaster.stopMotor();
    }

    public double getElevatorPower() {
        return this.elevatorGearBoxMaster.get();
    }

    public double getMastSlidePower() {
        return this.mastSlideGearBoxMaster.get();
    }

}
