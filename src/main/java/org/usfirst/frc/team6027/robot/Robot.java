
package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.commands.DriveStraightCommand;
import org.usfirst.frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import org.usfirst.frc.team6027.robot.commands.TeleopManager;
import org.usfirst.frc.team6027.robot.commands.TurnCommand;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.PneumaticSubsystem;

/**
 * The Virtual Machine is configured to automatically run this class, and to
 * call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the
 * package after creating this project, you must also update the manifest file
 * in the resource directory.
 */
public class Robot extends IterativeRobot {
    public static final double ROBOT_WIDTH_INCHES = 27.75;
    public static final double ROBOT_LENGTH_INCHES = 32.0;
    
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private OperatorInterface operatorInterface;
	private OperatorDisplay operatorDisplay;

	private Command autonomousCommand;

	private DrivetrainSubsystem drivetrain;
	private PneumaticSubsystem pneumaticSubsystem;
	private SensorService sensorService;
	
	private Field field = new Field();

	Preferences prefs = Preferences.getInstance();

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
	    outputBanner();

		this.sensorService = new SensorService();
		this.setOperatorDisplay(new OperatorDisplaySmartDashboardImpl());
		this.setOperatorInterface(new OperatorInterface(this.getOperatorDisplay()));
		this.setDrivetrain(new DrivetrainSubsystem(this.getOperatorInterface()));
		this.pneumaticSubsystem = new PneumaticSubsystem(this.getOperatorDisplay());

		// This ensures that the Teleop command is running whenever we are not in
		// autonomous mode
		this.getDrivetrain().setDefaultCommand(new TeleopManager(this.operatorInterface, this.sensorService,
				this.getDrivetrain(), this.pneumaticSubsystem));

	}

	protected void outputBanner() {
	    logger.info(">>>>> Newark Catholic Team 6027 Robot started! <<<<<");
	    logger.info("	     ________.____    ._____________      ___ ___  ");
	    logger.info("	    /  _____/|    |   |__\\__    ___/___  /   |   \\ ");
	    logger.info("	   /   \\  ___|    |   |  | |    |_/ ___\\/    ~    \\");
	    logger.info("	   \\    \\_\\  \\    |___|  | |    |\\  \\___\\    Y    /");
	    logger.info("	    \\______  /_______ \\__| |____| \\___  >\\___|_  / ");
	    logger.info("	           \\/        \\/               \\/       \\/ "); 
    }

    /**
	 * This function is called once each time the robot enters Disabled mode. You
	 * can use it to reset any subsystem information you want to clear when the
	 * robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString code to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons to
	 * the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		//this.autonomousCommand = new AutoLineStraight(this.sensorService, this.drivetrain, this.operatorDisplay);
//		this.autonomousCommand = new DriveStraightCommand(this.sensorService, this.drivetrain, this.operatorDisplay, this.prefs.getDouble("driveStraightCommand.driveDistance", 12.0), DriveDistanceMode.DistanceReadingOnEncoder);
        this.autonomousCommand = new TurnCommand(90.0, this.sensorService, this.drivetrain, this.operatorDisplay);
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		this.field.doFieldAssignments(gameData);
        
		// schedule the autonomous command (example)
		if (autonomousCommand != null) {
			autonomousCommand.start();
		}
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
        String gameData = DriverStation.getInstance().getGameSpecificMessage();
        if (this.field.getAssignmentData() == null) {
            this.field.doFieldAssignments(gameData);
        }
        
		Scheduler.getInstance().run();
		updateOperatorDisplay();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}

	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
	    this.updateOperatorDisplay();
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}

	public OperatorInterface getOperatorInterface() {
		return operatorInterface;
	}

	public void setOperatorInterface(OperatorInterface operatorInterface) {
		this.operatorInterface = operatorInterface;
	}

	public OperatorDisplay getOperatorDisplay() {
		return operatorDisplay;
	}

	public void setOperatorDisplay(OperatorDisplay operatorDisplay) {
		this.operatorDisplay = operatorDisplay;
	}

	public DrivetrainSubsystem getDrivetrain() {
		return drivetrain;
	}

	public void setDrivetrain(DrivetrainSubsystem drivetrain) {
		this.drivetrain = drivetrain;
	}

	public PneumaticSubsystem getPneumaticSubsystem() {
		return pneumaticSubsystem;
	}

	public void setPneumaticSubsystem(PneumaticSubsystem pneumaticSubsystem) {
		this.pneumaticSubsystem = pneumaticSubsystem;
	}

	public void updateOperatorDisplay() {
        getOperatorDisplay().setFieldValue("rightEncoder Raw Values",
                this.sensorService.getEncoderSensors().getRightEncoder().getRaw());
        getOperatorDisplay().setFieldValue("rightEncoder Distance",
                this.sensorService.getEncoderSensors().getRightEncoder().getDistance());
        getOperatorDisplay().setFieldValue("leftEncoder Raw Values",
                this.sensorService.getEncoderSensors().getLeftEncoder().getRaw());
        getOperatorDisplay().setFieldValue("leftEncoder Distance",
                this.sensorService.getEncoderSensors().getLeftEncoder().getDistance());
        getOperatorDisplay().setFieldValue("leftEncoder Raw",
                this.sensorService.getEncoderSensors().getLeftEncoder().getRaw());
        getOperatorDisplay().setFieldValue("rightEncoder Raw",
                this.sensorService.getEncoderSensors().getRightEncoder().getRaw());
        getOperatorDisplay().setFieldValue("Gyro Angle", this.sensorService.getGyroSensor().getAngle());
        getOperatorDisplay().setFieldValue("Gyro Yaw Angle", this.sensorService.getGyroSensor().getYawAngle());
        getOperatorDisplay().setFieldValue("Air Pressure", this.sensorService.getAirPressureSensor().getAirPressurePsi());
        getOperatorDisplay().setFieldValue("Ultrasonic Distance (in)", this.sensorService.getUltrasonicSensor().getDistanceInches());

	}
}
