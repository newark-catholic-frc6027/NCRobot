package frc.team6027.robot.sensors;


import frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitchSensors {

    public enum LimitSwitchId {
        MastTop,      
        MastBottom,   
        MastSlideForward,
        MastSlideBackward
    }
    
    private DigitalInput mastBottom = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_BOTTOM_DIO_CHANNEL); // bottom a
    private DigitalInput mastTop = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_TOP_DIO_CHANNEL);

    private DigitalInput mastForward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_SLIDE_FORWARD_DIO_CHANNEL);
    private DigitalInput mastBackward = new DigitalInput(RobotConfigConstants.LIMIT_SWITCH_MAST_SLIDE_BACKWARD_DIO_CHANNEL);
    
    
    public LimitSwitchSensors() {
    }

    /**
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
                switchTripped = ! this.mastForward.get();
                break;
            case MastSlideBackward:
                switchTripped = ! this.mastBackward.get();
                break;
    
        }
        
        return switchTripped;
    }
    
}
