package frc.team6027.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team6027.robot.RobotConfigConstants;
import frc.team6027.robot.sensors.MotorEncoder;
import frc.team6027.robot.sensors.MotorEncoderAS5600Impl;
import frc.team6027.robot.sensors.MotorEncoderCANImpl;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;

public class Turret extends SubsystemBase {
    public final static String TURRET_POWER = "turret.power";
    public final static String TURRET_MAX_CCW_KEY = "turret.maxCounterClockwise";
    public final static String TURRET_MAX_CW_KEY = "turret.maxClockwise";


    public final static int TALON_PID_LOOP_INDEX = 0;
    public final static int TALON_PID_TIMEOUT_MS = 30;
    public final static boolean TALON_SENSOR_PHASE = true;
    public final static boolean TALON_INVERT = false;
    public final static int TALON_PID_ENCODER_ERROR_TOLERANCE = 10;

    private final Logger logger = LogManager.getLogger(getClass());

    private Preferences prefs = Preferences.getInstance();
    private WPI_TalonSRX turretCtrl = new WPI_TalonSRX(RobotConfigConstants.TURRET_CIM_ID);
    private MotorEncoderAS5600Impl encoder = new MotorEncoderAS5600Impl(turretCtrl.getSensorCollection());
    private final SimpleMotorFeedforward ff = new SimpleMotorFeedforward(0.1, .1);
                            
    private boolean manualOverrideAllowed = false;
    private long executionCount = 0;
//    private PIDController controller;
    public Turret() {
//        super(new PIDController(0, 0 ,0));
//        this.controller = getController();
 //       this.controller.setSetpoint(2100);
        this.initialize();
    }

    public MotorEncoder<?> getEncoder() {
        return encoder;
    }

    protected void initialize() {
/*
        turretCtrl.configFactoryDefault();
        turretCtrl.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TALON_PID_LOOP_INDEX, TALON_PID_TIMEOUT_MS);
        turretCtrl.setSensorPhase(TALON_SENSOR_PHASE);
        turretCtrl.setInverted(TALON_INVERT);

        turretCtrl.configNominalOutputForward(0, TALON_PID_TIMEOUT_MS);
		turretCtrl.configNominalOutputReverse(0, TALON_PID_TIMEOUT_MS);
		turretCtrl.configPeakOutputForward(prefs.getDouble(TURRET_POWER, .5), TALON_PID_TIMEOUT_MS);
		turretCtrl.configPeakOutputReverse(prefs.getDouble(TURRET_POWER, .5) * -1, TALON_PID_TIMEOUT_MS);
        
		turretCtrl.configAllowableClosedloopError(TALON_PID_LOOP_INDEX, TALON_PID_ENCODER_ERROR_TOLERANCE, TALON_PID_TIMEOUT_MS);

		turretCtrl.config_kF(TALON_PID_LOOP_INDEX, prefs.getDouble(TURRET_PID_FF, 0), TALON_PID_TIMEOUT_MS);
		turretCtrl.config_kP(TALON_PID_LOOP_INDEX, prefs.getDouble(TURRET_PID_P, 0), TALON_PID_TIMEOUT_MS);
		turretCtrl.config_kI(TALON_PID_LOOP_INDEX, prefs.getDouble(TURRET_PID_I, 0), TALON_PID_TIMEOUT_MS);
		turretCtrl.config_kD(TALON_PID_LOOP_INDEX, prefs.getDouble(TURRET_PID_D, 0), TALON_PID_TIMEOUT_MS);

        int absolutePosition = turretCtrl.getSensorCollection().getPulseWidthPosition();

		absolutePosition &= 0xFFF;
		if (TALON_SENSOR_PHASE) { absolutePosition *= -1; }
        if (TALON_INVERT) { absolutePosition *= -1; }
        
        turretCtrl.setSelectedSensorPosition(absolutePosition, TALON_PID_LOOP_INDEX, TALON_PID_TIMEOUT_MS);
*/
/*        
        this.controller.setPID(
            prefs.getDouble(TURRET_PID_P, 0),
            prefs.getDouble(TURRET_PID_I, 0),
            prefs.getDouble(TURRET_PID_D, 0)
        );
        this.controller.enableContinuousInput(
            prefs.getDouble(TURRET_MAX_CCW_KEY, 1100), 
            prefs.getDouble(TURRET_MAX_CW_KEY, 3000)
        );
*/
        // this.disable();  // disable the PID until turnToSetpoint is invoked
        this.stopMotor();
        
    }

    public void stop() {
        this.stopMotor();
    }

    public void stopMotor() {
        if (this.turretCtrl != null) {
//            turretMotor.set(0);
            turretCtrl.set(ControlMode.PercentOutput, 0);
            this.turretCtrl.stopMotor();
        }

    }


    public void turn(double power) {
        this.executionCount++;
        this.turretCtrl.set(power);
    }

        /*
    public void turn(double encoderTicks) {
        if (this.turretCtrl != null) {
            logger.trace("Raw encoder position: {}, encoderTicks: {}", encoder.getPosition(), encoderTicks);
            this.turretCtrl.set(ControlMode.Position, encoderTicks);
//            logger.trace("Raw encoder position: {}", this.turretMotor.getEncoder(EncoderType.kQuadrature, 128).getPosition());
        }

    }
*/

    public boolean isManualOverrideAllowed() {
        return this.manualOverrideAllowed;
    }

    public void turn(double power, MotorDirection turnDirection) {
        this.executionCount++;
//        this.disable();  // disable pid control
        if (this.turretCtrl != null) {
            this.turretCtrl.set(turnDirection == MotorDirection.Reverse ? power * -1 : power);
//            logger.trace("Raw encoder position: {}", this.turretMotor.getEncoder(EncoderType.kQuadrature, 128).getPosition());
            if (executionCount % 20 == 0) {
                logger.trace("Raw encoder position: {}", encoder.getPosition());
            }
        }
    }

	public void setManualOverrideAllowed(boolean overrideAllowed) {
        this.manualOverrideAllowed = overrideAllowed;

	}

    /*
    public boolean atSetpoint() {
        boolean atSetpoint = this.isEnabled() && this.controller.atSetpoint();

        if (atSetpoint) {
            this.stopMotor();
            this.disable();
        }
        return atSetpoint;
    }
*/
/*
    public void turnToSetpoint(double position) {
        logger.trace("Turret subsystem Turning to setpoint: {}", position);
        logger.trace("p: {}, i: {}, d: {}", controller.getP(), controller.getI(), controller.getD());

        this.setSetpoint(position);
        this.enable();  // enable pid control
//        this.turretMotor.set(.1);
    }
*/
/*
    @Override
    protected void useOutput(double output, double setpoint) {
        double adjPower = output + ff.calculate(setpoint);
        logger.trace("pid output: {}, pid setpoint: {}, adjPower: {}", output, setpoint, adjPower);
        this.turretMotor.set(adjPower);
    }

    @Override
    protected double getMeasurement() {
        return encoder.getPosition();
    }
*/
}