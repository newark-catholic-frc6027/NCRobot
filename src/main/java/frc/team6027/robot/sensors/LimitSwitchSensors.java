package frc.team6027.robot.sensors;

import java.util.HashMap;
import java.util.Map;

import frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitchSensors {

    public enum LimitSwitchId {
        MastTop,      /* 7 */
        MastBottom,   /* 6 */
        MastSlideForward, /* 8 */
        MastSlideBackward /* 9 */
    }

    // Ultrasonic front - 0

    
    
    private DigitalInput mastBottom = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_BOTTOM_DIO_CHANNEL); // bottom a
//    private DigitalInput limitSwitchBottomB = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_BOTTOM_B_CHANNEL); // bottom b
    private DigitalInput mastTop = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_TOP_DIO_CHANNEL);

    private DigitalInput mastForward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_SLIDE_FORWARD_DIO_CHANNEL);
    private DigitalInput mastBackward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_SLIDE_BACKWARD_DIO_CHANNEL);
    
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
                switchTripped = ! this.mastTop.get();
                break;
            case MastBottom:
                switchTripped = ! this.mastBottom.get();
                break;
            case MastSlideForward:
                switchTripped = this.mastForward.get();
                break;
            case MastSlideBackward:
                switchTripped = this.mastBackward.get();
                break;
    
        }
        
        return switchTripped;
    }
    
}
