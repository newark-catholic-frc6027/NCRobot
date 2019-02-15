package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

public class AutoDriveToVisionTarget extends CommandGroup {
    private final Logger logger = LogManager.getLogger(getClass());

    // TODO: Read from Robot Preferences
    protected static final int VISION_TURN_LEG_INCHES_INCREMENT = 48;

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

	protected void prepare(){

        this.curVisionDistanceToTarget = this.sensorService.getCurDistToVisionTarget();
        this.numAngleAdjustmentStops = Math.round((float) this.curVisionDistanceToTarget) / VISION_TURN_LEG_INCHES_INCREMENT;

        logger.info("Initializing AutoDriveToVisionTarget command with {} angle adjustment stops " + 
            "and curVisionDistanceToTarget={}", this.numAngleAdjustmentStops, this.curVisionDistanceToTarget);

        for (int i = this.numAngleAdjustmentStops; i >= 0 ; i--) {
            if (i == 0) {
                this.addSequential(new AutoDriveWithUltraVision(
                    20*12 /* use large value so that it will use stopDistanceFromTarget for stopping*/, 
                    this.stopDistanceFromTarget, this.drivePower, 
                    this.sensorService, this.drivetrain, this.operatorDisplay ));
            } else {
                this.addSequential(new AutoDriveWithUltraVision(
                    VISION_TURN_LEG_INCHES_INCREMENT,
                    this.stopDistanceFromTarget, this.drivePower, 
                    this.sensorService, this.drivetrain, this.operatorDisplay ));
            }
        }
    }

    @Override
    protected void execute() {
        this.logger.debug("Execute invoked");
        super.execute();    
    }

    @Override
    public void start() {
        this.prepare();
        super.start();
    }
    
    @Override
    protected boolean isFinished() {
        // TODO: Add logic to check for Joystick button press to cancel the command
        boolean isFinished = super.isFinished();
        if (isFinished) {
            this.logger.debug("isFinished = true");
        }
        return isFinished;
    }
}