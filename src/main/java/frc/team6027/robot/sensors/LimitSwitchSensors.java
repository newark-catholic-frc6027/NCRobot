package frc.team6027.robot.sensors;

import java.util.HashMap;
import java.util.Map;

import frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitchSensors {

    public enum LimitSwitchId {
        RearLiftUp,
        RearLiftDown,
        MastTop,
        MastBottom,
        MastForward,
        MastBackward
    }
    
    
    private DigitalInput mastBottom = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_BOTTOM_CHANNEL); // bottom a
//    private DigitalInput limitSwitchBottomB = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_BOTTOM_B_CHANNEL); // bottom b
    private DigitalInput mastTop = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_TOP_CHANNEL);
//    private DigitalInput limitSwitchTopB = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_TOP_B_CHANNEL); // top b
    private DigitalInput rearLiftUp = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_REAR_LIFT_UP_CHANNEL);
    private DigitalInput rearLiftDown = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_REAR_LIFT_DOWN_CHANNEL);

    private DigitalInput mastForward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_FORWARD_CHANNEL);
    private DigitalInput mastBackward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_BACKWARD_CHANNEL);
    
//    private Map<LimitSwitchId, DigitalInput> limitSwitchMap = new HashMap<>();
    
    
    public LimitSwitchSensors() {
    }
/*
    @Deprecated
    public boolean isLimitSwitchTripped(LimitSwitchId id) {
        boolean switchTripped = true;
        
        if (id == LimitSwitchId.MastTop) {
            switchTripped = this.limitSwitchTopA.get();
        } else if (id == LimitSwitchId.MastBottom) {
            switchTripped = this.limitSwitchBottomA.get();
        }
        
        return switchTripped;
    }
  */  
    /**
     * TODO: Once all limit switches are installed, use this code
     * @param id
     * @return
     */
    public boolean isLimitSwitchTripped(LimitSwitchId id) {
        boolean switchTripped = true;
        switch (id) {
            case MastTop:
                switchTripped = this.mastTop.get();
                break;
            case MastBottom:
                switchTripped = this.mastBottom.get();
                break;
            case RearLiftDown:
                switchTripped = this.rearLiftDown.get();
                break;
            case RearLiftUp:
                switchTripped = this.rearLiftUp.get();
                break;
            case MastForward:
                switchTripped = this.mastForward.get();
                break;
            case MastBackward:
                switchTripped = this.mastBackward.get();
                break;
    
        }
        
        return switchTripped;
    }
    
}
