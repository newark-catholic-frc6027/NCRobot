package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.sensors.SensorService;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class TurnCommand extends Command implements PIDOutput {
    
    protected static final double PROPORTIONAL_COEFFICIENT = 0.08;
    protected static final double INTEGRAL_COEFFICIENT = 0.00;
    protected static final double DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double FEED_FORWARD_TERM = 0.00;
    /* This tuning parameter indicates how close to "on target" the    */
    /* PID Controller will attempt to get.                             */
    protected static final double TOLERANCE_DEGREES = 2.0;
    private Preferences prefs = Preferences.getInstance();
    private PIDController pidController;
    
    private SensorService sensorService;
    
    private DrivetrainSubsystem drivetrain;
    private double targetAngle;
    
    private double pidLoopCalculationOutput;
    private OperatorDisplay operatorDisplay;
    
   public TurnCommand (double angle, SensorService sensorService, DrivetrainSubsystem drivetrain, OperatorDisplay operatorDisplay) {
       this.sensorService = sensorService;
       this.drivetrain = drivetrain;
       this.targetAngle = angle;
       this.operatorDisplay = operatorDisplay;
       
       initPIDController();
   }
   @Override
   protected void initialize() {
	   this.sensorService.getGyroSensor().reset();
   }
    
   protected void initPIDController() {
	     // pidController = new PIDController(this.prefs.getDouble("turnCommand.pCoeff", PROPORTIONAL_COEFFICIENT), INTEGRAL_COEFFICIENT, DERIVATIVE_COEFFICIENT, FEED_FORWARD_TERM, this.gyro.getPIDSource(), this);
       pidController = new PIDController(
    		   this.prefs.getDouble("turnCommand.pCoeff", PROPORTIONAL_COEFFICIENT), 
    		   this.prefs.getDouble("turnCommand.iCoeff", INTEGRAL_COEFFICIENT),
    		   this.prefs.getDouble("turnCommand.dCoeff", DERIVATIVE_COEFFICIENT),
    		   FEED_FORWARD_TERM, this.sensorService.getGyroSensor().getPIDSource(), this);
       pidController.setInputRange(-180.0,  180.0);
       pidController.setOutputRange(-1.0, 1.0);
       pidController.setAbsoluteTolerance(TOLERANCE_DEGREES);
       pidController.setContinuous(true);
       pidController.setSetpoint(this.targetAngle); // sets the angle to which we want to turn to
       pidController.enable();
   }
   
    @Override
    protected boolean isFinished() {
       if (Math.abs(this.sensorService.getGyroSensor().getAngle() - this.targetAngle)<=0.5) {
           pidController.disable();
           this.drivetrain.getRobotDrive().drive (0, 0);
           return true;
       }
        return false;
    }
    protected void execute() {
        
        this.drivetrain.getRobotDrive().drive (0.2, pidLoopCalculationOutput);
        
    }

    @Override
    public void pidWrite(double output) {
        this.pidLoopCalculationOutput = output;
        this.operatorDisplay.setNumericFieldValue("PID Loop Output Value", this.pidLoopCalculationOutput);
        
    }
}
