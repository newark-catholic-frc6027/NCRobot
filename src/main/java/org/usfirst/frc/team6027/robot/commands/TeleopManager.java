package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.OperatorInterface;
import org.usfirst.frc.team6027.robot.controls.XboxJoystick;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

public class TeleopManager extends Command {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private OperatorInterface operatorInterface;
	private SensorService sensorService;
	private XboxJoystick joystick;
	private DrivetrainSubsystem drivetrain;
	private Preferences prefs = Preferences.getInstance();
	private PneumaticSubsystem pneumaticSubsystem;

	private JoystickButton shiftGearButton;
	private JoystickButton leftBumperButton;
	private JoystickButton aButton;
	private JoystickButton bButton;

	private ShiftGearCommand shiftGearCommand;
	private ToggleGrippersCommand toggleGrippersCommand;
	private CubeKickerCommand toggleKickerCommand;
	protected int execCount = 0;

	public TeleopManager(OperatorInterface operatorInterface, SensorService sensorService,
			DrivetrainSubsystem drivetrain, PneumaticSubsystem pneumaticSubsystem) {
		// Identify the subsystems we will be using in this command and this command
		// only
		requires(drivetrain);

		// Hang onto references of the components we will need during teleop
		this.operatorInterface = operatorInterface;
		this.sensorService = sensorService;
		this.joystick = this.operatorInterface.getJoystick();
		this.drivetrain = drivetrain;
		this.pneumaticSubsystem = pneumaticSubsystem;

		// Create the commands we will be using during teleop
		shiftGearCommand = new ShiftGearCommand(this.pneumaticSubsystem);
		toggleGrippersCommand = new ToggleGrippersCommand(this.pneumaticSubsystem);
		toggleKickerCommand = new CubeKickerCommand(this.pneumaticSubsystem);
		
		// Set up the commands on the Joystick buttons
		initializeJoystick();
	}

	protected void initializeJoystick() {

		this.shiftGearButton = new JoystickButton(this.joystick, this.joystick.getRightBumperButtonNumber());
		shiftGearButton.whenPressed(this.shiftGearCommand);

		this.leftBumperButton = new JoystickButton(this.joystick, this.joystick.getLeftBumperButtonNumber());
		this.leftBumperButton.whenPressed(new Command() {
            @Override
            protected boolean isFinished() {
                return true;
            }
		    
            protected void execute() {
                sensorService.getGyroSensor().reset(); 
                sensorService.getEncoderSensors().reset();
            }
		});
		
		this.aButton = new JoystickButton(this.joystick, this.joystick.getAButtonNumber());
		this.aButton.whenPressed(this.toggleGrippersCommand);
		
		this.bButton = new JoystickButton(this.joystick, this.joystick.getBButtonNumber());
		this.bButton.whenPressed(this.toggleKickerCommand);
		// Add new button assignments here
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		// This will only get called if isFinished returns true
		// this.drivetrain.stopArcadeDrive();
	}

	@Override
	protected void interrupted() {
		logger.info("Teleop interrupted");
	}

	@Override
	protected void execute() {
	    this.execCount++;
	    //	       this.drivetrain.tankDrive(this.operatorInterface.getJoystick().getLeftAxis(), this.operatorInterface.getJoystick().getRightAxis());

//	    logger.trace("leftaxis: {}, rightaxis: {}", this.joystick.getLeftAxis(), this.joystick.getRightAxis() );
		this.drivetrain.doArcadeDrive(this.joystick.getLeftAxis(), this.joystick.getRightAxis());
		
		
		if (this.execCount % 20 == 0) {
    		logger.trace("Ultrasonic dist/valid: {}/{}", 
    		        String.format("%.3f",this.sensorService.getUltrasonicSensor().getDistanceInches()), 
    		        this.sensorService.getUltrasonicSensor().isRangeValid());
		}
	}

	public OperatorDisplay getOperatorDisplay() {
		return this.operatorInterface.getOperatorDisplay();
	}


}
