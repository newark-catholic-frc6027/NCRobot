package org.usfirst.frc.team6027.robot.sensors;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitchSensors {

    public enum LimitSwitchId {
        MastTop,
        MastBottom
    }
    
    private DigitalInput limitSwitch1 = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_1_CHANNEL); // bottom
    private DigitalInput limitSwitch2 = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_2_CHANNEL); // top
    
    private Map<LimitSwitchId, DigitalInput> limitSwitchMap = new HashMap<>();
    
    
    public LimitSwitchSensors() {
        this.limitSwitchMap.put(LimitSwitchId.MastTop, limitSwitch2);
        this.limitSwitchMap.put(LimitSwitchId.MastBottom, limitSwitch1);
    }


    public DigitalInput getLimitSwitch(LimitSwitchId id) {
        return this.limitSwitchMap.get(id);
    }
}
