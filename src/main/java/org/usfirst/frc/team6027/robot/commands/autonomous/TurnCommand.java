package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class TurnCommand extends Command implements PIDOutput {
    public static final String NAME = "Turn";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.005;
	protected static final double PID_INTEGRAL_COEFFICIENT = 0.00;
	protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
	protected static final double PID_FEED_FORWARD_TERM = 0.5;
	/* This tuning parameter indicates how close to "on target" the */
	/* PID Controller will attempt to get. */
	protected static final double PID_TOLERANCE_DEGREES = 2.0;
	protected static final double DRIVE_POWER = 0.7;
	
	private Preferences prefs = Preferences.getInstance();
	private long executionStartThreshold = this.prefs.getLong("turnCommand.execStartThreshold", 500);
	private double pidAngleStopThreshold = this.prefs.getDouble("turnCommand.pidAngleStopThreshold", 0.1);
	private PIDController pidController;

	private SensorService sensorService;
	private PIDCapableGyro gyro;

	private DrivetrainSubsystem drivetrain;
	private double targetAngle;

	private double pidLoopCalculationOutput;
	private OperatorDisplay operatorDisplay;
	private long startTime;
	private double initialGyroAngle;
	private int execCount = 0;

	public TurnCommand(double angle, SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay) {
		requires(drivetrain);
		this.sensorService = sensorService;
		this.gyro = this.sensorService.getGyroSensor();
		this.drivetrain = drivetrain;
		this.targetAngle = angle;
		this.operatorDisplay = operatorDisplay;
//		this.gyro.reset();
		this.initialGyroAngle = this.gyro.getYawAngle();
		this.startTime = System.currentTimeMillis();
        this.setName(NAME);

		initPIDController();
	}

	@Override
	protected void initialize() {
	    logger.info(">>> Turn Command starting, target angle: {}, initial gyro angle", this.targetAngle, this.initialGyroAngle);
	}

	protected void initPIDController() {
		pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
				this.prefs.getDouble("turnCommand.feedForward", PID_FEED_FORWARD_TERM),
				this.sensorService.getGyroSensor().getPIDSource(), this);
		pidController.setInputRange(-180.0, 180.0);
		pidController.setOutputRange(-1* DRIVE_POWER, DRIVE_POWER);
		pidController.setAbsoluteTolerance(PID_TOLERANCE_DEGREES);
		pidController.setContinuous(true);
		pidController.setSetpoint(this.targetAngle); // sets the angle to which we want to turn to
		pidController.enable();
	}

	@Override
	protected boolean isFinished() {
	    
	    if (this.pidController.onTarget()
//        if (Math.abs(this.gyro.getYawAngle() - this.targetAngle) <= 2
                && Math.abs(this.gyro.getRate()) <= pidAngleStopThreshold) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Turn done, angle={}", this.sensorService.getGyroSensor().getYawAngle());
	        
            pidController.disable();
            return true;
	    } else {
	        return false;
	    }
	}

	protected void execute() {
        this.execCount++;
		long currentElapsedExecutionMs = System.currentTimeMillis() - this.startTime;
		/*
		if (currentElapsedExecutionMs < this.executionStartThreshold && this.gyro.getYawAngle() == this.initialGyroAngle) {
			logger.trace("Gyro not reset yet. Skipping");
		} else {
*/
			double pidOutput = this.pidLoopCalculationOutput;
			
			double leftPower = 0;
			double rightPower = 0;
			
		    leftPower = pidOutput;
		    // If we are within 3 degrees of our target and our power has dropped under a minimum threshold
		    // increase power to an adjusted value in order to get the PID loop going again.
            if (Math.abs(this.targetAngle - this.gyro.getYawAngle()) > 3.0 
                    && Math.abs(leftPower) < prefs.getDouble("turnCommand.minPower", .20) ) {
                double adjustedPower = prefs.getDouble("turnCommand.adjustedPower", 0.3);
                leftPower  = leftPower < 0.0 ? -1*adjustedPower : adjustedPower;
                logger.info("Power increased to: {}", leftPower);
            }
		    rightPower = -1 * leftPower;
			    
		    if (this.execCount % 20 == 0) {
                logger.trace("yaw: {}, pid: {}, gyro rate: {}, leftPower: {}, rightPower: {}", 
                        String.format("%.3f",this.gyro.getYawAngle()), 
                        String.format("%.3f",this.pidLoopCalculationOutput), 
                        String.format("%.3f",this.gyro.getRate()),
                        String.format("%.3f", leftPower), 
                        String.format("%.3f", rightPower));
		    }
		    
            this.drivetrain.tankDrive(leftPower, rightPower);           
//		}
	}

	public double getTargetAngle() {
        return targetAngle;
    }

    public void setTargetAngle(double targetAngle) {
        this.targetAngle = targetAngle;
    }

    @Override
	public void pidWrite(double output) {
		this.pidLoopCalculationOutput = output;

	}
}
