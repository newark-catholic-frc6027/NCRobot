
package frc.team6027.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.commands.TeleopManager;
import frc.team6027.robot.commands.autonomous.AutoCommandHelper;
// import frc.team6027.robot.commands.autonomous.AutonomousCommandManager;
import frc.team6027.robot.commands.autonomous.NoOpCommand;
import frc.team6027.robot.commands.autonomous.AutonomousPreference;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubNetworkTableImpl;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.DatahubRobotServerImpl;
import frc.team6027.robot.data.LimelightDataConstants;
import frc.team6027.robot.data.VisionDataConstants;
import frc.team6027.robot.field.Field;
import frc.team6027.robot.field.StationPosition;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import frc.team6027.robot.sensors.UltrasonicSensorManager.UltrasonicSensorKey;
import frc.team6027.robot.server.RobotStatusServer;
import frc.team6027.robot.subsystems.Ballpickup;
import frc.team6027.robot.subsystems.Drive;
//import frc.team6027.robot.subsystems.Elevator;
import frc.team6027.robot.subsystems.Pneumatics;
import frc.team6027.robot.subsystems.Shooter;
import frc.team6027.robot.subsystems.Turret;

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

    private Drive drivetrain;
    private Ballpickup ballpickup;
    private Shooter shooterSubsystem;

    private Pneumatics pneumatics;
    private Turret turret;
    // private ElevatorSubsystem elevatorSubsystem;
    // private ArmSubsystem armSubsystem;

    private SensorService sensorService;

    private Field field = new Field();
    private Preferences prefs = Preferences.getInstance();
    // private AutonomousCommandManager autoCommandManager;

    private int teleopExecCount = 0;
    private int autoExecCount = 0;

    private Datahub limelightData;
    private RobotStatusServer robotStatusServer;
    private TeleopManager teleopManager;

    private OperatingSystemMXBean osbean = null;
    private long totalPhysicalMemorySize;

    public Robot() {
        addShutdownHook();
        initOsBean();
    }


    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        logger.info("******************* ROBOT INIT STARTING *******************");

        outputBanner();
//        this.autoCommandManager = AutonomousCommandManager.instance();

        this.setSensorService(new SensorService());
        this.setOperatorDisplay(new OperatorDisplaySmartDashboardImpl());
        this.setOperatorInterface(new OperatorInterface(this.getOperatorDisplay()));
        this.setDrivetrain(
            new Drive(this.getOperatorInterface(), this.getSensorService())
        );
        this.setBallpickup(new Ballpickup());
        this.setShooterSubsystem(new Shooter());
        this.setTurret(new Turret());

        this.drivetrain.registerMotorEncoders(this.sensorService);
        this.sensorService.addEncoder(EncoderKey.Turret, this.turret.getEncoder());

//        this.setArmSubsystem(new ArmSubsystem(this.getOperatorInterface()));
//        this.setElevatorSubsystem(new ElevatorSubsystem(this.getSensorService().getLimitSwitchSensors(), this.getOperatorDisplay()));
        this.setPneumatics(new Pneumatics(this.getOperatorDisplay()));
        //this.visionData = new DatahubNetworkTableImpl(VisionDataConstants.VISION_DATA_KEY);
        this.limelightData = new DatahubNetworkTableImpl(LimelightDataConstants.LIMELIGHT_DATAHUB_KEY);
        initLimelight();
        DatahubRegistry.instance().register(this.limelightData);
//        DatahubRegistry.instance().register(this.visionData);

        // This ensures that the Teleop command is running whenever we are not in
        // autonomous mode
        this.teleopManager = new TeleopManager(this.operatorInterface, this.sensorService,
                this.getDrivetrain(), this.getBallpickup(), this.getPneumatics(), 
                this.getTurret(), this.getShooterSubsystem(), this.getOperatorDisplay(), this.getField());
        this.getDrivetrain().setDefaultCommand(teleopManager);
/*
        this.autoCommandManager.initialize(
            AutonomousPreference.Rocket,
            this.getField(),
            this.getSensorService(),
            this.getDrivetrain(),
            this.getPneumaticSubsystem(),
            this.getElevatorSubsystem(),
            this.getOperatorDisplay()
        );
*/
/*
        this.autoCommandManager.initOperatorDisplayCommands();
*/
        this.sensorService.resetAll();
//        this.elevatorSubsystem.initialize();

//        this.drivetrain.enableBrakeMode();

        // Start a SocketServer to listen for client ping requests.  This allows us
        // to let other processes (such as vision) know that the robot server is ready
        this.robotStatusServer = new RobotStatusServer();
        this.robotStatusServer.start();

//        AutonomousCommandManager.initAutoScenarioDisplayValues(this.getOperatorDisplay());
//        displayAutoRunning(false);
        this.outputMemoryUsage();
        logger.info("******************* ROBOT INIT COMPLETE *******************");

    }

    private void initLimelight() {
        this.limelightData.put(LimelightDataConstants.LED_MODE_KEY, LimelightDataConstants.LedMode.Blink.value());

    }

    public Ballpickup getBallpickup() {
        return this.ballpickup;
    }

    public void setBallpickup(Ballpickup ballpickup) {
        this.ballpickup = ballpickup;
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    protected void outputBanner() {
        logger.info(">>>>> Newark Catholic Team 6027 Robot started! <<<<<");
        logger.info(" ____          ___               __  __      ____      ");
        logger.info("/\\  _`\\      /'___`\\            /\\ \\/\\ \\    /\\  _`\\    ");
        logger.info("\\ \\ \\L\\ \\   /\\_\\ /\\ \\           \\ \\ `\\\\ \\   \\ \\ \\/\\_\\  ");
        logger.info(" \\ \\ ,  /   \\/_/// /__  _______  \\ \\ , ` \\   \\ \\ \\/_/_ ");
        logger.info("  \\ \\ \\\\ \\     // /_\\ \\/\\______\\  \\ \\ \\`\\ \\   \\ \\ \\L\\ \\");
        logger.info("   \\ \\_\\ \\_\\  /\\______/\\/______/   \\ \\_\\ \\_\\   \\ \\____/");
        logger.info("    \\/_/\\/ /  \\/_____/              \\/_/\\/_/    \\/___/ ");    
    }

    /**
     * This function is called once each time the robot enters Disabled mode. You
     * can use it to reset any subsystem information you want to clear when the
     * robot is disabled.
     */
    @Override
    public void disabledInit() {
        this.outputMemoryUsage();
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
//        this.getElevatorSubsystem().elevatorStop();
//        this.getElevatorSubsystem().mastSlideStop();

    }

    protected StationPosition applyStationPosition() {
        StationPosition pos = StationPosition.fromInt(this.getOperatorDisplay().getSelectedPosition());
        if (pos != null) {
            this.getField().setOurStationPosition(pos);
        } else {
            logger.error("!!!!! No Station Position available, !!!!");
        }
        return pos;
    }

    protected boolean isInMatch() {
        return AutoCommandHelper.isInMatch();
    }
    
    @Override
    public void autonomousInit() {
        logger.info("******************* AUTONOMOUS INIT STARTING *******************");
        this.limelightData.put(LimelightDataConstants.LED_MODE_KEY, LimelightDataConstants.LedMode.On.value());

        // TODO: Reset pneumatics
        // TODO: ensure drivetrain in low gear
//        StationPosition pos = this.applyStationPosition();
        AutonomousPreference autoPref = AutonomousPreference.fromDisplayName(this.getOperatorDisplay().getSelectedAutoScenario());
//        this.autoCommandManager.setPreferredScenario(autoPref);

//        this.drivetrain.enableBrakeMode();

        this.sensorService.resetAll();
/*        
        boolean autoStarted = false;
        if (pos != null) {
            this.autonomousCommand = this.autoCommandManager.chooseCommand();
            if (autonomousCommand != null && ! NoOpCommand.getInstance().equals(autonomousCommand) ) {
                autonomousCommand.start();
                autoStarted = true;
            } else {
                this.autonomousCommand = null;
                logger.warn("No autonomous command to run!");
            }
        } else {
            logger.error("No station position available, can't run Autonomous!");
        }

        displayAutoRunning(autoStarted);
*/        
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
        pneumatics.enableAutomaticCompressorControl();

        logger.info("******************* TELEOP INIT STARTING *******************");
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }

        displayAutoRunning(false);
        outputMemoryUsage();

        // If elevatorSubsystem is already initialized, this will do nothing
//        this.elevatorSubsystem.initialize();
        this.getPneumatics().reset();
    }

    protected void displayAutoRunning(boolean isAutoRunning) {
        getOperatorDisplay().setFieldValue("Auto Running", isAutoRunning);
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

        if (this.teleopExecCount % 250 == 0) {
            // Output roughly every 5 secs
            this.outputMemoryUsage();
        }
    }

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
//        LiveWindow.run();
    }

    public Pneumatics getPneumatics() {
        return pneumatics;
    }

    public void setPneumatics(Pneumatics pneumatics) {
        this.pneumatics = pneumatics;
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

    public Drive getDrivetrain() {
        return drivetrain;
    }

    public void setDrivetrain(Drive drivetrain) {
        this.drivetrain = drivetrain;
    }

    public void setTurret(Turret turret) {
        this.turret = turret;
    }

    public Turret getTurret() {
        return this.turret;
    }

    public Shooter getShooterSubsystem() {
        return shooterSubsystem;
    }

    public void setShooterSubsystem(Shooter shooterSubsystem) {
        this.shooterSubsystem = shooterSubsystem;
    }

/*
    public void setArmSubsystem(ArmSubsystem armSubsystem) {
        this.armSubsystem = armSubsystem;
    }

    public ArmSubsystem getArmSubsystem() {
        return this.armSubsystem;
    }

    public ElevatorSubsystem getElevatorSubsystem() {
        return elevatorSubsystem;
    }


    public void setElevatorSubsystem(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }
*/
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

    public void outputMemoryUsage() {
        if (this.osbean == null) {
            return;
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            logger.info("## JVM memory (b) => max: {}, total: {}, free: {}", runtime.maxMemory(), runtime.totalMemory(), runtime.freeMemory());
            logger.info("## System => free mem (b): {}, system cpu load: {}, system avg load: {}, process cpu load: {}",
                this.osbean.getFreePhysicalMemorySize(), this.osbean.getSystemCpuLoad(), this.osbean.getSystemLoadAverage(), 
                this.osbean.getProcessCpuLoad() );
        } catch (Exception ex) {
            this.logger.debug("Failed to output memory/system stats. Reason: {}", ex.getMessage());
        }
    }
    public void updateOperatorDisplay() {
        try {
            OperatorDisplay disp = this.getOperatorDisplay();
/*
            disp.setFieldValue("Center of Contours", this.visionData.getDouble(VisionDataConstants.CONTOURS_CENTER_X_KEY, -1.0) );
            disp.setFieldValue("# of Contours", this.visionData.getDouble(VisionDataConstants.NUM_CONTOURS_KEY, 0.0));
            disp.setFieldValue("Elevator Dist", this.sensorService.getElevatorHeightInches());
            disp.setFieldValue("Elevator Raw", this.sensorService.getEncoderSensors().getElevatorEncoder().getRaw());
*/
            if (this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft) != null) {
                disp.setFieldValue("Left Motor Dist",
                    this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft).getRelativeDistance());
            }

            if (this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight) != null) {
                disp.setFieldValue("Right Motor Dist",
                    this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorRight).getRelativeDistance());
                disp.setFieldValue("Avg Motor Dist",
                this.sensorService.getEncoderSensors().getAvgEncoderRelativeDistance());
            }

//            disp.setFieldValue("Elev topLimitTripped?", this.elevatorSubsystem.isTopLimitSwitchTripped());
//            disp.setFieldValue("Elev bottomLimitTripped?", this.elevatorSubsystem.isBottomLimitSwitchTripped());
        
                
            disp.setFieldValue("Gyro Angle", this.sensorService.getGyroSensor().getAngle());
            disp.setFieldValue("Gyro Yaw Angle", this.sensorService.getGyroSensor().getYawAngle());

            disp.setFieldValue("Turret position", this.sensorService.getEncoderSensors().getTurretEncoder().getPosition());
            disp.setFieldValue("Shooter RPM", this.shooterSubsystem.getCurrentRPM());
/*            
            Double dist = this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front).getDistanceInches();
*/            
/*
            disp.setFieldValue("Ultrasonic Front Dist", dist != null ? dist + "" : "INFINITY");
            disp.setFieldValue("Level Selection", this.autoCommandManager.getLevelSelection().name());
            disp.setFieldValue("Object Selection", this.autoCommandManager.getObjectSelection().name());
            disp.setFieldValue("Operation Selection", this.autoCommandManager.getOperationSelection().name());
    //        disp.setFieldValue("Drivetrain Mode", this.drivetrain.isBrakeModeEnabled() ? "BRAKE" : "COAST");
*/    
/*    
            Double ultrasonicInches = this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front).getDistanceInches();
            if (ultrasonicInches != null) {
                disp.setFieldValue("In range", 
                    ultrasonicInches >= prefs.getDouble("ultrasonic.inRange.lower", 11.0)
                    &&
                    ultrasonicInches <= prefs.getDouble("ultrasonic.inRange.upper", 13.0)
                );
            }
            disp.setFieldValue("Auto", this.autoCommandManager.isAutoCommandRunning());
*/
        } catch (Exception ex) {
            logger.error("Failure in updateOperatorDisplay. Error: {}, {} ", ex.getClass().getName(), ex.getMessage());
        }
    }

    protected void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new RobotShutdownHook());
    }

    protected void initOsBean() {
        try {
            java.lang.management.OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
            if (bean instanceof OperatingSystemMXBean) {
                this.osbean = (OperatingSystemMXBean) bean;
                this.totalPhysicalMemorySize = this.osbean.getTotalPhysicalMemorySize();
                logger.info("OS Arch: {}, version: {}, avail processors: {}, phys memory (b): {}, free memory (b): {}", 
                    this.osbean.getArch(), this.osbean.getVersion(), this.osbean.getAvailableProcessors(), 
                    this.totalPhysicalMemorySize, this.osbean.getFreePhysicalMemorySize());
            } else {
                logger.warn("Expecting osbean to be instance of  com.sun.management.OperatingSystemMXBean, but is actually instance of {}",
                    bean != null ? bean.getClass().getName() : null);
            }
        } catch (Exception ex) {
            logger.warn("Failed to get OperatingSystemMXBean.  Reason: {}", ex.getMessage());
        }
    }
}
