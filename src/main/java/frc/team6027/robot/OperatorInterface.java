package frc.team6027.robot;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.controls.XboxJoystick;

/**
 * Represents and holds the human interfaces to control the robot. Here we
 * retain and configure the control device such as the joystick used to steer
 * the robot.
 */
public class OperatorInterface {
	@SuppressWarnings("unused")
	private final Logger logger = LogManager.getLogger(getClass());

	private XboxJoystick joystick1 = new XboxJoystick(RobotConfigConstants.JOYSTICK1_PORT_NUMBER);
	private XboxJoystick joystick2 = new XboxJoystick(RobotConfigConstants.JOYSTICK2_PORT_NUMBER);
	private OperatorDisplay operatorDisplay;

	public OperatorInterface(OperatorDisplay operatorDisplay) {
		this.operatorDisplay = operatorDisplay;
	}

	public XboxJoystick getJoystick1() {
		return joystick1;
	}
	public XboxJoystick getJoystick2() {
		return joystick2;                                       
    }

	public void setJoystick1(XboxJoystick joystick1) {
		this.joystick1 = joystick1;
	}
	public void setJoystick2(XboxJoystick joystick2) {
		this.joystick2 = joystick2;
	}
	public OperatorDisplay getOperatorDisplay() {
		return operatorDisplay;
	}

	public void setOperatorDisplay(OperatorDisplay operatorDisplay) {
		this.operatorDisplay = operatorDisplay;
	}

}
