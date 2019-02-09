package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.controls.XboxJoystick;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class OutputAxisCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    XboxJoystick joystick;
    public OutputAxisCommand(XboxJoystick joystick) {
        this.joystick = joystick;
    }
    
    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        return false;
    }

    protected void execute() {
        double leftTriggerValue = this.joystick.getTriggerAxis(Hand.kLeft);
        double rightTriggerValue = this.joystick.getTriggerAxis(Hand.kRight);
        
        logger.trace("Left trigger: {}, right trigger: {}", leftTriggerValue, rightTriggerValue);
        
    }
}
