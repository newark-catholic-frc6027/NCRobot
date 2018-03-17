package org.usfirst.frc.team6027.robot.subsystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors;
import org.usfirst.frc.team6027.robot.sensors.LimitSwitchSensors.LimitSwitchId;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class ElevatorSubsystem extends Subsystem {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WPI_TalonSRX elevatorGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.ELEVATOR_GEARBOX_CIM_1_ID);    

    private LimitSwitchSensors limitSwitches;
    
    public ElevatorSubsystem(LimitSwitchSensors limitSwitches) {
        this.limitSwitches = limitSwitches;
    }
    
    @Override
    protected void initDefaultCommand() {
    }

    public void periodic() {
        /*
        if ((this.isGoingUp() && this.elevatorGearBoxMaster.get() > 0.0) || (this.isGoingDown() && this.elevatorGearBoxMaster.get() < 0.0)) {
            this.elevatorStop();
            this.logger(">>>> Elevator Emergency Stopped due to movement in the wrong direction!!!!");
        }
        */
        
        if (this.isGoingUp() && this.limitSwitches.getLimitSwitch(LimitSwitchId.MastTop).get()) {
            this.elevatorStop();
        } else if (this.isGoingDown() && this.limitSwitches.getLimitSwitch(LimitSwitchId.MastBottom).get()) {
            this.elevatorStop();
        }
    }
    
    public boolean isGoingUp() {
        return this.elevatorGearBoxMaster.get() < 0.0;
    }
    
    public boolean isGoingDown() {
        return this.elevatorGearBoxMaster.get() > 0.0;
    }
    
    public void elevatorUp(double power) {
        double adjustedPower = power > 0.0 ? power * -1 : power;
        this.elevatorGearBoxMaster.set(adjustedPower);
    }
    
    public void elevatorDown(double power) {
        double adjustedPower = power > 0.0 ? power : power * -1;
        this.elevatorGearBoxMaster.set(adjustedPower);
    }
    
    public void elevatorStop() {
        this.elevatorGearBoxMaster.stopMotor();
    }

    public double getPower() {
        return this.elevatorGearBoxMaster.get();
    }
}
