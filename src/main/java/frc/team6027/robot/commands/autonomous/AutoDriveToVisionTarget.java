package frc.team6027.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

public class AutoDriveToVisionTarget extends CommandGroup {
    // TODO: Read from Robot Preferences
    protected static final int VISION_TURN_LEG_INCHES_INCREMENT = 48;

    protected SensorService sensorService;
    protected DrivetrainSubsystem drivetrain;
    protected OperatorDisplay operatorDisplay;
    protected double stopDistanceFromTarget;
    protected Datahub visionData;
    protected double curVisionDistanceToTarget;
    protected int numAngleAdjustmentStops;

    public AutoDriveToVisionTarget(double stopDistanceFromTarget, 
        SensorService sensorService, DrivetrainSubsystem drivetrain,
        OperatorDisplay operatorDisplay) {
    
        this.stopDistanceFromTarget = stopDistanceFromTarget;
        this.sensorService = sensorService;
        this.drivetrain = drivetrain;
        this.operatorDisplay = operatorDisplay;
    }

    @Override
	protected void initialize() {
        super.initialize();
        // TODO: fall back to ultrasonic if vision number is not good
        this.curVisionDistanceToTarget = this.sensorService.getCurDistToVisionTarget();
        this.numAngleAdjustmentStops = Math.round((float) this.curVisionDistanceToTarget) / VISION_TURN_LEG_INCHES_INCREMENT;
        double initialAngle = this.sensorService.getCurAngleHeadingToVisionTarget();

        // First command will turn to target
        this.addSequential(new TurnCommand(initialAngle, this.sensorService, this.drivetrain, this.operatorDisplay));

        if (this.numAngleAdjustmentStops <= 0) {
            // TODO: Add command to drive using vision and ultrasound to stop
            // Use this.stopDistanceFromTarget
        } else {
            for (int i = 0; i < this.numAngleAdjustmentStops; i++) {
                // Need a command that will turn toward the target, then travel no more than a given distance, 
                // and stop if within certain distance to target.

                // Calculate distance to drive    
                // TODO: Add command to drive using vision and ultrasound to stop
                // Use this.stopDistanceFromTarget
                // TODO: fix VisionTurnCommand to use sensorservice like I did above
                this.addSequential(new VisionTurnCommand(this.sensorService, this.drivetrain, this.operatorDisplay));

            }
        }

	}


}