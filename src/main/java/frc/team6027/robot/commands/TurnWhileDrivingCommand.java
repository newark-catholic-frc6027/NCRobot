package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorDisplay;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;

public class TurnWhileDrivingCommand extends DriveStraightCommand implements PIDOutput {
    public static final String NAME = "Turn While Driving";
    private final Logger logger = LogManager.getLogger(getClass());
    private Preferences prefs = Preferences.getInstance();

    protected TargetVector[] targetVectors;
    int currentTargetVectorIndex = 0;
    double prevLegLeftEncDistance = 0.0;
    double prevLegRightEncDistance = 0.0;
    double curLegLeftEncDistance = 0.0;
    double curLegRightEncDistance = 0.0;

    Double currentLegPower = null;

    
    public TurnWhileDrivingCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, TargetVector[] targetVectors, DriveDistanceMode driveUntil, double drivePower) {
        
        super(sensorService, drivetrainSubsystem, operatorDisplay, targetVectors[0].getDistance(), driveUntil, drivePower);
        this.targetVectors = targetVectors;
        if (targetVectors.length > 0) {
            this.currentLegPower = targetVectors[0].getPower();
        }
        this.setName(NAME);
    }

    @Override
    protected void reset() {
        super.reset();
        this.currentTargetVectorIndex = 0;
        this.currentLegPower = this.targetVectors[0].getPower();
        this.curLegLeftEncDistance = this.curLegRightEncDistance = 
            this.prevLegLeftEncDistance = this.prevLegRightEncDistance = 0.0;

        if (targetVectors[0].getAngle() != null) {
            this.gyroPidController.setSetpoint(targetVectors[0].getAngle());
        } else {
            this.gyroPidController.setSetpoint(this.gyro.getYawAngle());
        }
    }
    
    @Override
    protected boolean isFinished() {
        if (this.currentTargetVectorIndex >= this.targetVectors.length) {
            this.drivetrainSubsystem.stopMotor();
            this.closePidControllers();
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TurnWhileDrivingCommand FINISHED, distance={}", this.encoderSensors.getLeftEncoder().getDistance());
            this.isReset = false;
            return true;
        }
        
        return false;
    }

    @Override 
    protected double getDrivePower() {
        if (this.currentLegPower != null) {
            return this.currentLegPower;
        } else {
            return super.getDrivePower();
        }
    }
    @Override
    protected void execute() {
        
        TargetVector currentVector = this.targetVectors[this.currentTargetVectorIndex];
        double leftLegDisplacement = this.encoderSensors.getLeftEncoder().getRelativeDistance() - this.prevLegLeftEncDistance;
        double rightLegDisplacement = this.encoderSensors.getRightEncoder().getRelativeDistance() - this.prevLegRightEncDistance;


        if (Math.min(leftLegDisplacement, rightLegDisplacement) >= currentVector.getDistance()) {
            this.currentTargetVectorIndex++;
            logger.info(">>>>>>>>>>> LEG {} REACHED, displacements(L/R): {}/{}", 
               this.currentTargetVectorIndex, leftLegDisplacement, rightLegDisplacement);
            if (this.currentTargetVectorIndex < this.targetVectors.length) { // Leg completed
                currentVector = this.targetVectors[this.currentTargetVectorIndex];
                if (currentVector.getAngle() != null) {
                    logger.info("Using current vector angle: {}", currentVector.getAngle());
                    this.gyroPidController.setSetpoint(currentVector.getAngle());
                } else {
                    logger.info("Using current gyro angle: {}", this.gyro.getYawAngle());
                    this.gyroPidController.setSetpoint(this.gyro.getYawAngle());
                }
                if (currentVector.getPower() != null) {
                    logger.info("Changing power to: {}", currentVector.getPower());
                    this.currentLegPower = currentVector.getPower();
                    this.gyroPidController.setOutputRange(-1 * currentVector.getPower(), currentVector.getPower());
                }
                this.prevLegLeftEncDistance = this.encoderSensors.getLeftEncoder().getRelativeDistance();
                this.prevLegRightEncDistance = this.encoderSensors.getRightEncoder().getRelativeDistance();
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
        Double angle;
        String anglePref = null;
        Double power = null;
        String powerPref  = null;
        double distance;
        String distancePref = null;
        private Preferences prefs = Preferences.getInstance();

        
        public TargetVector(Double angle, double distance) {
            this.angle = angle;
            this.distance = distance;
        }
        public TargetVector(Double angle, double distance, Double power) {
            this.angle = angle;
            this.distance = distance;
            this.power = power;
        }

        public TargetVector(String anglePref, String distancePref, String powerPref) {
            this.anglePref = anglePref;
            this.distancePref = distancePref;
            this.powerPref = powerPref;
        }

        public Double getAngle() {
            if (this.anglePref != null) {
                return this.prefs.getDouble(this.anglePref, 0.0);
            } else {
                return angle;
            }
        }
        public void setAngle(Double angle) {
            this.angle = angle;
        }
        public double getDistance() {
            if (this.distancePref != null) {
                return this.prefs.getDouble(this.distancePref, 0.0);
            }  else {
                return distance;
            }
        }
        public void setDistance(double distance) {
            this.distance = distance;
        }
        public Double getPower() {
            if (this.powerPref != null) {
                return this.prefs.getDouble(this.powerPref, 0.0);
            } else {
                return power;
            }
        }
        public void setPower(Double power) {
            this.power = power;
        }
        
        
    }
}
