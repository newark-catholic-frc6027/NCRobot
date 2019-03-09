package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.sensors.EncoderSensors;
import frc.team6027.robot.sensors.MotorEncoder;
import frc.team6027.robot.sensors.MotorPIDController;
import frc.team6027.robot.sensors.PIDCapableGyro;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.UltrasonicSensor;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import frc.team6027.robot.sensors.UltrasonicSensorManager.UltrasonicSensorKey;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.DrivetrainSubsystem.MotorKey;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDInterface;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class DriveStraightCommand extends Command implements PIDOutput {
    public final static String NAME = "Drive Straight";
    
    private final Logger logger = LogManager.getLogger(getClass());
    protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.005;
    protected static final double PID_INTEGRAL_COEFFICIENT = 0.00;
    protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double PID_FEED_FORWARD_TERM = 0.5;

    protected static final double GYRO_PID_TOLERANCE = 1.0;  // degrees
    protected static final double GYRO_PID_PROPORTIONAL_COEFFICIENT = .01;
    protected static final double GYRO_PID_INTEGRAL_COEFFICIENT = 0.0;
    protected static final double GYRO_PID_DERIVATIVE_COEFFICIENT = 0.0;
    protected static final double GYRO_PID_FEED_FORWARD_TERM = 0.0;

    protected static final double DISTANCE_PID_TOLERANCE = 1.0;  // inches
    protected static final int LOG_REDUCTION_MOD = 10;
    protected static final int EXEC_LOG_REDUCTION_MOD = 4;

    protected static final double DRIVE_POWER = 0.7;

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
    private Double driveDistance;
    private double currentDistance = 0;
    private DriveDistanceMode driveDistanceMode = DriveDistanceMode.DistanceReadingOnEncoder;
    protected PIDController gyroPidController;
    protected PIDController distancePidController;
    //protected MotorPIDController distancePidController;
    private Preferences prefs = Preferences.getInstance();
    private double gyroPidLoopCalculationOutput;
    private double distPidLoopCalculationOutput;
    protected int execCount = 0;
    protected Double distancePidCutoverPercent = null;
    protected Double distancePidCutoverPoint = null;
    protected MotorEncoder leftEncoder;
    protected MotorEncoder rightEncoder;

    private Double drivePower;
    private double currentAngleHeading = 0.0;

    private String driveDistancePrefName;
    private String drivePowerPrefName;
    protected boolean isReset = false;

    public DriveStraightCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, Double driveDistance, DriveDistanceMode driveUntil, Double drivePower) {
        this(driveDistance, driveUntil, drivePower, null, sensorService, drivetrainSubsystem, operatorDisplay);
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
    public DriveStraightCommand(Double driveDistance, DriveDistanceMode driveUntil, 
        Double drivePower, Double distancePidCutoverPercent,
        SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
        OperatorDisplay operatorDisplay) {
    
        requires(drivetrainSubsystem);
        if (driveUntil != null) {
            this.driveDistanceMode = driveUntil;
            logger.info("DriveDistanceMode is: {}, target distance is: {}", driveUntil, driveDistance);
        }
        this.distancePidCutoverPercent = distancePidCutoverPercent;
        this.driveDistance = driveDistance;

        this.drivePower = drivePower;
        this.sensorService = sensorService;
        this.encoderSensors = this.sensorService.getEncoderSensors();
        this.leftEncoder = this.encoderSensors.getLeftEncoder();
        this.rightEncoder = this.encoderSensors.getRightEncoder();
        this.ultrasonicSensor = this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front);
        this.gyro = this.sensorService.getGyroSensor();
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.operatorDisplay = operatorDisplay;
        
        this.setName(NAME);
    }
    
    public DriveStraightCommand(String driveDistancePrefName, DriveDistanceMode driveUntil, 
        String drivePowerPrefName, Double distancePidCutoverPercent,
        SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
        OperatorDisplay operatorDisplay) {

        this((Double) null, driveUntil, null, null, sensorService, drivetrainSubsystem, operatorDisplay);
        this.driveDistancePrefName = driveDistancePrefName;
        this.drivePowerPrefName = drivePowerPrefName;
    }


	@Override
	public void cancel() {
        this.isReset = false;
        this.disablePidControllers();
		super.cancel();
	}

	protected void reset() {
        if (isReset) {
            this.logger.info("Not reset since reset has already been run");
            return;
        }
        this.isReset = true;
        this.execCount = 0;
        
        this.encoderSensors.reset();
        if (this.drivePowerPrefName != null) {
            this.drivePower = this.prefs.getDouble(this.drivePowerPrefName, DRIVE_POWER);
            this.logger.info("Drive power set to {} from preference '{}''", this.drivePower, this.drivePowerPrefName);
        } else {
            if (this.drivePower == null) {
                this.drivePower = this.prefs.getDouble("POWER.driveStraightCommand.power", DRIVE_POWER);
                this.logger.info("Drive power set to {} from preference '{}''", this.drivePower, "POWER.driveStraightCommand.power");
            } else {
                this.logger.info("Using power passed into command: {}", this.drivePower);
            }
        }

        if (this.driveDistancePrefName != null) {
            this.driveDistance = this.prefs.getDouble(this.driveDistancePrefName, 0.0);
            this.logger.info("Drive distance set to {} from preference '{}''", this.driveDistance, this.driveDistancePrefName);
        }

        if (this.driveDistance == null) {
            this.logger.error("Drive distance not passed into command or set from preference!");
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
/*
		this.initialGyroAngle = this.gyro.getYawAngle();

		if (this.anglePrefName != null) {
			this.targetAngle = this.prefs.getDouble(this.anglePrefName, 0.0);
		}

		this.turnMinPower = prefs.getDouble("turnCommand.minPower", .20);
		this.adjustedPower = prefs.getDouble("turnCommand.adjustedPower", 0.3);
        initPIDController();
*/        
	}

	@Override
	public void start() {
		logger.info(">>> Drive Straight Command starting, initial gyro angle: {}", this.currentAngleHeading);
		this.reset();
		super.start();
	}

    @Override
    protected void initialize() {
        reset();
    }
    
    protected void initDistancePIDController() {
        double p = this.prefs.getDouble("PID.driveStraightCommand.dist.pCoeff", PID_PROPORTIONAL_COEFFICIENT);
        double i = this.prefs.getDouble("PID.driveStraightCommand.dist.iCoeff", PID_INTEGRAL_COEFFICIENT);
        double d = this.prefs.getDouble("PID.driveStraightCommand.dist.dCoeff", PID_DERIVATIVE_COEFFICIENT);
        double ff = this.prefs.getDouble("PID.driveStraightCommand.dist.feedForward", PID_FEED_FORWARD_TERM);
        double absoluteDist = this.driveDistance;
        if (this.getDriveDistanceMode() == DriveDistanceMode.DistanceReadingOnEncoder) {
            absoluteDist =  this.sensorService.getEncoderSensors().getLeftEncoder().getPosition() + this.driveDistance;
        }

        // PIDSource is either the ultrasonic sensor or the left motor encoder
        PIDSource pidSource = this.getDriveDistanceMode() == DriveDistanceMode.DistanceFromObject ?
            this.sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front) : 
            this.sensorService.getEncoderSensors().getMotorEncoder(EncoderKey.DriveMotorLeft);
        
        this.distancePidController = new PIDController(p, i, d, ff, 
                pidSource,
                new DistancePidOutputHandler()
        );

        // this.distancePidController = this.drivetrainSubsystem.getPIDController(MotorKey.MotorLeft);
        this.distancePidController.setPID(p, i, d);
        this.distancePidController.setSetpoint(absoluteDist);
        this.distancePidController.setInputRange(-50.0 * 12.0, 50.0 * 12.0);
        this.distancePidController.setContinuous(true);
        
        // TODO: change input and output ranges
        this.distancePidController.setOutputRange(-1 * getDrivePower(), getDrivePower());
        this.distancePidController.setAbsoluteTolerance(DISTANCE_PID_TOLERANCE);
        this.distancePidController.enable();
        
    }
    
    protected void initGyroPIDController() {
        gyroPidController = new PIDController(
                this.prefs.getDouble("PID.driveStraightCommand.pCoeff", GYRO_PID_PROPORTIONAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.iCoeff", GYRO_PID_INTEGRAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dCoeff", GYRO_PID_DERIVATIVE_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.feedForward", GYRO_PID_FEED_FORWARD_TERM),
                this.sensorService.getGyroSensor().getPIDSource(), this);
        
        logger.info("DriveStraightCommand currentAngleHeading: {}", this.currentAngleHeading);
        gyroPidController.setSetpoint(this.currentAngleHeading);
        gyroPidController.setInputRange(-180.0, 180.0);
        gyroPidController.setContinuous(true);
        
        // TODO: change input and output ranges
        gyroPidController.setOutputRange(-1 * getDrivePower(), getDrivePower());
        gyroPidController.setAbsoluteTolerance(GYRO_PID_TOLERANCE);
        gyroPidController.enable();
    }
    
    protected void disablePidControllers() {
        if (this.distancePidController != null) {
            this.distancePidController.disable();
        }

        if (this.gyroPidController != null) {
            this.gyroPidController.disable();
        }

    }
    
    @Override
    protected boolean isFinished() {
        boolean finished = false;
        if (this.distancePidCutoverPoint == null) {
            // In this case, we aren't using the distance PID to determine when to stop, only using raw sensor readings
            if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                
                if (this.driveDistance >= 0) {
                    finished = this.leftEncoder.getRelativeDistance() >= this.driveDistance || 
                               this.rightEncoder.getRelativeDistance() >= this.driveDistance; 
                } else {
                    finished = this.leftEncoder.getRelativeDistance() <= this.driveDistance || 
                               this.rightEncoder.getRelativeDistance() <= this.driveDistance; 
                }

                if (finished) {
                    this.drivetrainSubsystem.stopMotor();
                    logger.info(">>>>>>>>>>>>>>>>> NON-PID >>>>>>>>>>>>>>>>>>> DriveStraight done, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
                    finished = true;
                }
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                double distanceToObject = this.ultrasonicSensor.getDistanceInches();
                // TODO put distance constant in preference
                if (distanceToObject <= this.driveDistance && !(distanceToObject > 1.94 && distanceToObject < 1.95)  ) {
                    this.drivetrainSubsystem.stopMotor();
                    this.disablePidControllers();
                    logger.info(">>>>>>>>>>>>>>>>> NON-PID >>>>>>>>>>>>>>>>>>> DriveStraight done, distance from object={}", this.ultrasonicSensor.getDistanceInches());
                    finished = true;
                }
            } else {
                finished = true;
            }
            
        } else {
            // We are using the distance PID to determine when to stop
            double distanceReading = 0.0;
            if (this.driveDistanceMode == DriveDistanceMode.DistanceReadingOnEncoder) {
                distanceReading = this.encoderSensors.getLeftEncoder().getDistance();
            } else if (this.driveDistanceMode == DriveDistanceMode.DistanceFromObject) {
                distanceReading =  this.ultrasonicSensor.getDistanceInches();
            } else {
                finished = true;
            }
            
            if (this.distancePidController.onTarget()) {
                this.drivetrainSubsystem.stopMotor();
                logger.info(">>>>>>>>>>>>>>>>> PID >>>>>>>>>>>>>>>>>>> DriveStraight done, distance={}", distanceReading);
                finished = true;
            }

        }

        if (finished) {
            this.disablePidControllers();
            this.isReset = false;
        }

        return finished;
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
                this.drivetrainSubsystem.tankDrive(this.driveDirection() * power, this.driveDirection() * power);
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("On target, driving with GYRO PID, gyroPidOutput: {}, distPidOutput: {}", this.gyroPidLoopCalculationOutput, 
                            this.distPidLoopCalculationOutput);
                }

            } else {
                double leftPower = (this.driveDirection() * this.getDrivePower() + this.gyroPidLoopCalculationOutput);
                double rightPower = (this.driveDirection() * this.getDrivePower() - this.gyroPidLoopCalculationOutput);
                if (this.execCount % EXEC_LOG_REDUCTION_MOD == 0) {
                    logger.trace("OFF TARGET, driving with GYRO PID, adjustTo{}, pidOutput: {}, leftPower: {}, rightPower: {}",
                            this.gyroPidLoopCalculationOutput > 0 ? "Right" : "Left", this.gyroPidLoopCalculationOutput, leftPower, rightPower);
                }
                this.drivetrainSubsystem.tankDrive(leftPower, rightPower);
            }
            
        } else {  // Driving with distance PID
            double power = this.distPidLoopCalculationOutput;
            if (Math.abs(power) < prefs.getDouble("PID.driveStraightCommand.minPower", .20) ) {
                double adjustedPower = prefs.getDouble("PID.driveStraightCommand.adjustedPower", 0.3);
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
    }

    protected void setDrivePower(double power) {
       this.drivePower = power;
    }

    protected double driveDirection() {
        return this.driveDistance >= 0 ? 1 : -1 ;
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
