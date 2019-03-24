package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.autonomous.KillableAutoCommand;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.VisionDataConstants;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

public class VisionTurnCommand extends TurnCommand implements KillableAutoCommand  {
    public static final String NAME = "VisionTurn";
	private final Logger logger = LogManager.getLogger(getClass());

	protected Datahub visionData;
	protected boolean visionDataChecked = false;
	protected boolean visionDataValid = false;
	protected boolean finished = true;
	protected Object _finishedLock = new Object();

	public VisionTurnCommand(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay) {
		
		this(sensorService, drivetrain, operatorDisplay, (String) null);
		
	}

	public VisionTurnCommand(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay, Double timeout) {
		
		this(sensorService, drivetrain, operatorDisplay, (String) null);
		if (timeout != null) {
			this.setTimeout(timeout);
		}
		
	}

	public VisionTurnCommand(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay, String powerPrefName) {

		this(sensorService, drivetrain, operatorDisplay, powerPrefName, null);
	}

	public VisionTurnCommand(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay, String powerPrefName, Double timeout) {
		super(null, sensorService, drivetrain, operatorDisplay, powerPrefName);
		this.visionData = DatahubRegistry.instance().get(VisionDataConstants.VISION_DATA_KEY);
		if (timeout != null) {
			this.setTimeout(timeout);
		}
	}

	@Override
	public void start() {
		synchronized (this._finishedLock) {
			if (! this.finished) {
				logger.info("Already running Vision Turn, returning");
				return;
			}
			this.finished = false;
		}
        this.registerAsKillable();
        logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
	    logger.info(">>> Vision Turn Command, target angle: {}, initial gyro angle :{}", this.targetAngle, this.initialGyroAngle);
		super.start();
	}

	@Override
	protected boolean isFinished() {
		if (! this.visionDataValid) {
			this.isReset = false;
			logger.warn("Vision data is not valid, not turning.  Command finished.");
			synchronized (this._finishedLock) {
				this.finished = true;

			}
			return true;
		}

		synchronized (this._finishedLock) {
			this.finished = super.isFinished();
		}
		return this.finished;
	}

	@Override
	protected void execute() {
		synchronized (this._finishedLock) {
			this.finished = false;
		}
		if (! visionDataChecked) {
			this.reset();
			logger.info(">>> Vision Turn Command starting, target angle: {}, initial gyro angle: {}", this.targetAngle, this.initialGyroAngle);
			this.registerAsKillable();
    		this.visionDataValid = this.targetAngle != null;
			visionDataChecked = true;
		} 

		if (this.visionDataValid) {
			super.execute();
		} else {
			logger.warn("Vision data is not valid, not turning");
		}
	}

	protected double getTurnMinPower() {
		return prefs.getDouble("visionTurnCommand.minPower", .20);
	}

	protected double getAdjustedPower() {
		return prefs.getDouble("visionTurnCommand.adjustedPower", 0.3);
	}

	@Override
	protected void reset() {

		this.targetAngle = this.sensorService.getCurAngleHeadingToVisionTarget();
		logger.info(">>> Vision Turn Command reset, target angle: {}, initial gyro angle", this.targetAngle, this.initialGyroAngle);
		this.visionDataChecked = false;
		this.visionDataValid = false;
		super.reset();
	}

	@Override
	public void registerAsKillable() {
		this.default_registerAsKillable();
	}

	@Override
	public void onComplete() {
	}

    @Override
    public void end() {
        // When it ends peacefully, clean up the Killable command
        this.default_onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
    }


}
