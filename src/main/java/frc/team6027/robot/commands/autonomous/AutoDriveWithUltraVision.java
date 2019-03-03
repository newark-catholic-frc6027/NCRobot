package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.FlexCommandGroup;
import frc.team6027.robot.commands.TurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.sensors.UltrasonicSensor;
import frc.team6027.robot.sensors.UltrasonicSensorManager.UltrasonicSensorKey;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoDriveWithUltraVision extends Command {
    public static final String NAME = "DriveWithUltraVision";
	private final Logger logger = LogManager.getLogger(getClass());
	
    protected Datahub visionData;

    protected SensorService sensorService;
    protected DrivetrainSubsystem drivetrain;
    protected OperatorDisplay operatorDisplay;
    protected UltrasonicSensor ultrasonic;

    protected double curVisionDistanceToTarget = -1.0;
    protected double stopDistanceFromTarget = 2.0;
    protected double maxTravelDistance = -1.0;
    protected boolean initialTurnFinished = false;
    protected double targetAngle;
    protected double drivePower = 7.0;

    protected FlexCommandGroup commandGroup;

    /**
     * If you set maxTravelDistance to a large number, the stopDistanceFromTarget argument will be used
     * to stop driving when either the vision or ultrasonic sensors return a distance from target that is
     * less than or equal that value.
     */
    public AutoDriveWithUltraVision(double maxTravelDistance, double stopDistanceFromTarget, double drivePower, 
        SensorService sensorService, DrivetrainSubsystem drivetrain, OperatorDisplay operatorDisplay) {
        this.sensorService = sensorService;
        this.drivetrain = drivetrain;
        this.operatorDisplay = operatorDisplay;
        this.drivePower = drivePower;
        this.ultrasonic = sensorService.getUltrasonicSensor(UltrasonicSensorKey.Front);
		
        this.visionData = DatahubRegistry.instance().get(DatahubRegistry.VISION_KEY);
        this.stopDistanceFromTarget = stopDistanceFromTarget;
        this.maxTravelDistance = maxTravelDistance;

        this.setName(NAME);
	}

	
	protected void prepare() {
        this.commandGroup = new FlexCommandGroup();

        this.curVisionDistanceToTarget = this.sensorService.getCurDistToVisionTarget();
        this.targetAngle = this.sensorService.getCurAngleHeadingToVisionTarget();
        this.logger.info("Adding TurnCommand and DriveStraightCommand");
        this.commandGroup.addSequential(new TurnCommand(this.targetAngle, this.sensorService, this.drivetrain, this.operatorDisplay));
        this.commandGroup.addSequential(new DriveStraightCommand(this.sensorService, this.drivetrain, this.operatorDisplay, 
            this.stopDistanceFromTarget*-1, DriveDistanceMode.DistanceFromObject, this.drivePower));
	}

    @Override
    public void start() {
        this.logger.info("Started.  targetAngle={}", this.targetAngle);
        this.prepare();
        this.commandGroup.start();
    }

    @Override
    protected boolean isFinished() {
        // TODO: why is this null?
        if (this.commandGroup == null) {
            return false;
        }
        return this.commandGroup.isFinished();
    }


}
