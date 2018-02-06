package org.usfirst.frc.team6027.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.controls.XboxJoystick;

/**
 * Represents and holds the human interfaces to control the robot. Here we
 * retain and configure the control device such as the joystick used to steer
 * the robot.
 */
public class OperatorInterface {
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private XboxJoystick joystick = new XboxJoystick();
	private OperatorDisplay operatorDisplay;

	public OperatorInterface(OperatorDisplay operatorDisplay) {
		this.operatorDisplay = operatorDisplay;
	}

	public XboxJoystick getJoystick() {
		return joystick;
	}

	public void setJoystick(XboxJoystick joystick) {
		this.joystick = joystick;
	}

	public OperatorDisplay getOperatorDisplay() {
		return operatorDisplay;
	}

	public void setOperatorDisplay(OperatorDisplay operatorDisplay) {
		this.operatorDisplay = operatorDisplay;
	}

}
