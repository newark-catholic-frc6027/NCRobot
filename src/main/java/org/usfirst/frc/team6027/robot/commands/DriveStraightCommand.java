package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.EncoderSensors;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.sensors.UltrasonicSensor;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class DriveStraightCommand extends Command implements PIDOutput {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final double PID_PROPORTIONAL_COEFFICIENT = 0.08;
    protected static final double PID_INTEGRAL_COEFFICIENT = 0.00;
    protected static final double PID_DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double PID_FEED_FORWARD_TERM = 0.3;
    protected static final double PID_TOLERANCE = 0.5;

    public enum DriveDistanceMode {
        DistanceReadingOnEncoder,
        DistanceFromObject
    }
    
    private SensorService sensorService;
    private EncoderSensors encoderSensors;
    private UltrasonicSensor ultrasonicSensor;
    private Gyro gyro;
    private DrivetrainSubsystem drivetrainSubsystem;
    private OperatorDisplay operatorDisplay;
    private double driveDistance;
    private double currentDistance = 0;
    private DriveDistanceMode driveUntil = DriveDistanceMode.DistanceReadingOnEncoder;
    private PIDController pidController;
    private Preferences prefs = Preferences.getInstance();
    private double pidLoopCalculationOutput;


    // private double targetDistance =
    double diff = 0; // Creating variable for the difference between right
                     // encoder distance and left encoder distance
    int leftcount = 0;
    int rightcount = 0;
    int centercount = 0;

    public DriveStraightCommand(SensorService sensorService, DrivetrainSubsystem drivetrainSubsystem,
            OperatorDisplay operatorDisplay, double driveDistance, DriveDistanceMode driveUntil) {
        requires(drivetrainSubsystem);
        this.sensorService = sensorService;
        this.encoderSensors = this.sensorService.getEncoderSensors();
        this.ultrasonicSensor = this.sensorService.getUltrasonicSensor();
        this.gyro = this.sensorService.getGyroSensor();
        this.drivetrainSubsystem = drivetrainSubsystem;
        this.driveDistance = driveDistance;
        this.operatorDisplay = operatorDisplay;
        if (driveUntil != null) {
            this.driveUntil = driveUntil;
        }
        
        initPIDController();
    }

    protected void initPIDController() {
        // pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff",
        // PROPORTIONAL_COEFFICIENT), INTEGRAL_COEFFICIENT, DERIVATIVE_COEFFICIENT,
        // FEED_FORWARD_TERM, this.gyro.getPIDSource(), this);
        pidController = new PIDController(this.prefs.getDouble("driveStraightCommand.pCoeff", PID_PROPORTIONAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.iCoeff", PID_INTEGRAL_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.dCoeff", PID_DERIVATIVE_COEFFICIENT),
                this.prefs.getDouble("driveStraightCommand.feedForward", PID_FEED_FORWARD_TERM),
                this.sensorService.getEncoderSensors().getLeftEncoder(), this);
        
        // TODO: change input and output ranges
        pidController.setInputRange(-180.0, 180.0);
        pidController.setOutputRange(-1.0, 1.0);
        pidController.setAbsoluteTolerance(PID_TOLERANCE);
        pidController.setContinuous(true);
        pidController.setSetpoint(this.driveDistance); // sets the angle to which we want to turn to
        pidController.enable();

    }
    @Override
    protected boolean isFinished() {
        // TODO: use the PIDController to determine when we are done
        if (this.driveUntil == DriveDistanceMode.DistanceReadingOnEncoder) {
            if (Math.abs(this.encoderSensors.getLeftEncoder().getDistance()) >= this.driveDistance) {
                this.drivetrainSubsystem.stopArcadeDrive();
                return true;
            } else {
                return false;
            }
        } else if (this.driveUntil == DriveDistanceMode.DistanceFromObject) {
            if (this.ultrasonicSensor.getDistanceInches() <= this.driveDistance) {
                this.drivetrainSubsystem.stopArcadeDrive();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void execute() {
        logger.trace("Gyro angle: {}, left-enc: {}, right-enc: {}", 
                this.gyro.getAngle(), this.encoderSensors.getLeftEncoder().getDistance(), 
                this.encoderSensors.getRightEncoder().getDistance()
        );
        this.drivetrainSubsystem.drive(0.2, 0);
/*        
        if (gyro.getAngle() <= 2.0) {
            this.drivetrainSubsystem.drive(0.2, -0.2);
        } else if (gyro.getAngle() >= -2.0) {
            this.drivetrainSubsystem.drive(0.2, 0.2);
        } else {
            this.drivetrainSubsystem.drive(0.2, 0);
        }
        */
        // if (this.sensorService.getUltrasonicSensor().getDistanceInches() ==
        // driveDistance) {
        // this.drivetrainSubsystem.stopArcadeDrive();
        // }

        // double diff=this.encoderSensors.getRightEncoder().getDistance() -
        // this.encoderSensors.getLeftEncoder().getDistance();
        // /*
        // if(this.encoderSensors.getRightEncoder().getDistance()<=1 ||
        // this.encoderSensors.getRightEncoder().getDistance()>=-5) {
        // this.drivetrainSubsystem.drive(-0.3, 0);
        // }*/
        //
        //
        // if(diff>0.05) {
        // rightcount = rightcount + 1;
        // this.drivetrainSubsystem.drive(-0.3, 0);
        // } else if(diff<-0.05) {
        // leftcount++;
        // if(leftcount % 4 == 0) {
        // this.drivetrainSubsystem.drive(-0.3, -0.01);
        //
        // }
        //
        // } else if (diff<=0.05 || diff>=-0.05) {
        // this.encoderSensors.getRightEncoder().reset();
        // this.encoderSensors.getLeftEncoder().reset();
        // centercount++;
        // this.drivetrainSubsystem.drive(-0.3, 0);
        // }
        // this.operatorDisplay.setNumericFieldValue("leftcount",leftcount);
        // this.operatorDisplay.setNumericFieldValue("rightcount",rightcount);
        // this.operatorDisplay.setNumericFieldValue("centercount",
        // centercount);

    }

    @Override
    public void pidWrite(double output) {
        this.pidLoopCalculationOutput = output;
        this.operatorDisplay.setFieldValue(OperatorDisplay.PID_LOOP_OUPUT_LABEL, this.pidLoopCalculationOutput);
    }

}
