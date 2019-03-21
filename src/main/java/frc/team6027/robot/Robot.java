
package frc.team6027.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.commands.TeleopManager;
import frc.team6027.robot.commands.autonomous.AutoCommandHelper;
import frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import frc.team6027.robot.commands.autonomous.NoOpCommand;
import frc.team6027.robot.commands.autonomous.AutonomousPreference;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubNetworkTableImpl;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.DatahubRobotServerImpl;
import frc.team6027.robot.data.VisionDataConstants;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import frc.team6027.robot.sensors.UltrasonicSensorManager.UltrasonicSensorKey;
import frc.team6027.robot.server.RobotStatusServer;
import frc.team6027.robot.subsystems.ArmSubsystem;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import frc.team6027.robot.subsystems.PneumaticSubsystem;

/**
 * The Virtual Machine is configured to automatically run this class, and to
 * call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the
 * package after creating this project, you must also update the manifest file
 * in the resource directory.
 */
public class Robot extends TimedRobot {
    public static final double ROBOT_WIDTH_INCHES = 27.75;
    public static final double ROBOT_LENGTH_INCHES = 32.0;

    private final Logger logger = LogManager.getLogger(getClass());

    private OperatorInterface operatorInterface;
    private OperatorDisplay operatorDisplay;

    private Command autonomousCommand;

    private DrivetrainSubsystem drivetrain;


    private PneumaticSubsystem pneumaticSubsystem;
    private ElevatorSubsystem elevatorSubsystem;
    private ArmSubsystem armSubsystem;
    
    private SensorService sensorService;

    private Field field = new Field();
    private Preferences prefs = Preferences.getInstance();
    private AutonomousCommandManager autoCommandManager;

    private int teleopExecCount = 0;
    private int autoExecCount = 0;

    private Datahub visionData;
    private RobotStatusServer robotStatusServer;
    private TeleopManager teleopManager;
   
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
        this.setDrivetrain(
            new DrivetrainSubsystem(this.getOperatorInterface(), this.getSensorService())
        );
        this.drivetrain.registerMotorEncoders(this.sensorService);

        this.setArmSubsystem(new ArmSubsystem(this.getOperatorInterface()));
        this.setElevatorSubsystem(new ElevatorSubsystem(this.getSensorService().getLimitSwitchSensors(), this.getOperatorDisplay()));
        this.setPneumaticSubsystem(new PneumaticSubsystem(this.getOperatorDisplay()));
        this.visionData = new DatahubNetworkTableImpl(VisionDataConstants.VISION_DATA_KEY);
        // this.visionData = new DatahubRobotServerImpl(VisionDataConstants.VISION_DATA_KEY);
        DatahubRegistry.instance().register(this.visionData);

        // This ensures that the Teleop command is running whenever we are not in
        // autonomous mode
        this.teleopManager = new TeleopManager(this.operatorInterface, this.sensorService,
                this.getDrivetrain(), this.getArmSubsystem(), this.pneumaticSubsystem, 
                this.getElevatorSubsystem(), this.getOperatorDisplay(), this.getField());
        this.getDrivetrain().setDefaultCommand(teleopManager);

        this.autoCommandManager = AutonomousCommandManager.instance();
        this.autoCommandManager.initialize(
            AutonomousPreference.Rocket,
            this.getField(),
            this.getSensorService(),
            this.getDrivetrain(),
            this.getPneumaticSubsystem(),
            this.getElevatorSubsystem(),
            this.getOperatorDisplay()
        );

        this.autoCommandManager.initOperatorDisplayCommands();
        this.sensorService.resetAll();

        // Start a SocketServer to listen for client ping requests.  This allows us
        // to let other processes (such as vision) know that the robot server is ready
        this.robotStatusServer = new RobotStatusServer();
        this.robotStatusServer.start();

        AutonomousCommandManager.initAutoScenarioDisplayValues(this.getOperatorDisplay());
    }


    protected void outputBanner() {
        logger.info(">>>>> Newark Catholic Team 6027 Robot started! <<<<<");
        logger.info("	     ___  ________    ___   _____  ");
        logger.info("	    /   |/_  __/ /   /   | / ___/ ");
        logger.info("	   / /| | / / / /   / /| | |__ | ");
        logger.info("	  / ___ |/ / / /___/ ___ |___/ / ");
        logger.info("	 /_/  |_/_/ /_____/_/  |_/____/   ");
        logger.info("	  "); 
    }

    /**
     * This function is called once each time the robot enters Disabled mode. You
     * can use it to reset any subsystem information you want to clear when the
     * robot is disabled.
     */
    @Override
    public void disabledInit() {
//        this.getField().clearAssignmentData();
//        this.gameDataPollCount = 0;

    }

    @Override
    public void disabledPeriodic() {
        // Query for game data until we get something.
        // Make sure that there is no test data configured on the Driver Station!
        // boolean gameDataExists = pollForGameData();
        Scheduler.getInstance().run();
        
        this.getDrivetrain().stopMotor();
        this.getElevatorSubsystem().elevatorStop();
        this.getElevatorSubsystem().mastSlideStop();

    }

    protected void applyStationPosition() {
        this.getField().setOurStationPosition(
            StationPosition.fromInt(this.getOperatorDisplay().getSelectedPosition())
        );
    }

    protected boolean isInMatch() {
        return AutoCommandHelper.isInMatch();
    }
    
    @Override
    public void autonomousInit() {
        // TODO: Reset pneumatics
        // TODO: ensure drivetrain in low gear
        // TODO: ensure elevator in high gear
        this.applyStationPosition();

        // TODO: LEFT OFF HERE
        this.sensorService.resetAll();
        this.autonomousCommand = this.autoCommandManager.chooseCommand();

        this.autonomousCommand = this.autoCommandManager.chooseCommand();
        if (autonomousCommand != null && ! NoOpCommand.getInstance().equals(autonomousCommand) ) {
//            this.elevatorSubsystem.initialize();
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
        // TODO: what needs reset here?
//        this.getRearLift().initialize();
        // If elevatorSubsystem is already initialized, this will do nothing
        this.elevatorSubsystem.initialize();
        this.getPneumaticSubsystem().reset();

//        this.getOperatorDisplay().setFieldValue(OperatorDisplay.ELEVATOR_MAX, this.getElevatorSubsystem().isTopLimitSwitchTripped() ? "YES" : "NO");
//        this.getOperatorDisplay().setFieldValue(OperatorDisplay.ELEVATOR_MIN, this.getElevatorSubsystem().isBottomLimitSwitchTripped() ? "YES" : "NO");
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

    public void setArmSubsystem(ArmSubsystem armSubsystem) {
        this.armSubsystem = armSubsystem;
    }

    public ArmSubsystem getArmSubsystem() {
        return this.armSubsystem;
    }

/*
    public RearLiftSubsystem getRearLift() {
        return this.rearLiftSubsystem;
    }

    public void setRearLift(RearLiftSubsystem rearLift) {
        this.rearLiftSubsystem = rearLift;
    }
*/
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
        /*
        getOperatorDisplay().setFieldValue("Right Motor Enc Raw",
                this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight).getRelativePosition());
        getOperatorDisplay().setFieldValue("Left Motor Enc Raw",
            this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft).getRelativePosition());
        */

        OperatorDisplay disp = this.getOperatorDisplay();
        disp.setFieldValue("Center of Contours", this.visionData.getDouble(VisionDataConstants.CONTOURS_CENTER_X_KEY, -1.0) );
        disp.setFieldValue("# of Contours", this.visionData.getDouble(VisionDataConstants.NUM_CONTOURS_KEY, 0.0));
        disp.setFieldValue("Elevator Dist", this.sensorService.getElevatorHeightInches());
        disp.setFieldValue("Elevator Raw", this.sensorService.getEncoderSensors().getElevatorEncoder().getRaw());

        disp.setFieldValue("Left Motor Dist",
            this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft).getRelativeDistance());
        disp.setFieldValue("Right Motor Dist",
            this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight).getRelativeDistance());
        disp.setFieldValue("Avg Motor Dist",
            this.sensorService.getEncoderSensors().getAvgEncoderRelativeDistance());
        disp.setFieldValue("Elev topLimitTripped?", this.elevatorSubsystem.isTopLimitSwitchTripped());
        disp.setFieldValue("Elev bottomLimitTripped?", this.elevatorSubsystem.isBottomLimitSwitchTripped());
    
            
        disp.setFieldValue("Gyro Angle", this.sensorService.getGyroSensor().getAngle());
        disp.setFieldValue("Gyro Yaw Angle", this.sensorService.getGyroSensor().getYawAngle());
        
        Double dist = this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front).getDistanceInches();
        disp.setFieldValue("Ultrasonic Front Dist", dist != null ? dist + "" : "INFINITY");
        disp.setFieldValue("Level Selection", this.autoCommandManager.getLevelSelection().name());
        disp.setFieldValue("Object Selection", this.autoCommandManager.getObjectSelection().name());
        disp.setFieldValue("Operation Selection", this.autoCommandManager.getOperationSelection().name());

//        disp.setFieldValue("Ultrasonic RIGHT Dist", this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Right).getDistanceInches());
        /*

        disp.setFieldValue("rightEncoder Distance",
                this.sensorService.getEncoderSensors().getRightEncoder().getDistance());
        disp.setFieldValue("leftEncoder Distance",
                this.sensorService.getEncoderSensors().getLeftEncoder().getDistance());
        disp.setFieldValue("leftEncoder Raw",
                this.sensorService.getEncoderSensors().getLeftEncoder().getRaw());
        disp.setFieldValue("rightEncoder Raw",
                this.sensorService.getEncoderSensors().getRightEncoder().getRaw());
                /*
        disp.setFieldValue("Air Pressure", this.sensorService.getAirPressureSensor().getAirPressurePsi());
        disp.setFieldValue("Ultrasonic Distance (in)", this.sensorService.getUltrasonicSensor().getDistanceInches());
        //disp.setFieldValue("Contour Center", this.contoursCenterXEntry.getDouble(defaultValue));
        //disp.setFieldValue("Battery Voltage:", RobotController.getBatteryVoltage());
        */
    }
}
