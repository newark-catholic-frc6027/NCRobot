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
    public final static String NAME = "Charge";
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.005;
    protected static final double PID_INTEGRAL_COEFFICIENT = 0.000;
    protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double PID_FEED_FORWARD_TERM = 0.3;
    protected static final double GYRO_PID_TOLERANCE = 1.0;  // degrees
    protected static final double DISTANCE_PID_TOLERANCE = 1.0;  // inches
    
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
    private DriveDistanceMode driveDistanceMode = DriveDistanceMode.DistanceReadingOnEncoder;
    protected PIDController gyroPidController;
    protected PIDController distancePidController;
    private Preferences prefs = Preferences.getInstance();
    private double gyroPidLoopCalculationOutput;
    private double distPidLoopCalculationOutput;
    protected int execCount = 0;

    private Double drivePower;

    // private double targetDistance =
    private double diff = 0; // Creating variable for the difference between right
                     // encoder distance and left encoder distance
    private int leftcount = 0;
    private int rightcount = 0;
    private int centercount = 0;

    private double currentAngleHeading = 0.0;
    
    public DriveStraightCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, double driveDistance, DriveDistanceMode driveUntil, double drivePower) {
        requires(drivetrainSubsystem);
        this.drivePower = drivePower;
        this.sensorService = sensorService;
        this.encoderSensors = this.sensorService.getEncoderSensors();
        this.ultrasonicSensor = this.sensorService.getUltrasonicSensor();
        this.gyro = this.sensorService.getGyroSensor();
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
        this.operatorDisplay = operatorDisplay;
        if (driveUntil != null) {
            this.driveDistanceMode = driveUntil;
        }

        this.setName(NAME);
    }

 
    @Override
    protected void initialize() {
        this.encoderSensors.reset();
        if (this.drivePower == null) {
        	this.drivePower = this.prefs.getDouble("driveStraightCommand.power", DRIVE_POWER);
        }
        this.currentAngleHeading =  this.gyro.getYawAngle();
        
        initGyroPIDController();
        initDistancePIDController();
        
        logger.info("DriveStraightCommand target distance: {}", this.driveDistance);
    }
    
    protected void initDistancePIDController() {
        this.distancePidController = new PIDController(
                this.prefs.getDouble("driveStraightCommand.dist.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dist.iCoeff", PID_INTEGRAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dist.dCoeff", PID_DERIVATIVE_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dist.feedForward", PID_FEED_FORWARD_TERM),
                this.getDriveDistanceMode() == DriveDistanceMode.DistanceFromObject ?
                        this.sensorService.getUltrasonicSensor() : this.sensorService.getEncoderSensors().getLeftEncoder(),
                new DistancePidOutputHandler()
        );
        
        this.distancePidController.setSetpoint(this.driveDistance);
        this.distancePidController.setInputRange(-50.0 * 12.0, 50.0 * 12.0);
        this.distancePidController.setContinuous(true);
        
        // TODO: change input and output ranges
        this.distancePidController.setOutputRange(-1* getDrivePower(), getDrivePower());
        this.distancePidController.setAbsoluteTolerance(DISTANCE_PID_TOLERANCE);
        this.distancePidController.enable();
        
    }
    
    protected void initGyroPIDController() {
        gyroPidController = new PIDController(
                this.prefs.getDouble("driveStraightCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.feedForward", PID_FEED_FORWARD_TERM),
                this.sensorService.getGyroSensor().getPIDSource(), this);
        
        logger.info("DriveStraightCommand currentAngleHeading: {}", this.currentAngleHeading);
        gyroPidController.setSetpoint(this.currentAngleHeading);
        gyroPidController.setInputRange(-180.0, 180.0);
        gyroPidController.setContinuous(true);
        
        // TODO: change input and output ranges
        gyroPidController.setOutputRange(-1* getDrivePower(), getDrivePower());
        gyroPidController.setAbsoluteTolerance(GYRO_PID_TOLERANCE);
        gyroPidController.enable();
    }
    
    
    @Override
    protected boolean isFinished() {
        // TODO: use the PIDController to determine when we are done
        if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
//            if (   Math.abs(this.encoderSensors.getLeftEncoder().getDistance()) >= this.driveDistance 
//                || Math.abs(this.encoderSensors.getRightEncoder().getDistance()) >= this.driveDistance) {
            if (this.distancePidController.onTarget()) {
                
                this.drivetrainSubsystem.stopMotor();
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DriveStraight done, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
                return true;
            } else {
                return false;
            }
        } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
            double distanceToObject = this.ultrasonicSensor.getDistanceInches();
            // TODO put distance constant in preference
//            if (distanceToObject <= this.driveDistance && !(distanceToObject > 1.94 && distanceToObject < 1.95)  ) {
            if (this.distancePidController.onTarget()) {
                this.drivetrainSubsystem.stopMotor();
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
        this.execCount++;
        if (this.execCount % 20 == 0) {
            logger.trace("Gyro angles (yaw,raw): ({},{}) left-enc: {}, right-enc: {}, ultrasonic dist/valid: {}/{},  pidOutput gyro/dist: {}/{}", 
                    String.format("%.3f",this.gyro.getYawAngle()),  
                    String.format("%.3f",this.gyro.getAngle()),  
                    String.format("%.3f",this.encoderSensors.getLeftEncoder().getDistance()),  
                    String.format("%.3f",this.encoderSensors.getRightEncoder().getDistance()),
                    String.format("%.3f",this.ultrasonicSensor.getDistanceInches()),
                    this.ultrasonicSensor.isRangeValid(),
                    String.format("%.3f",this.gyroPidLoopCalculationOutput),
                    String.format("%.3f",this.distPidLoopCalculationOutput)
            );
        }
        
        if (! this.distancePidController.onTarget()) {
            if (this.gyroPidController.onTarget()) {
                double power = this.distPidLoopCalculationOutput;

                if (Math.abs(power) < prefs.getDouble("driveStraightCommand.minPower", .20) ) {
                    double adjustedPower = prefs.getDouble("driveStraightCommand.adjustedPower", 0.3);
                    power  = power < 0.0 ? -1*adjustedPower : adjustedPower;
                    logger.info("Power increased to: {}", power);                
                }
                
                this.setDrivePower(power);
                this.drivetrainSubsystem.tankDrive(power, power);
                if (this.execCount % 8 == 0) {
                    logger.trace("onTarget, gyroPidOutput: {}, distPidOutput: {}", this.gyroPidLoopCalculationOutput, 
                            this.distPidLoopCalculationOutput);
                }
            } else {
                double leftPower = getDrivePower() + this.gyroPidLoopCalculationOutput;
                double rightPower = getDrivePower() - this.gyroPidLoopCalculationOutput;
                if (this.execCount % 8 == 0) {
                    logger.trace("adjustTo{}, pidOutput: {}, leftPower: {}, rightPower: {}", this.gyroPidLoopCalculationOutput > 0 ? "Right" : "Left", this.gyroPidLoopCalculationOutput, leftPower, rightPower);
                }
                this.drivetrainSubsystem.tankDrive(leftPower, rightPower);
            }
        } else {
            this.drivetrainSubsystem.tankDrive(getDrivePower(), getDrivePower());
        }

    }

    protected void setDrivePower(double power) {
       this.drivePower = power;
    }
    
    protected double getDrivePower() {
        return this.drivePower;
    }
    
    @Override
    public void pidWrite(double output) {
        this.gyroPidLoopCalculationOutput = output;
    }


    public DriveDistanceMode getDriveDistanceMode() {
        return driveDistanceMode;
    }


    public void setDriveDistanceMode(DriveDistanceMode driveDistanceMode) {
        this.driveDistanceMode = driveDistanceMode;
    }

    class DistancePidOutputHandler implements PIDOutput {

        @Override
        public void pidWrite(double output) {
            DriveStraightCommand.this.distPidLoopCalculationOutput = output;
        }
        
    }
}
