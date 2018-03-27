package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.OperatorInterface;
import org.usfirst.frc.team6027.robot.commands.CubeDeliveryCommand.DeliveryMode;
import org.usfirst.frc.team6027.robot.commands.DropCarriageCommand.DropFunction;
import org.usfirst.frc.team6027.robot.controls.XboxJoystick;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class TeleopManager extends Command {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final int LOG_REDUCTION_MOD = 10;

	private OperatorInterface operatorInterface;
	private SensorService sensorService;
	private XboxJoystick joystick;
	private DrivetrainSubsystem drivetrain;
	private Preferences prefs = Preferences.getInstance();
	private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;

	private JoystickButton shiftGearButton;
	private JoystickButton leftBumperButton;
	private JoystickButton backButton;
	private JoystickButton aButton;
	private JoystickButton bButton;
	private JoystickButton yButton;
	private JoystickButton xButton;

	private ShiftGearCommand shiftGearCommand;
	private ToggleGrippersCommand toggleGrippersCommand;
	
	protected int execCount = 0;

	public TeleopManager(OperatorInterface operatorInterface, SensorService sensorService,
			DrivetrainSubsystem drivetrain, PneumaticSubsystem pneumaticSubsystem, ElevatorSubsystem elevator) {
		// Identify the subsystems we will be using in this command and this command
		// only
		requires(drivetrain);
		requires(elevator);

		// Hang onto references of the components we will need during teleop
		this.operatorInterface = operatorInterface;
		this.sensorService = sensorService;
		this.joystick = this.operatorInterface.getJoystick();
		this.drivetrain = drivetrain;
		this.pneumaticSubsystem = pneumaticSubsystem;
		this.elevatorSubsystem = elevator;
		// Create the commands we will be using during teleop
		shiftGearCommand = new ShiftGearCommand(this.pneumaticSubsystem);
		toggleGrippersCommand = new ToggleGrippersCommand(this.pneumaticSubsystem);

		// Set up the commands on the Joystick buttons
		initializeJoystick();
	}

	protected void initializeJoystick() {

		this.shiftGearButton = new JoystickButton(this.joystick, this.joystick.getRightBumperButtonNumber());
		shiftGearButton.whenPressed(this.shiftGearCommand);

		this.yButton = new JoystickButton(this.joystick, this.joystick.getYButtonNumber());
		this.yButton.whenPressed(new Command() {
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
		
// for testing
//      DeliveryMode.valueOf(this.prefs.getString("cubeDelivery.mode", DeliveryMode.DropThenKick.toString())), 
//      this.prefs.getInt("cubeDelivery.msdelay", 10),
		
        this.xButton = new JoystickButton(this.joystick, this.joystick.getXButtonNumber());
	    this.xButton.whenPressed(new CubeDeliveryCommand(
	            DeliveryMode.DropThenKick,
	            10,
	            this.pneumaticSubsystem)
	    );

	    this.backButton = new JoystickButton(this.joystick,  this.joystick.getBackButtonNumber());
	    this.backButton.whenPressed(new PrepareForClimbCommand(this.pneumaticSubsystem));
//	    this.backButton.whenPressed(new DropCarriageCommand(DropFunction.DropForDelivery, DriverStation.getInstance(), pneumaticSubsystem, null, false));
		// Add new button assignments here
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
	    this.clearRequirements();
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
	    this.drive();
		this.runElevatorIfRequired();

		// this.logData();
	}

	private void drive() {
        this.drivetrain.doArcadeDrive(this.joystick.getLeftAxis(), this.joystick.getRightAxis());
    }

    protected void logData() {
     
        if (this.execCount % LOG_REDUCTION_MOD == 0) {
            logger.trace("Ultrasonic dist: {}", 
                    String.format("%.3f",this.sensorService.getUltrasonicSensor().getDistanceInches()));
        }
        
    }

    private void runElevatorIfRequired() {
        if (this.joystick.getTriggerAxis(Hand.kLeft) > .05) {
            this.elevatorSubsystem.elevatorDown(this.joystick.getTriggerAxis(Hand.kLeft));
        } else {
            this.elevatorSubsystem.elevatorUp(this.joystick.getTriggerAxis(Hand.kRight));
        }
    }

    public OperatorDisplay getOperatorDisplay() {
		return this.operatorInterface.getOperatorDisplay();
	}


    // TODO: remove after done testing
	class ElevatorCommandGroup extends CommandGroup {
	    public ElevatorCommandGroup(ElevatorCommand ecmd) {
	        super();
            TeleopManager.this.clearRequirements();
            TeleopManager.this.requires(drivetrain);
            this.addSequential(ecmd);
	    }
	    
        public void end() {
            TeleopManager.this.requires(elevatorSubsystem);
        }
	    
	}
}
