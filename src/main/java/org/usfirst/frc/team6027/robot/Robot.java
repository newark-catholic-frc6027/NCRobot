
package org.usfirst.frc.team6027.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.commands.StickDriveCommand;
import org.usfirst.frc.team6027.robot.commands.autonomous.AutonomousCrossLine;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.sensors.NavxGyroSensor;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

/**
 * The Virtual Machine is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    @SuppressWarnings("unused")
    //AHRS ahrs;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OperatorInterface operatorInterface;
    private OperatorDisplay operatorDisplay;

    private Command autonomousCommand;
    
    private DrivetrainSubsystem drivetrain;
    private EncoderSensors encoderSensors;
    //private NavxSubsystem navxSubsystem;
    private PIDCapableGyro gyroSensor;

    Preferences prefs = Preferences.getInstance();
    double motorPower;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        //ahrs = new AHRS(SerialPort.Port.kUSB);
        this.gyroSensor = new NavxGyroSensor();
        this.encoderSensors = new EncoderSensors();
    	motorPower = prefs.getDouble("motorPower", 1.0);
        this.setOperatorDisplay(new OperatorDisplaySmartDashboardImpl());
        this.setOperatorInterface(new OperatorInterface(this.getOperatorDisplay()));
        this.setDrivetrain(new DrivetrainSubsystem(this.getOperatorInterface()));
        
        this.getDrivetrain().setDefaultCommand(new StickDriveCommand(this.getDrivetrain(), this.getOperatorInterface()));
        
    }

    /**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
     * the robot is disabled.
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
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString code to get the auto name from the text box below the Gyro
     *
     * You can add additional auto modes by adding additional commands to the
     * chooser code above (like the commented example) or additional comparisons
     * to the switch structure below with additional strings & commands.
     */
    @Override
    public void autonomousInit() {
    	this.autonomousCommand = new AutonomousCrossLine(this.encoderSensors,this.drivetrain, this.operatorDisplay, this.gyroSensor);
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
        Scheduler.getInstance().run();
        updateOperatorDisplay();
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
    public void updateOperatorDisplay(){
        
        
        getOperatorDisplay().setNumericFieldValue("rightEncoder Raw Values", this.encoderSensors.getRightEncoder().getRaw());
        getOperatorDisplay().setNumericFieldValue("rightEncoder Distance", this.encoderSensors.getRightEncoder().getDistance());
        getOperatorDisplay().setNumericFieldValue("leftEncoder Raw Values", this.encoderSensors.getLeftEncoder().getRaw());
        getOperatorDisplay().setNumericFieldValue("leftEncoder Distance", this.encoderSensors.getLeftEncoder().getDistance());
        getOperatorDisplay().setNumericFieldValue("Gyro Angle", this.gyroSensor.getAngle());
        
    }
}
