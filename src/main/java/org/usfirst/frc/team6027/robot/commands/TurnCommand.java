package org.usfirst.frc.team6027.robot.commands;

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
	private final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.08;
	protected static final double PID_INTEGRAL_COEFFICIENT = 0.00;
	protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
	protected static final double PID_FEED_FORWARD_TERM = 0.3;
	/* This tuning parameter indicates how close to "on target" the */
	/* PID Controller will attempt to get. */
	protected static final double PID_TOLERANCE_DEGREES = 2.0;
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

	public TurnCommand(double angle, SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay) {
		requires(drivetrain);
		this.sensorService = sensorService;
		this.gyro = this.sensorService.getGyroSensor();
		this.drivetrain = drivetrain;
		this.targetAngle = angle;
		this.operatorDisplay = operatorDisplay;
		this.gyro.reset();
		this.initialGyroAngle = this.gyro.getYawAngle();
		this.startTime = System.currentTimeMillis();

		initPIDController();
	}

	@Override
	protected void initialize() {
		logger.info("Current Angle, PID Loop Output, Yaw Rate, Right Motor Power");

	}

	protected void initPIDController() {
		// pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff",
		// PROPORTIONAL_COEFFICIENT), INTEGRAL_COEFFICIENT, DERIVATIVE_COEFFICIENT,
		// FEED_FORWARD_TERM, this.gyro.getPIDSource(), this);
		pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
				this.prefs.getDouble("turnCommand.feedForward", PID_FEED_FORWARD_TERM),
				this.sensorService.getGyroSensor().getPIDSource(), this);
		pidController.setInputRange(-180.0, 180.0);
		pidController.setOutputRange(-1.0, 1.0);
		pidController.setAbsoluteTolerance(PID_TOLERANCE_DEGREES);
		pidController.setContinuous(true);
		pidController.setSetpoint(this.targetAngle); // sets the angle to which we want to turn to
		pidController.enable();
	}

	@Override
	protected boolean isFinished() {
	    
	    if (this.pidController.onTarget()) {
            pidController.disable();
            //this.drivetrain.stopMotor();
            this.drivetrain.differentialStopMotor();
            logger.info("Turn done, angle={}", this.sensorService.getGyroSensor().getYawAngle());
            return true;
	    } else {
	        return false;
	    }
	    /*
	    // TODO: see if we can use the onTarget() method of the PID controller here
		if (Math.abs(this.gyro.getYawAngle() - this.targetAngle) <= 0.5
				&& Math.abs(this.gyro.getRate()) <= pidAngleStopThreshold) {
			pidController.disable();
			// this.drivetrain.drive (0, 0);
			this.drivetrain.stopMotor();
			logger.info("Turn done, angle={}", this.sensorService.getGyroSensor().getYawAngle());
			return true;
		}
		return false;
		*/
	}

	protected void execute() {
		// this.drivetrain.drive (0.2, pidLoopCalculationOutput);
		long currentElapsedExecutionMs = System.currentTimeMillis() - this.startTime;
		if (currentElapsedExecutionMs < this.executionStartThreshold && this.gyro.getYawAngle() == this.initialGyroAngle) {
			logger.info("Gyro not reset yet. Skipping");
		} else {
			double pidPower = pidLoopCalculationOutput / this.prefs.getDouble("turnCommand.pidPowerDivisor", 4.0);

			
			logger.info("yaw: {}, pid: {}, gyro rate: {}, pidPower: {}", this.gyro.getYawAngle(), this.pidLoopCalculationOutput, this.gyro.getRate(),
					pidPower);
			
			this.drivetrain.differentialDrive(pidPower, -1 * pidPower);
			/*
			if (this.pidLoopCalculationOutput > 0) {
	            this.drivetrain.tankDrive(-1 * pidPower, 0);
			} else {
	            this.drivetrain.tankDrive(-1 * pidPower, pidPower);
			}
*/
			logger.trace("Current Angle: {}", this.sensorService.getGyroSensor().getYawAngle());

		}
	}

	@Override
	public void pidWrite(double output) {
		this.pidLoopCalculationOutput = output;
		this.operatorDisplay.setFieldValue(OperatorDisplay.PID_LOOP_OUPUT_LABEL, this.pidLoopCalculationOutput);

	}
}
