package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.sensors.PIDCapableGyro;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.Drive;

/*
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
*/
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class TurnCommand extends CommandBase {
    public static final String NAME = "Turn";
	private final Logger logger = LogManager.getLogger(getClass());
	protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.005;
	protected static final double PID_INTEGRAL_COEFFICIENT = 0.00;
	protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
	protected static final double PID_FEED_FORWARD_TERM = 0.5;
	/* This tuning parameter indicates how close to "on target" the */
	/* PID Controller will attempt to get. */
	protected static final double PID_TOLERANCE_DEGREES = 2.0;
	protected static final double DRIVE_POWER = 0.5;
	
	protected Preferences prefs = Preferences.getInstance();
	protected long executionStartThreshold = this.prefs.getLong("turnCommand.execStartThreshold", 500);
	protected double pidAngleStopThreshold = this.prefs.getDouble("turnCommand.pidAngleStopThreshold", 0.1);
	protected PIDController pidController;

	protected SensorService sensorService;
	protected PIDCapableGyro gyro;

	protected Drive drivetrain;
	protected Double targetAngle;

	protected double pidLoopCalculationOutput;
	protected OperatorDisplay operatorDisplay;
	protected long startTime;
	protected double initialGyroAngle;
	protected int execCount = 0;
	protected String anglePrefName = null;
	protected Double turnPower;
	protected String turnPowerPrefName = null;
	protected double turnMinPower;
	protected double adjustedPower;
	protected boolean isReset = false;
	protected double lastYaw = 0.0;
	protected long unchangedYawStartMs = -1;
	protected boolean stopTurnNow = false;

	public TurnCommand(String anglePrefName, SensorService sensorService, Drive drivetrain,
		OperatorDisplay operatorDisplay) {
		this(sensorService, drivetrain, operatorDisplay);
		this.anglePrefName = anglePrefName;
	}

	public TurnCommand(String anglePrefName, SensorService sensorService, Drive drivetrain,
		OperatorDisplay operatorDisplay, String powerPrefName) {
		this(sensorService, drivetrain, operatorDisplay);
		this.anglePrefName = anglePrefName;
		this.turnPowerPrefName = powerPrefName;
	}

	public TurnCommand(SensorService sensorService, Drive drivetrain,
		OperatorDisplay operatorDisplay) {
		this(-1, sensorService, drivetrain, operatorDisplay);
	}

	public TurnCommand(double angle, SensorService sensorService, Drive drivetrain,
			OperatorDisplay operatorDisplay) {
		this(angle, sensorService, drivetrain, operatorDisplay, null);
	}
	
	public TurnCommand(double angle, SensorService sensorService, Drive drivetrain,
			OperatorDisplay operatorDisplay, Double power) {
	    this.addRequirements(drivetrain);
		this.sensorService = sensorService;
		this.gyro = this.sensorService.getGyroSensor();
		this.drivetrain = drivetrain;
		this.targetAngle = angle;
		this.operatorDisplay = operatorDisplay;
		if (null != power) {
			this.turnPower = power;
		}
		this.setName(NAME);
	}

	protected void closePidController() {
//		this.pidController.disable();
		this.pidController.close();
		this.pidController = null;
	}

	protected void initPIDController() {
		if (this.pidController != null) {
			this.closePidController();
		}
		this.pidLoopCalculationOutput = 0.0;
		this.pidController = new PIDController(
			this.prefs.getDouble("turnCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
			this.prefs.getDouble("turnCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
			this.prefs.getDouble("turnCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT)
		);
		/*
				this.prefs.getDouble("turnCommand.feedForward", PID_FEED_FORWARD_TERM),
				this.sensorService.getGyroSensor().getPIDSource(), this
		*/
		if (this.turnPowerPrefName == null) {
			if (this.turnPower == null) {
				this.turnPower = this.prefs.getDouble("turnCommand.turnPower", .1);
			}
		} else {
			this.turnPower = this.prefs.getDouble(this.turnPowerPrefName, .1);
		}

		this.pidController.enableContinuousInput(-180.0, 180.0);
//		this.pidController.setInputRange(-180.0, 180.0);
		// TODO: pass this in as a parameter
        this.pidController.setIntegratorRange(-0.5, 0.5);
//		this.pidController.setOutputRange(-1* this.turnPower, this.turnPower);
		this.pidController.setTolerance(this.getPidTolerance());
//		this.pidController.setAbsoluteTolerance(this.getPidTolerance());
//		this.pidController.setContinuous(true);
		if (this.targetAngle != null) {
			if (this.targetAngle < -180.0) {
				this.targetAngle = 180.0 - Math.abs(-180.0 - this.targetAngle);
			} else if (this.targetAngle > 180.0) {
				this.targetAngle = -180.0 + Math.abs(180.0 - this.targetAngle);
			}
			this.logger.info("Requested Pid setpoint: {}", this.targetAngle);
			this.pidController.setSetpoint(this.targetAngle); // sets the angle to which we want to turn to
			this.logger.info("Actual Pidcontroller setpoint: {}", this.pidController.getSetpoint());
//			this.pidController.enable();
		}
		this.stopTurnNow = false;
	}

	protected double getPidTolerance() {
		return PID_TOLERANCE_DEGREES;
	}

	@Override
	public boolean isFinished() {
	    
	    if (this.stopTurnNow || (this.pidController.atSetpoint() /*
            && Math.abs(this.gyro.getRate()) <= pidAngleStopThreshold */)) {

			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Turn done, angle={}, stopTurnNow? {}, pidController.onTarget? {}, pidController setpoint: {}, gyro rate: {}, pidAngleStopThreshold: {} ", 
			      this.sensorService.getGyroSensor().getYawAngle(), this.stopTurnNow, this.pidController.atSetpoint(), this.pidController.getSetpoint(), this.gyro.getRate(), this.pidAngleStopThreshold);
	        
			this.closePidController();
			this.isReset = false;
			this.stopTurnNow = false;
            return true;
	    } else {
	        return false;
	    }
	}

	@Override
	public void cancel() {
		this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());

		this.isReset = false;
		this.stopTurnNow = false;

		this.closePidController();
		super.cancel();
	}

    @Override
    public void end(boolean interrupted) {
		if (interrupted) {
			this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
		}
		this.isReset = false;
		this.stopTurnNow = false;

		this.drivetrain.stopMotor();

		this.closePidController();
		super.end(interrupted);
    }

	protected void reset() {
        if (isReset) {
            this.logger.info("Not reset since reset has already been run");
            return;
		}
		this.isReset = true;
		this.stopTurnNow = false;

		this.execCount = 0;
		this.startTime = System.currentTimeMillis();
		this.initialGyroAngle = this.gyro.getYawAngle();

		if (this.anglePrefName != null) {
			this.targetAngle = this.prefs.getDouble(this.anglePrefName, 0.0);
		}

		this.turnMinPower = this.getTurnMinPower();
		this.adjustedPower = this.getAdjustedPower();
		initPIDController();
	}

	protected double getTurnMinPower() {
		return prefs.getDouble("turnCommand.minPower", .20);
	}

	protected double getAdjustedPower() {
		return prefs.getDouble("turnCommand.adjustedPower", 0.3);
	}

/*
	@Override
	public void start() {
		this.reset();
		logger.info(">>> Turn Command starting, target angle: {}, initial gyro angle: {}, turnMinPower: {}, turnPower: {}", 
		    this.targetAngle, this.initialGyroAngle, this.turnMinPower, this.turnPower);
		super.start();
	}
*/

    @Override
    public void initialize() {
		super.initialize();
		logger.info(">>> Turn Command initializing, target angle: {}, initial gyro angle: {}, turnMinPower: {}, turnPower: {}", 
		    this.targetAngle, this.initialGyroAngle, this.turnMinPower, this.turnPower);

		this.reset();
    }

	public void execute() {
        this.execCount++;
		long currentElapsedExecutionMs = System.currentTimeMillis() - this.startTime;
//		double pidOutput = this.pidLoopCalculationOutput;
		double currentYaw =  this.gyro.getYawAngle();
		double pidOutput = this.pidController.calculate(this.gyro.getYawAngle());

		double leftPower = pidOutput;
		double rightPower = -1 * leftPower;
		double angleDelta = Math.abs(this.targetAngle - currentYaw);
		boolean done = false;
		/*
		if (currentYaw == this.lastYaw) {
			long currentTs = System.currentTimeMillis();
			if (unchangedYawStartMs == -1) {
				unchangedYawStartMs = currentTs;
			}
			if (currentTs-unchangedYawStartMs >= 500) {
				logger.error("@@@@@@@@@! Gyro angle not changing! currentYaw: {}, lastYaw: {}", currentYaw, lastYaw);
			}
		} else {
			unchangedYawStartMs = -1;
		}
		*/
		/*
		boolean powerDroppedBelowMin = Math.abs(leftPower) <= this.turnMinPower;
		// If we are between X and 1.5 * X degrees of our target and our power has dropped under a minimum threshold
		// increase power to an adjusted value in order to get the PID loop going again.
		if (powerDroppedBelowMin) {
			// If our rate slows down below threshold, just cancel the command.  we're done.
			if (angleDelta <= this.getPidTolerance()) {
				if ( Math.abs(this.gyro.getRate()) <= pidAngleStopThreshold) {
					this.stopTurnNow = true;
					this.logger.info("Force Stopping turn.");
					// Can't cancel when we are in a command group
					//this.cancel();
				}
			} else {
				// Power is below min, but we aren't within tolerance yet, increase power
				leftPower  = leftPower < 0.0 ? -1 * this.adjustedPower : this.adjustedPower;
				rightPower = -1 * leftPower;
				logger.info("Power increased to: {}", leftPower);
			}
		} else {
			// Let pid resume control
			rightPower = -1 * leftPower;
		}
*/

/*
		if (angleDelta > PID_TOLERANCE_DEGREES || powerDroppedBelowMin) {
			if ((angleDelta < 1.5 * PID_TOLERANCE_DEGREES && powerDroppedBelowMin) || powerDroppedBelowMin) {
				leftPower  = leftPower < 0.0 ? -1 * this.adjustedPower : this.adjustedPower;
				logger.info("Power increased to: {}", leftPower);
			}
		}
		rightPower = -1 * leftPower;
*/
			
		if (this.execCount % 4 == 0) {
			logger.trace("yaw: {}, pidOutput: {}, gyro rate: {}, leftPower: {}, rightPower: {}", 
					String.format("%.3f",this.gyro.getYawAngle()), 
					String.format("%.3f",pidOutput), 
					String.format("%.3f",this.gyro.getRate()),
					String.format("%.3f", leftPower), 
					String.format("%.3f", rightPower));
		}
		
		if (! this.stopTurnNow) {
			this.drivetrain.tankDrive(leftPower, rightPower);
			this.lastYaw = currentYaw;
		}
	}

	public double getTargetAngle() {
        return targetAngle;
    }

    public void setTargetAngle(double targetAngle) {
        this.targetAngle = targetAngle;
    }

	/*
    @Override
	public void pidWrite(double output) {
		this.pidLoopCalculationOutput = output;

	}
	*/
}
