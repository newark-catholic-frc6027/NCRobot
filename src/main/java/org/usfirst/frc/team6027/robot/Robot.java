
package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.commands.TeleopManager;
import org.usfirst.frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import org.usfirst.frc.team6027.robot.commands.autonomous.NoOpCommand;
import org.usfirst.frc.team6027.robot.commands.autonomous.AutonomousCommandManager.AutonomousPreference;
import org.usfirst.frc.team6027.robot.commands.autonomous.AutonomousCommandManager.DontDoOption;
import org.usfirst.frc.team6027.robot.field.Field;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team6027.robot.subsystems.ElevatorSubsystem;
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
    private ElevatorSubsystem elevatorSubsystem;
    private SensorService sensorService;

    private Field field = new Field();
    private int gameDataPollCount = 0;

    private Preferences prefs = Preferences.getInstance();
    private AutonomousCommandManager autoCommandManager;
    
    private int teleopExecCount = 0;
    private int autoExecCount = 0;
    
    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        outputBanner();

        this.setSensorService(new SensorService());
        this.setOperatorDisplay(new OperatorDisplaySmartDashboardImpl());
        this.setOperatorInterface(new OperatorInterface(this.getOperatorDisplay()));
        this.setDrivetrain(new DrivetrainSubsystem(this.getOperatorInterface()));
        this.setElevatorSubsystem(new ElevatorSubsystem(this.getSensorService().getLimitSwitchSensors()));
        this.setPneumaticSubsystem(new PneumaticSubsystem(this.getOperatorDisplay()));

        // This ensures that the Teleop command is running whenever we are not in
        // autonomous mode
        TeleopManager teleOpCommand = new TeleopManager(this.operatorInterface, this.sensorService,
                this.getDrivetrain(), this.pneumaticSubsystem, this.elevatorSubsystem);
        this.getDrivetrain().setDefaultCommand(teleOpCommand);
        
        
        
        AutonomousCommandManager.initAutoScenarioDisplayValues(this.getOperatorDisplay());
        AutonomousCommandManager.initDontDoOptionDisplayValues(this.getOperatorDisplay());
        
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
        this.getField().clearAssignmentData();
        this.gameDataPollCount = 0;

    }

    @Override
    public void disabledPeriodic() {
        // Query for game data until we get something.
        // Make sure that there is no test data configured on the Driver Station!
        boolean gameDataExists = pollForGameData();
        Scheduler.getInstance().run();
        
        this.getDrivetrain().stopMotor();
        this.getElevatorSubsystem().elevatorStop();

    }

    protected void applyStationPosition() {
        this.getField().setOurStationPosition(
            this.getOperatorDisplay().getSelectedPosition()
        );
    }

    protected boolean pollForGameData() {
        if (! this.getField().hasAssignmentData()) {
            String gameData = DriverStation.getInstance().getGameSpecificMessage();
            if (gameData != null && gameData.length() > 0) {
                this.getField().doFieldAssignments(gameData);
                return true;
            } else {
                this.gameDataPollCount++;
                // Only output every 10 times we poll, don't need to do every time.
                if (this.gameDataPollCount % 10 == 0) {
                    logger.info("No field assignment data received yet");
                }
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void autonomousInit() {
        this.getPneumaticSubsystem().reset();
        
        applyStationPosition();
        String preferredAutoScenario = this.getOperatorDisplay().getSelectedAutoScenario();
        String dontDoOption = this.getOperatorDisplay().getSelectedDontDoOption();

        // Make sure we have the game data, even though we should already have it from disabledPeriodic method
        while (! pollForGameData() && this.gameDataPollCount < 20 ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Interrupted waiting for game data", e);
            }
        }

        this.sensorService.resetAll();
                
        this.autoCommandManager = new AutonomousCommandManager(
                AutonomousPreference.fromDisplayName(preferredAutoScenario), this.getField(), 
                this.getSensorService(), this.getDrivetrain(), this.getPneumaticSubsystem(), this.getElevatorSubsystem(), this.getOperatorDisplay()
        );
        this.autoCommandManager.setDontDoOption(DontDoOption.fromDisplayName(dontDoOption));
        
        this.autonomousCommand = this.autoCommandManager.chooseCommand();
        
        // schedule the autonomous command (example)
        if (autonomousCommand != null && ! NoOpCommand.getInstance().equals(autonomousCommand) ) {
            this.elevatorSubsystem.initialize();
            Scheduler.getInstance().add(autonomousCommand);
            autonomousCommand.start();
        } else {
            logger.warn("No autonomous command to run!");
        }

    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
        this.autoExecCount++;
        Scheduler.getInstance().run();
        if (this.autoExecCount % 17 == 0) {
            this.updateOperatorDisplay();
        }
    }

    @Override
    public void teleopInit() {
        // If elevatorSubsystem is already initialized, this will do nothing
        this.elevatorSubsystem.initialize();
        this.getPneumaticSubsystem().reset();
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
        this.teleopExecCount++;
        Scheduler.getInstance().run();
        
        if (this.teleopExecCount % 17 == 0) {
            this.updateOperatorDisplay();
        }
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

    public ElevatorSubsystem getElevatorSubsystem() {
        return elevatorSubsystem;
    }


    public void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }


    public SensorService getSensorService() {
        return sensorService;
    }

    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
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
        getOperatorDisplay().setFieldValue("Battery Voltage:", RobotController.getBatteryVoltage());
    }
}
