package frc.team6027.robot.commands;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.team6027.robot.data.Datahub;
import frc.team6027.robot.data.DatahubRegistry;
import frc.team6027.robot.data.LimelightDataConstants;
import frc.team6027.robot.sensors.MotorEncoder;
import frc.team6027.robot.subsystems.MotorDirection;
import frc.team6027.robot.subsystems.Turret;

public class TurretTurnToPositionCommand extends PIDCommand {
    private static final Logger logger = LogManager.getLogger(TurretTurnToPositionCommand.class);

    private final static int MAX_INVALID_CONSECUTIVE_POS = 25;
    private final static double INVALID_POS_OVERAGE_FACTOR = 4096 * .10;

    public final static String TURRET_PID_P = "turret.pid.p";
    public final static String TURRET_PID_I = "turret.pid.i";
    public final static String TURRET_PID_D = "turret.pid.d";
    public final static String TURRET_PID_FF = "turret.pid.ff";
    public final static String TURRET_PID_TOLERANCE = "turret.pid.tolerance";

    public static final String TURRET_DEFAULT_SETPOINT_KEY = "turret.setpoint";

    private Preferences prefs = Preferences.getInstance();
    protected boolean isReset = false;
    protected double power = 0;
    protected double maxCounterClockwiseSetpoint = 0;
    protected double maxClockwiseSetpoint = 0;
    protected long executionCount = 0;

    private Turret turret;
    private double pidPower;
    private PIDController controller;
    private Datahub limelightData;

    private Double currentSetpoint;
    private Double turretMin;
    private Double turretMax;
    private double absoluteTurretMin;
    private double absoluteTurretMax;

    private boolean stopped = false;

    private int consecutiveInvalidPosCount = 0;
//    private List<Double> consecutiveInvalidPositions = new ArrayList<>();

    public TurretTurnToPositionCommand(Turret turret) {
        // Set up a No-op, default PID controller, then set up real values in initPidController
        super(new PIDController(0, 0, 0), 
            () -> turret.getEncoder().getPosition(),
            () -> Preferences.getInstance().getDouble(TURRET_DEFAULT_SETPOINT_KEY, 2100),
            (value) -> {},
            turret
        );
        controller = getController();
        this.turret = turret;
    }


    private void initPidController() {
        this.m_measurement = 
        () -> {
            double position = turret.getEncoder().getPosition();
            if (executionCount % 20 == 0) {
                logger.trace("Turret position: {}", position);
            }
            return position;
        };

        turretMin = prefs.getDouble(Turret.TURRET_MAX_CCW_KEY, 1200);
        absoluteTurretMin = turretMin - INVALID_POS_OVERAGE_FACTOR;
        turretMax = prefs.getDouble(Turret.TURRET_MAX_CW_KEY, 2900);
        absoluteTurretMax = turretMax + INVALID_POS_OVERAGE_FACTOR;
        this.m_setpoint = 
        () -> {
//            Double currentPosition = turret.getEncoder().getPosition();
//            if (currentPosition != null && currentPosition >= turretMin && currentPosition <= turretMax) {
                return currentSetpoint != null ? currentSetpoint :
                  Preferences.getInstance().getDouble(TURRET_DEFAULT_SETPOINT_KEY, 2100);
//            } else {
//                return  Preferences.getInstance().getDouble(TURRET_DEFAULT_SETPOINT_KEY, 2100);
//            }
        };
        
        this.m_useOutput = 
        (pidOutput) -> {
            if (executionCount % 20 == 0) {
                logger.trace("Turret PID output: {}", pidOutput);
            }
            turret.turn(pidOutput);
        };

        if (this.controller == null) {
            return;
        }
        this.controller.setPID(
            prefs.getDouble(TURRET_PID_P, 0),
            prefs.getDouble(TURRET_PID_I, 0),
            prefs.getDouble(TURRET_PID_D, 0)
        );

        double tolerance = prefs.getDouble(TURRET_PID_TOLERANCE, 50.0);
        this.controller.setTolerance(tolerance);

        logger.debug("Turret PID settings. Setpoint: {} P: {}, I: {}, D: {}, tolerance: {}", 
            prefs.getDouble(TURRET_DEFAULT_SETPOINT_KEY, 2100), this.controller.getP(), this.controller.getI(), this.controller.getD(),
            tolerance
        );
        /*
        this.controller.enableContinuousInput(
            prefs.getDouble(TURRET_MAX_CCW_KEY, 1100), 
            prefs.getDouble(TURRET_MAX_CW_KEY, 3000)
        );
        */
    }
    @Override
    public void initialize() {
        super.initialize();
        logger.trace("TurretTurnToPositionCommand initializing...");

        initPidController();
        this.limelightData = DatahubRegistry.instance().get(LimelightDataConstants.LIMELIGHT_DATAHUB_KEY);
        this.limelightData.put(LimelightDataConstants.LED_MODE_KEY, LimelightDataConstants.LedMode.On.value);
        // For the case when manual override (of turret limits) has been triggered, ensure we are back in to a state
        // to not allow manual override until it is needed again.
        this.turret.setManualOverrideAllowed(false);

        this.consecutiveInvalidPosCount = 0;

        reset();
    }
    
	@Override
	public void cancel() {
        this.isReset = false;
        this.stopped = true;
		super.cancel();
	}

    protected void reset() {
        this.isReset = true;
        this.stopped = false;
        this.executionCount = 0;
    }


    public void execute() {
        executionCount++;
        Double tx = this.limelightData.getDouble(LimelightDataConstants.TARGET_HORIZ_OFFSET_DEG_KEY, 0.0);
        Double encoderUnitsToMove = null;
        Double newPosition = null;
        if (tx != null) {
            encoderUnitsToMove = (tx/360.0) * this.turret.getEncoder().getTotalUnits();
            Double currentPosition = this.turret.getEncoder().getPosition();
            newPosition = currentPosition + encoderUnitsToMove;
            // Don't change the setpoint if we are out of range
            if (newPosition >= turretMin && newPosition <= turretMax) {
                this.currentSetpoint = newPosition;
            } else {
                this.currentSetpoint = currentPosition;
            }
        }
        super.execute();

        if (executionCount % 20 == 0) {
            logger.trace("[Limelight data] tv: {}, tx: {}, encoderUnitsToMove: {}, newPosition: {}", 
                this.limelightData.getNumber(LimelightDataConstants.NUM_TARGETS_KEY).toString(),
                this.limelightData.getDouble(LimelightDataConstants.TARGET_HORIZ_OFFSET_DEG_KEY).toString(),
                encoderUnitsToMove,
                newPosition
            );
        }

    }

/*
    @Override 
    public void execute() {

        if (executionCount ==0) {
            this.ticks = prefs.getDouble(TURRET_TICKS_KEY, .125);
            logger.trace("Turning {} encoder ticks...", this.ticks);
            this.turret.turn(this.ticks);
        } else {
            if (executionCount % 100 == 0) {
                logger.trace("Still executing TurretTurnToPositionCommand");
            }
        }
        executionCount++;

    }
*/    
    private boolean isLimitExceeded() {
        double currentPosition = this.turret.getEncoder().getPosition();
        boolean limitExceeded = false;
        if (currentPosition > 0 && (currentPosition < absoluteTurretMin || currentPosition > absoluteTurretMax)) {
            if (this.consecutiveInvalidPosCount >= MAX_INVALID_CONSECUTIVE_POS) {
                limitExceeded = true;
                this.consecutiveInvalidPosCount = 0;
            } else {
                this.consecutiveInvalidPosCount++;
            }
        } else {
            this.consecutiveInvalidPosCount = 0;
        }

        return limitExceeded;
    }
    
    public void stop() {
        this.stopped = true;
    }

    public boolean isOnTarget() {
        return isOnTarget(false);
    }

    /**
     * @param turnOffLight Causes LED to turn off when on target
     */
    public boolean isOnTarget(boolean turnOffLight) {
        boolean onTarget = this.controller.atSetpoint();
        if (onTarget && turnOffLight) {
            this.limelightData.put(LimelightDataConstants.LED_MODE_KEY, LimelightDataConstants.LedMode.Off.value);
        }
        return onTarget;
    }
    
    @Override
    public boolean isFinished() {
        if (this.isLimitExceeded()) {
            logger.error("Killing {} command due to turret limit exceeded!", this.getClass().getSimpleName());
            this.turret.setManualOverrideAllowed(true);
            return true;
        }
        return this.stopped;
        /*
        boolean done = this.executionCount >= 50 * 120;//this.controller.atSetpoint();
        if (done) {
            logger.info("TurretTurnToPositionCommand finished");
        }
        return done;
        */
    }
    
    @Override
    public void end(boolean interrupted) {
        // this.turret.stop();
        // Reset our state for when we run again
        this.isReset = false;
        this.turret.stop();
    }

}
