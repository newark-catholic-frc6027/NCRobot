package org.usfirst.frc.team6027.robot.commands.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;

public class TurnWhileDrivingCommand extends DriveStraightCommand implements PIDOutput {
    public static final String NAME = "Turn While Driving";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Preferences prefs = Preferences.getInstance();

    protected TargetVector[] targetVectors;
    int currentTargetVectorIndex = 0;
    double prevLegLeftEncDistance = 0.0;
    double prevLegRightEncDistance = 0.0;
    double curLegLeftEncDistance = 0.0;
    double curLegRightEncDistance = 0.0;


    
    public TurnWhileDrivingCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, TargetVector[] targetVectors, DriveDistanceMode driveUntil, double drivePower) {
        
        super(sensorService, drivetrainSubsystem, operatorDisplay, targetVectors[0].getDistance(), driveUntil, drivePower);
        this.targetVectors = targetVectors;
        this.setName(NAME);
    }

    @Override
    protected void initialize() {
        super.initialize();
        
        this.curLegLeftEncDistance = 0.0;
        this.curLegRightEncDistance = 0.0;
        this.gyroPidController.setSetpoint(targetVectors[0].getAngle());
    }

    
    @Override
    protected boolean isFinished() {
        if (this.currentTargetVectorIndex >= this.targetVectors.length) {
            this.drivetrainSubsystem.stopMotor();
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TurnWhileDrivingCommand FINISHED, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
            return true;
        }
        
        return false;
    }

    @Override
    protected void execute() {
        TargetVector currentVector = this.targetVectors[this.currentTargetVectorIndex];
        double leftLegDisplacement = this.encoderSensors.getLeftEncoder().getDistance() - this.prevLegLeftEncDistance;
        double rightLegDisplacement = this.encoderSensors.getRightEncoder().getDistance() - this.prevLegRightEncDistance;


        if (Math.min(leftLegDisplacement, rightLegDisplacement) >= currentVector.getDistance()) {
            this.currentTargetVectorIndex++;
            logger.info(">>>>>>>>>>> LEG {} REACHED, displacements(L/R): {}/{}", this.currentTargetVectorIndex, leftLegDisplacement, rightLegDisplacement);
            if (this.currentTargetVectorIndex < this.targetVectors.length) { // Leg completed
                this.gyroPidController.setSetpoint(this.targetVectors[this.currentTargetVectorIndex].getAngle());
                this.prevLegLeftEncDistance = this.encoderSensors.getLeftEncoder().getDistance();
                this.prevLegRightEncDistance = this.encoderSensors.getRightEncoder().getDistance();
                super.execute();
            } else {
                // We're done
                logger.info("Last leg reached.");
            }
        } else {
            super.execute();
        }

    }


    static public class TargetVector {
        double angle;
        double distance;
        
        public TargetVector(double angle, double distance) {
            this.angle = angle;
            this.distance = distance;
        }
        public double getAngle() {
            return angle;
        }
        public void setAngle(double angle) {
            this.angle = angle;
        }
        public double getDistance() {
            return distance;
        }
        public void setDistance(double distance) {
            this.distance = distance;
        }
        
        
    }
}
