package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.sensors.UltrasonicSensor;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class DriveStraightCommand extends Command implements PIDOutput {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.005;
    protected static final double PID_INTEGRAL_COEFFICIENT = 0.000;
    protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double PID_FEED_FORWARD_TERM = 0.3;
    protected static final double PID_TOLERANCE = 1.0;  // degrees
    
    protected static final double DRIVE_POWER = 0.5;

    public enum DriveDistanceMode {
        DistanceReadingOnEncoder,
        DistanceFromObject
    }
    
    protected SensorService sensorService;
    protected EncoderSensors encoderSensors;
    protected UltrasonicSensor ultrasonicSensor;
    protected PIDCapableGyro gyro;
    protected DrivetrainSubsystem drivetrainSubsystem;
    protected OperatorDisplay operatorDisplay;
    private double driveDistance;
    private double currentDistance = 0;
    private DriveDistanceMode driveUntil = DriveDistanceMode.DistanceReadingOnEncoder;
    protected PIDController gyroPidController;
    private Preferences prefs = Preferences.getInstance();
    private double pidLoopCalculationOutput;

    private double drivePower = DRIVE_POWER;

    // private double targetDistance =
    private double diff = 0; // Creating variable for the difference between right
                     // encoder distance and left encoder distance
    private int leftcount = 0;
    private int rightcount = 0;
    private int centercount = 0;

    private double currentAngleHeading = 0.0;
    
    public DriveStraightCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, double driveDistance, DriveDistanceMode driveUntil) {
        requires(drivetrainSubsystem);
        this.sensorService = sensorService;
        this.encoderSensors = this.sensorService.getEncoderSensors();
        this.ultrasonicSensor = this.sensorService.getUltrasonicSensor();
        this.gyro = this.sensorService.getGyroSensor();
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
        this.operatorDisplay = operatorDisplay;
        if (driveUntil != null) {
            this.driveUntil = driveUntil;
        }
        
    }

    @Override
    protected void initialize() {
        this.encoderSensors.reset();
        this.drivePower = this.prefs.getDouble("driveStraightCommand.power", DRIVE_POWER);
        initPIDController();
        
        this.currentAngleHeading =  this.gyro.getYawAngle();
        
        logger.info("DriveStraightCommand target distance: {}", this.driveDistance);
    }
    
    protected void initPIDController() {
        gyroPidController = new PIDController(this.prefs.getDouble("driveStraightCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.feedForward", PID_FEED_FORWARD_TERM),
                this.sensorService.getGyroSensor().getPIDSource(), this);
        
        gyroPidController.setSetpoint(this.currentAngleHeading);
        gyroPidController.setInputRange(-180.0, 180.0);
        gyroPidController.setContinuous(true);
        
        // TODO: change input and output ranges
        gyroPidController.setOutputRange(-1* getDrivePower(), getDrivePower());
        gyroPidController.setAbsoluteTolerance(PID_TOLERANCE);
        gyroPidController.enable();
    }
    
    
    @Override
    protected boolean isFinished() {
        // TODO: use the PIDController to determine when we are done
        if (this.driveUntil == DriveDistanceMode.DistanceReadingOnEncoder) {
            if (   Math.abs(this.encoderSensors.getLeftEncoder().getDistance()) >= this.driveDistance 
                || Math.abs(this.encoderSensors.getRightEncoder().getDistance()) >= this.driveDistance) {
                
                this.drivetrainSubsystem.stopMotor();
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DriveStraight done, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
                return true;
            } else {
                return false;
            }
        } else if (this.driveUntil == DriveDistanceMode.DistanceFromObject) {
            if (this.ultrasonicSensor.getDistanceInches() <= this.driveDistance) {
                this.drivetrainSubsystem.stopArcadeDrive();
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DriveStraight done, distance from object={}", this.ultrasonicSensor.getDistanceInches());
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void execute() {
        logger.trace("Gyro angles (yaw,raw): ({},{}) left-enc: {}, right-enc: {}, pidOutput: {}", 
                String.format("%.3f",this.gyro.getYawAngle()),  
                String.format("%.3f",this.gyro.getAngle()),  
                String.format("%.3f",this.encoderSensors.getLeftEncoder().getDistance()),  
                String.format("%.3f",this.encoderSensors.getRightEncoder().getDistance()),
                String.format("%.3f",this.pidLoopCalculationOutput)
                
        );

        if (this.gyroPidController.onTarget()) {
            this.drivetrainSubsystem.tankDrive(getDrivePower(), getDrivePower());
            logger.trace("onTarget, pidOutput: {}", this.pidLoopCalculationOutput);
        } else {
            double leftPower = getDrivePower() + this.pidLoopCalculationOutput;
            double rightPower = getDrivePower() - this.pidLoopCalculationOutput;
            logger.trace("adjustTo{}, pidOutput: {}, leftPower: {}, rightPower: {}", this.pidLoopCalculationOutput > 0 ? "Right" : "Left", this.pidLoopCalculationOutput, leftPower, rightPower);
            this.drivetrainSubsystem.tankDrive(leftPower, rightPower);
        }
        

    }

    protected double getDrivePower() {
        return this.drivePower;
    }
    
    @Override
    public void pidWrite(double output) {
        this.pidLoopCalculationOutput = output;
        this.operatorDisplay.setFieldValue(OperatorDisplay.PID_LOOP_OUPUT_LABEL, this.pidLoopCalculationOutput);
    }

}
