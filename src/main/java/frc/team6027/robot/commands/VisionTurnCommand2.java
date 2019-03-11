package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

public class VisionTurnCommand2 extends TurnCommand  {
    public static final String NAME = "VisionTurn";
	private final Logger logger = LogManager.getLogger(getClass());

	protected Datahub visionData;

	public VisionTurnCommand2(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay) {
		
		this(sensorService, drivetrain, operatorDisplay, null);
	}

	public VisionTurnCommand2(SensorService sensorService, DrivetrainSubsystem drivetrain,
			OperatorDisplay operatorDisplay, String powerPrefName) {
		
		super(null, sensorService, drivetrain, operatorDisplay, powerPrefName);
	    this.visionData = DatahubRegistry.instance().get(DatahubRegistry.VISION_KEY);
	}


	@Override
	public void start() {
	    logger.info(">>> Vision Turn Command starting, target angle: {}, initial gyro angle", this.targetAngle, this.initialGyroAngle);
		super.start();
	}

	@Override
	protected void reset() {
		this.targetAngle = this.sensorService.getCurAngleHeadingToVisionTarget();
		super.reset();
	}


}
