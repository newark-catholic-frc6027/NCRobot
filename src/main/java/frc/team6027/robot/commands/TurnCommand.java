package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.sensors.PIDCapableGyro;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class TurnCommand extends Command implements PIDOutput {
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

	protected DrivetrainSubsystem drivetrain;
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

	public TurnCommand(String anglePrefName, SensorService sensorService, DrivetrainSubsystem drivetrain,
		OperatorDisplay operatorDisplay) {
		this(sensorService, drivetrain, operatorDisplay);
		this.anglePrefName = anglePrefName;
	}

	public TurnCommand(String anglePrefName, SensorService sensorService, DrivetrainSubsystem drivetrain,
		OperatorDisplay operatorDisplay, String powerPrefName) {
		this(sensorService, drivetrain, operatorDisplay);
		this.anglePrefName = anglePrefName;
		this.turnPowerPrefName = powerPrefName;
	}

	public TurnCommand(SensorService sensorService, DrivetrainSubsystem drivetrain,
		OperatorDisplay operatorDisplay) {
		this(-1, sensorService, drivetrain, operatorDisplay);
	}

	public TurnCommand(double angle, SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay) {
		this(angle, sensorService, drivetrain, operatorDisplay, null);
	}
	
	public TurnCommand(double angle, SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay, Double power) {
		requires(drivetrain);
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

	protected void initPIDController() {
		if (this.pidController != null) {
			this.pidController.disable();
		}
		this.pidLoopCalculationOutput = 0.0;
		pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
				this.prefs.getDouble("turnCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
				this.prefs.getDouble("turnCommand.feedForward", PID_FEED_FORWARD_TERM),
				this.sensorService.getGyroSensor().getPIDSource(), this);
		if (this.turnPowerPrefName == null) {
			if (this.turnPower == null) {
				this.turnPower = this.prefs.getDouble("turnCommand.turnPower", .1);
			}
		} else {
			this.turnPower = this.prefs.getDouble(this.turnPowerPrefName, .1);
		}

		pidController.setInputRange(-180.0, 180.0);
		pidController.setOutputRange(-1* this.turnPower, this.turnPower);
		pidController.setAbsoluteTolerance(PID_TOLERANCE_DEGREES);
		pidController.setContinuous(true);
		if (this.targetAngle != null) {
			this.logger.info("Pid setpoint set to: {}", this.targetAngle);
			pidController.setSetpoint(this.targetAngle); // sets the angle to which we want to turn to
			pidController.enable();
		}
	}

	@Override
	protected boolean isFinished() {
	    
	    if (this.pidController.onTarget()
            && Math.abs(this.gyro.getRate()) <= pidAngleStopThreshold) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Turn done, angle={}", this.sensorService.getGyroSensor().getYawAngle());
	        
			pidController.disable();
			this.isReset = false;
            return true;
	    } else {
	        return false;
	    }
	}

	@Override
	public void cancel() {
		this.isReset = false;

		if (this.pidController != null) {
			this.pidController.disable();
		}
		super.cancel();
	}

	protected void reset() {
        if (isReset) {
            this.logger.info("Not reset since reset has already been run");
            return;
		}
		this.isReset = true;

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

	@Override
	public void start() {
		this.reset();
		logger.info(">>> Turn Command starting, target angle: {}, initial gyro angle: {}, turnMinPower: {}, turnPower: {}", 
		    this.targetAngle, this.initialGyroAngle, this.turnMinPower, this.turnPower);
		super.start();
	}

    @Override
    protected void initialize() {
        this.reset();
    }

	protected void execute() {
        this.execCount++;
		long currentElapsedExecutionMs = System.currentTimeMillis() - this.startTime;
		double pidOutput = this.pidLoopCalculationOutput;
		
		double leftPower = 0;
		double rightPower = 0;
		
		leftPower = pidOutput;
		double angleDelta = Math.abs(this.targetAngle - this.gyro.getYawAngle());
		boolean powerDroppedBelowMin = Math.abs(leftPower) <= this.turnMinPower;
		// If we are between X and 1.5 * X degrees of our target and our power has dropped under a minimum threshold
		// increase power to an adjusted value in order to get the PID loop going again.
		if (angleDelta > PID_TOLERANCE_DEGREES || powerDroppedBelowMin) {
			if ((angleDelta < 1.5 * PID_TOLERANCE_DEGREES && powerDroppedBelowMin) || powerDroppedBelowMin) {
				leftPower  = leftPower < 0.0 ? -1 * this.adjustedPower : this.adjustedPower;
				logger.info("Power increased to: {}", leftPower);
			}
		}

		rightPower = -1 * leftPower;
			
		if (this.execCount % 4 == 0) {
			logger.trace("yaw: {}, pid: {}, gyro rate: {}, leftPower: {}, rightPower: {}", 
					String.format("%.3f",this.gyro.getYawAngle()), 
					String.format("%.3f",this.pidLoopCalculationOutput), 
					String.format("%.3f",this.gyro.getRate()),
					String.format("%.3f", leftPower), 
					String.format("%.3f", rightPower));
		}
		
		this.drivetrain.tankDrive(leftPower, rightPower);           
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
