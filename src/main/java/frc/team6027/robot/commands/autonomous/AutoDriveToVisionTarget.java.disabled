package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.commands.DriveStraightCommand;
import frc.team6027.robot.commands.FlexCommand;
import frc.team6027.robot.commands.VisionTurnCommand;
import frc.team6027.robot.commands.DriveStraightCommand.DriveDistanceMode;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

public class AutoDriveToVisionTarget extends FlexCommand implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    // TODO: Read from Robot Preferences
    protected static final double VISION_TURN_LEG_INCHES_INCREMENT = 48.0;

    protected SensorService sensorService;
    protected DrivetrainSubsystem drivetrain;
    protected OperatorDisplay operatorDisplay;
    protected double stopDistanceFromTarget;
    protected Datahub visionData;
    protected double curVisionDistanceToTarget;
    protected int numAngleAdjustmentStops;
    protected double drivePower = 7.0;

    public AutoDriveToVisionTarget(double stopDistanceFromTarget, double drivePower,
        SensorService sensorService, DrivetrainSubsystem drivetrain,
        OperatorDisplay operatorDisplay) {
        this.stopDistanceFromTarget = stopDistanceFromTarget;
        this.sensorService = sensorService;
        this.drivetrain = drivetrain;
        this.operatorDisplay = operatorDisplay;
        this.drivePower = drivePower;
    }

    @Override
	protected void prepare(){
        super.prepare();

        this.curVisionDistanceToTarget = this.sensorService.getCurDistToVisionTarget();
        this.numAngleAdjustmentStops = (int) (Math.round(this.curVisionDistanceToTarget) / VISION_TURN_LEG_INCHES_INCREMENT);

        logger.info("Initializing AutoDriveToVisionTarget command with {} angle adjustment stops " + 
            "and curVisionDistanceToTarget={}", this.numAngleAdjustmentStops, this.curVisionDistanceToTarget);

        for (int i = this.numAngleAdjustmentStops; i >= 0 ; i--) {
            if (i == 0) {
                this.addDriveCommands(-1.0);
                logger.debug("Added last drive command, stopDistanceFromTarget={}", this.stopDistanceFromTarget);
            } else {
                this.addDriveCommands(VISION_TURN_LEG_INCHES_INCREMENT);
                logger.debug("Added drive command, distance = {}, stopDistanceFromTarget={}", VISION_TURN_LEG_INCHES_INCREMENT,
                    this.stopDistanceFromTarget);
            }
        }
    }

    protected void addDriveCommands(double maxDriveDistance) {
        this.commandGroup.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrain, this.operatorDisplay));

        DriveDistanceMode mode = maxDriveDistance >= 0 ? DriveDistanceMode.DistanceReadingOnEncoder : DriveDistanceMode.DistanceFromObject;
        double distance = maxDriveDistance >= 0 ? maxDriveDistance * -1 : this.stopDistanceFromTarget;
        this.commandGroup.addSequential(
            new DriveStraightCommand(this.sensorService, this.drivetrain, this.operatorDisplay, distance, mode, this.drivePower));
    }

    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
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
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }

}