package org.usfirst.frc.team6027.robot.commands;

import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.sensors.PIDCapableGyro;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

public class TurnCommand extends Command implements PIDOutput {
    
    protected static final double PROPORTIONAL_COEFFICIENT = 0.03;
    protected static final double INTEGRAL_COEFFICIENT = 0.00;
    protected static final double DERIVATIVE_COEFFICIENT = 0.00;
    protected static final double FEED_FORWARD_TERM = 0.00;
    /* This tuning parameter indicates how close to "on target" the    */
    /* PID Controller will attempt to get.                             */
    protected static final double TOLERANCE_DEGREES = 2.0;
    
    private PIDController pidController;
    
    private PIDCapableGyro gyro;
    
    private DrivetrainSubsystem drivetrain;
    private double angle;
    
    private double pidLoopCalculationOutput;
    private OperatorDisplay operatorDisplay;
    
   public TurnCommand (double angle, PIDCapableGyro gyro, DrivetrainSubsystem drivetrain, OperatorDisplay operatorDisplay) {
       this.gyro = gyro;
       this.drivetrain = drivetrain;
       this.angle = angle;
       this.operatorDisplay = operatorDisplay;
       
       initPIDController();
   }
    
   protected void initPIDController() {
       pidController = new PIDController(PROPORTIONAL_COEFFICIENT, INTEGRAL_COEFFICIENT, DERIVATIVE_COEFFICIENT, FEED_FORWARD_TERM, this.gyro.getPIDSource(), this);
       pidController.setInputRange(-180.0,  180.0);
       pidController.setOutputRange(-1.0, 1.0);
       pidController.setAbsoluteTolerance(TOLERANCE_DEGREES);
       pidController.setContinuous(true);
       pidController.setSetpoint(this.angle); // sets the angle to which we want to turn to
       pidController.enable();
   }
   
    @Override
    protected boolean isFinished() {
       if (this.gyro.getAngle() >= this.angle + 170) {
           pidController.disable();
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
