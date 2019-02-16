package frc.team6027.robot.controls;

import frc.team6027.robot.RobotConfigConstants;

import edu.wpi.first.wpilibj.XboxController;


public class XboxJoystick extends XboxController {
    
    public XboxJoystick() {
        this(RobotConfigConstants.JOYSTICK1_PORT_NUMBER);
    }
    
    public XboxJoystick(int port) {
        super(port);
    }

    public boolean isLeftBumperClicked() {
        return this.getBumper(Hand.kLeft);
    }

    public int getLeftBumperButtonNumber() {
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

    public int getBButtonNumber() {
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
        // Xbox controller outputs negative values for movement up and positive values for movement down,
        // but we want postive values for movement up which controls forward speed of the robot.  Therefore,
        // we invert the value.
        return RobotConfigConstants.OPTIONAL_LEFT_JOYSTICK_INVERSION * this.getRawAxis(RobotConfigConstants.LEFT_ANALOG_STICK);
    }
    
    public double getRightAxis() {
        // Xbox controller outputs negative values for movement left and positive values for movement right,
        // but arcade drive seems to expect the opposite.  I.e., turning left is positive, like you would have on the
        // unit circle.
        return RobotConfigConstants.OPTIONAL_RIGHT_JOYSTICK_INVERSION *this.getRawAxis(RobotConfigConstants.RIGHT_ANALOG_STICK);
    }
}
