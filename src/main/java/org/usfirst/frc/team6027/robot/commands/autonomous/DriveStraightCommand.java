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
    protected static final int LOG_REDUCTION_MOD = 10;
    protected static final int EXEC_LOG_REDUCTION_MOD = 4;
    
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
    protected Double distancePidCutoverPercent = null;
    protected Double distancePidCutoverPoint = null;

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
        this(sensorService, drivetrainSubsystem, operatorDisplay, driveDistance, driveUntil, drivePower, null);
    }

    /**
     * 
     * @param sensorService
     * @param drivetrainSubsystem
     * @param operatorDisplay
     * @param driveDistance
     * @param driveUntil
     * @param drivePower
     * @param distancePidCutoverPercent A number between 0 and 1. After this percentage of distance traveled, the command will stop using the gyroscopic
     *   PID loop output and then use the distance PID loop output. If DriveDistanceMode is DistanceReadingOnEncoder, then
     *   the driveDistance will be an absolute distance to travel (such as 72 inches) and if distancePidCutoverPercent is 90.0, then
     *   the distance PID controller will take over at .90 * 72 = 65 inches.  If DriveDistanceMode is DistanceFromObject, and the
     *   distancePidCutoverPercent is .10, then the distance PID controller will take over when the distance from the object is
     *   at 1.10 * driveDistance.  If driveDistance in this case was -20.0, the distance PID controller would take over at 
     *   1.10 * -20.0 = -22.0.
     */
    public DriveStraightCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, double driveDistance, DriveDistanceMode driveUntil, double drivePower, Double distancePidCutoverPercent) {
    
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
            logger.info("DriveDistanceMode is: {}, target distance is: {}", driveUntil, driveDistance);
        }
        this.distancePidCutoverPercent = distancePidCutoverPercent;
        
        this.setName(NAME);
    }
    
    @Override
    protected void initialize() {
        this.encoderSensors.reset();
        if (this.drivePower == null) {
        	this.drivePower = this.prefs.getDouble("driveStraightCommand.power", DRIVE_POWER);
        }
        this.currentAngleHeading =  this.gyro.getYawAngle();

        if ( this.distancePidCutoverPercent != null) {
            if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                this.distancePidCutoverPoint = (1.0 + this.distancePidCutoverPercent) * this.driveDistance;
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                this.distancePidCutoverPoint = this.distancePidCutoverPercent * this.driveDistance;
            }
            
            logger.info("PID loop control will switch from gryo PID to distance PID at this threshold: {}", this.distancePidCutoverPoint);
        } else {
            logger.info("No distancePidCutoverPercent set, gyro PID will be used for entire distance. Command will finish when this distance is reached: {}", 
                    this.driveDistance);
            
        }
        
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
        if (this.distancePidCutoverPoint == null) {
            // In this case, we aren't using the distance PID to determine when to stop, only using raw sensor readings
            if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                if (   Math.abs(this.encoderSensors.getLeftEncoder().getDistance()) >= this.driveDistance 
                    || Math.abs(this.encoderSensors.getRightEncoder().getDistance()) >= this.driveDistance) {
                    
                    this.drivetrainSubsystem.stopMotor();
                    logger.info(">>>>>>>>>>>>>>>>> NON-PID >>>>>>>>>>>>>>>>>>> DriveStraight done (, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
                    return true;
                } else {
                    return false;
                }
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                double distanceToObject = this.ultrasonicSensor.getDistanceInches();
                // TODO put distance constant in preference
                if (distanceToObject >= this.driveDistance && !(distanceToObject > 1.94 && distanceToObject < 1.95)  ) {
                    this.drivetrainSubsystem.stopMotor();
                    logger.info(">>>>>>>>>>>>>>>>> NON-PID >>>>>>>>>>>>>>>>>>> DriveStraight done, distance from object={}", this.ultrasonicSensor.getDistanceInches());
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
            
        } else {
            // We are using the distance PID to determine when to stop
            double distanceReading = 0.0;
            if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                distanceReading = this.encoderSensors.getLeftEncoder().getDistance();
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                distanceReading =  this.ultrasonicSensor.getDistanceInches();
            } else {
                return true;
            }
            
            if (this.distancePidController.onTarget()) {
                this.drivetrainSubsystem.stopMotor();
                logger.info(">>>>>>>>>>>>>>>>> PID >>>>>>>>>>>>>>>>>>> DriveStraight done, distance={}", distanceReading);
                return true;
            } else {
                return false;
            }

        }
    }

    @Override
    protected void execute() {
        this.execCount++;
        boolean driveWithGyroPID = true;
        if (this.distancePidCutoverPoint != null) {
            if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                double leftEncDistance = this.encoderSensors.getLeftEncoder().getDistance();
                if (leftEncDistance >= this.distancePidCutoverPoint) {
                    logger.info("Distance PID Cutover point reached, switching to driving with distance PID. left-enc dist: {}, cutoverPoint: {}", leftEncDistance, this.distancePidCutoverPoint);
                    driveWithGyroPID = false;
                }
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                double ultrasonicDist = this.ultrasonicSensor.getDistanceInches();
                if (ultrasonicDist >= this.distancePidCutoverPoint) {
                    logger.info("Distance PID Cutover point reached, switching to driving with distance PID. ultrasonic dist: {}, cutoverPoint: {}", ultrasonicDist, this.distancePidCutoverPoint);
                    driveWithGyroPID = false;
                }
            }
        }

        if (this.execCount % LOG_REDUCTION_MOD == 0) {
            logger.trace("Driving with: {} PID, Gyro angles (yaw,raw): ({},{}) left-enc: {}, right-enc: {}, ultrasonic dist: {},  gyro PID Out: {}, dist PID Out: {}", 
                    driveWithGyroPID ? "GYRO" : "DISTANCE",  
                    String.format("%.3f",this.gyro.getYawAngle()),  
                    String.format("%.3f",this.gyro.getAngle()),  
                    String.format("%.3f",this.encoderSensors.getLeftEncoder().getDistance()),  
                    String.format("%.3f",this.encoderSensors.getRightEncoder().getDistance()),
                    String.format("%.3f",this.ultrasonicSensor.getDistanceInches()),
                    String.format("%.3f",this.gyroPidLoopCalculationOutput),
                    String.format("%.3f",this.distPidLoopCalculationOutput)
            );
        }
        
        if (driveWithGyroPID) {
            if (this.gyroPidController.onTarget()) {
                // We are on target with the gyro, just drive
                double power = this.getDrivePower();
                this.drivetrainSubsystem.tankDrive(power, power);
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("On target, driving with GYRO PID, gyroPidOutput: {}, distPidOutput: {}", this.gyroPidLoopCalculationOutput, 
                            this.distPidLoopCalculationOutput);
                }

            } else {
                double leftPower = getDrivePower() + this.gyroPidLoopCalculationOutput;
                double rightPower = getDrivePower() - this.gyroPidLoopCalculationOutput;
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("OFF TARGET, driving with GYRO PID, adjustTo{}, pidOutput: {}, leftPower: {}, rightPower: {}",
                            this.gyroPidLoopCalculationOutput > 0 ? "Right" : "Left", this.gyroPidLoopCalculationOutput, leftPower, rightPower);
                }
                this.drivetrainSubsystem.tankDrive(leftPower, rightPower);
            }
            
        } else {  // Driving with distance PID
            double power = this.distPidLoopCalculationOutput;
            if (Math.abs(power) < prefs.getDouble("driveStraightCommand.minPower", .20) ) {
                double adjustedPower = prefs.getDouble("driveStraightCommand.adjustedPower", 0.3);
                power  = power < 0.0 ? -1*adjustedPower : adjustedPower;
                logger.info("Power increased by DISTANCE PID to: {}", power);                
            }
            
            this.setDrivePower(power);
            this.drivetrainSubsystem.tankDrive(power, power);
            if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                logger.trace("Driving with DISTANCE PID, gyroPidOutput: {}, distPidOutput: {}, power: {}", this.gyroPidLoopCalculationOutput, 
                        this.distPidLoopCalculationOutput, power);
            }
        }
        /*
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
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("onTarget, gyroPidOutput: {}, distPidOutput: {}", this.gyroPidLoopCalculationOutput, 
                            this.distPidLoopCalculationOutput);
                }
            } else {
                double leftPower = getDrivePower() + this.gyroPidLoopCalculationOutput;
                double rightPower = getDrivePower() - this.gyroPidLoopCalculationOutput;
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("adjustTo{}, pidOutput: {}, leftPower: {}, rightPower: {}", this.gyroPidLoopCalculationOutput > 0 ? "Right" : "Left", this.gyroPidLoopCalculationOutput, leftPower, rightPower);
                }
                this.drivetrainSubsystem.tankDrive(leftPower, rightPower);
            }
        } else {
            this.drivetrainSubsystem.tankDrive(getDrivePower(), getDrivePower());
        }
        */

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
