package org.usfirst.frc.team6027.robot.controls;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.XboxController;


public class XBoxJoystick extends XboxController {
    
    public XBoxJoystick() {
        this(RobotConfigConstants.JOYSTICK_PORT_NUMBER);
    }
    
    public XBoxJoystick(int port) {
        super(port);
    }

    public boolean isLeftBumperClicked() {
        return this.getBumper(Hand.kLeft);
    }

    public int getLeftBumberButtonNumber() {
        return 5;
    }
    
    public boolean isRightBumperClicked() {
        return this.getBumper(Hand.kRight);
    }
    
    public int getRightBumperButtonNumber() {
        return 6;
    }
    
    public boolean isButtonAClicked() {
        return this.getAButton();
    }

    public int getAButtonNumber() {
        return 1;
    }
    
    public boolean isButtonBClicked() {
        return this.getBButton();
    }

    public int geBButtonNumber() {
        return 2;
    }
    
    public boolean isButtonXClicked() {
        return this.getXButton();
    }
    
    public int getXButtonNumber() {
        return 3;
    }
    
    public boolean isButtonYClicked() {
        return this.getYButton();
    }

    public int getYButtonNumber() {
        return 4;
    }
    
    public boolean isBackButtonClicked() {
        return this.getBackButton();
    }

    public int getBackButtonNumber() {
        return 7;
    }
    
    public boolean isStartButtonClicked() {
        return this.getStartButton();
    }
    
    public int getStartButtonNumber() {
        return 8;
    }
    
    public boolean isLeftJoystickClicked() {
        return this.getStickButton(Hand.kLeft);
    }

    public int getLeftJoystickButtonNumber() {
        return 9;
    }
    
    public boolean isRightJoystickClicked() {
        return this.getStickButton(Hand.kRight);
    }
    
    public int getRightJoystickButtonNumber() {
        return 10;
    }
    
    public boolean isLeftTriggerClicked() {
        return this.getTriggerAxis(Hand.kLeft) > 0.0;
    }

    public boolean isRightTriggerClicked() {
        return this.getTriggerAxis(Hand.kRight) > 0.0;
    }
    
    public double getLeftAxis() {
        return this.getRawAxis(RobotConfigConstants.LEFT_ANALOG_STICK);
    }
    
    public double getRightAxis() {
        return this.getRawAxis(RobotConfigConstants.RIGHT_ANALOG_STICK);
    }
}
