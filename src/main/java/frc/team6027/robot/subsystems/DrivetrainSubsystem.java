package frc.team6027.robot.subsystems;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.OperatorInterface;
import frc.team6027.robot.RobotConfigConstants;
// import com.ctre.CANTalon;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.sensors.EncoderSensors.EncoderKey;
import frc.team6027.robot.sensors.MotorEncoder;
import frc.team6027.robot.sensors.MotorEncoderCANImpl;
import frc.team6027.robot.sensors.MotorPIDController;
import frc.team6027.robot.sensors.MotorPIDControllerCANImpl;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DrivetrainSubsystem extends Subsystem {
    public enum MotorKey {
        MotorLeft,
        MotorRight
    }

    @SuppressWarnings("unused")
    private final Logger logger = LogManager.getLogger(getClass());

//    private WPI_TalonSRX rightGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.RIGHT_GEARBOX_CIM_1_ID);
//    private WPI_TalonSRX leftGearBoxMaster = new WPI_TalonSRX(RobotConfigConstants.LEFT_GEARBOX_CIM_1_ID);
    private CANSparkMax rightGearBoxMasterMotor = new CANSparkMax(
        RobotConfigConstants.RIGHT_GEARBOX_MASTER_CIM_1_ID, MotorType.kBrushless);
    private CANSparkMax leftGearBoxMasterMotor = new CANSparkMax(
        RobotConfigConstants.LEFT_GEARBOX_MASTER_CIM_1_ID, MotorType.kBrushless);
    
// Added by Russ (trying to slave the second motors)

    private CANSparkMax rightGearBoxSlave1 = new CANSparkMax(
        RobotConfigConstants.RIGHT_GEARBOX_SLAVE_CIM_2_ID, MotorType.kBrushless);
    private CANSparkMax leftGearBoxSlave1 = new CANSparkMax(
        RobotConfigConstants.LEFT_GEARBOX_SLAVE_CIM_2_ID, MotorType.kBrushless);


    private DifferentialDrive robotDrive = null;

    private OperatorInterface operatorInterface;

    public DrivetrainSubsystem(OperatorInterface operatorInterface) {
        this(operatorInterface, null);
    }

    public DrivetrainSubsystem(OperatorInterface operatorInterface, SensorService sensorService) {
        this.operatorInterface = operatorInterface;
        if (sensorService != null) {
            this.registerMotorEncoders(sensorService);
        }        
        this.initialize();
    }

	public void registerMotorEncoders(SensorService sensorService) {
        Map<EncoderKey, MotorEncoder> encoderMap = new HashMap<>();
        encoderMap.put(EncoderKey.DriveMotorLeft, new MotorEncoderCANImpl(this.leftGearBoxMasterMotor.getEncoder()));
        // Right motor needs negated
        encoderMap.put(EncoderKey.DriveMotorRight, new MotorEncoderCANImpl(this.rightGearBoxMasterMotor.getEncoder(), true));
        sensorService.addMotorEncoders(encoderMap);
	}

    public MotorPIDController getPIDController(MotorKey key) {
        if (key == MotorKey.MotorLeft) {
            return new MotorPIDControllerCANImpl(this.leftGearBoxMasterMotor.getPIDController());
        } else if (key == MotorKey.MotorRight) {
            return new MotorPIDControllerCANImpl(this.rightGearBoxMasterMotor.getPIDController());
        } else {
            return null;
        }
    }
    protected void initialize() {
        this.rightGearBoxSlave1.follow(this.rightGearBoxMasterMotor);
        this.leftGearBoxSlave1.follow(this.leftGearBoxMasterMotor);

        this.robotDrive = new DifferentialDrive(leftGearBoxMasterMotor, rightGearBoxMasterMotor);

        // Setting the speed controllers forward for our drivetrain
        boolean invert = RobotConfigConstants.OPTIONAL_DRIVETRAIN_DIRECTION_INVERSION == -1;

//        this.rightGearBoxMaster.setInverted(invert);
//        this.rightGearBoxSlave1.setInverted(invert);
//        this.leftGearBoxMaster.setInverted(invert);
//        this.leftGearBoxSlave1.setInverted(invert);

        this.stopMotor();
    }

    /**
     * When the run method of the scheduler is called this method will be
     * called.
     */
    @Override
    public void periodic() {
    }

    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    public void doArcadeDrive(double forwardValue, double rotateValue) {
        getRobotDrive().arcadeDrive(forwardValue, -1 * rotateValue);
    }

    public void stopArcadeDrive() {
        getRobotDrive().arcadeDrive(0, 0);
        getRobotDrive().stopMotor();
    }

    public OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    public void setOperatorInterface(OperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }

    protected DifferentialDrive getRobotDrive() {
        return robotDrive;
    }

    public void setRobotDrive(DifferentialDrive robotDrive) {
        this.robotDrive = robotDrive;

    }

    public void tankDrive(double leftValue, double rightValue) {
        getRobotDrive().tankDrive(leftValue, rightValue);

    }

    public void stopMotor() {
        this.leftGearBoxMasterMotor.stopMotor();
        this.rightGearBoxMasterMotor.stopMotor();
        getRobotDrive().stopMotor();
    }

    public boolean isCoastModeEnabled() {
        return this.rightGearBoxMasterMotor.getIdleMode() == IdleMode.kCoast;
    }

    public boolean isBrakeModeEnabled() {
        return this.rightGearBoxMasterMotor.getIdleMode() == IdleMode.kBrake;
    }

    public boolean enableCoastMode() {
        boolean netResultSuccess = false;

        CANError resultRightMaster = this.rightGearBoxMasterMotor.setIdleMode(IdleMode.kCoast);
        CANError resultRightSlave1 = this.rightGearBoxSlave1.setIdleMode(IdleMode.kCoast);
        
        CANError resultLeftMaster = this.leftGearBoxMasterMotor.setIdleMode(IdleMode.kCoast);
        CANError resultLeftSlave1 = this.leftGearBoxSlave1.setIdleMode(IdleMode.kCoast);

        netResultSuccess = resultRightMaster == CANError.kOK && resultRightSlave1 == CANError.kOK
                    && resultLeftMaster == CANError.kOK && resultLeftSlave1 == CANError.kOK;

        if (! netResultSuccess) {
            logger.warn("COAST MODE FAILED to put all motors into COAST mode.  Results: " + 
                "rightMaster: {}, rightSlave1: {}, leftMaster: {}, leftSlave1: {}",
                resultRightMaster, resultRightSlave1, resultLeftMaster, resultLeftSlave1);
        } else {
            logger.info("Motors now in COAST MODE");
        }

        return netResultSuccess;
    }

    public boolean enableBrakeMode() {
        boolean netResultSuccess = false;

        CANError resultRightMaster = this.rightGearBoxMasterMotor.setIdleMode(IdleMode.kBrake);
        CANError resultRightSlave1 = this.rightGearBoxSlave1.setIdleMode(IdleMode.kBrake);
        
        CANError resultLeftMaster = this.leftGearBoxMasterMotor.setIdleMode(IdleMode.kBrake);
        CANError resultLeftSlave1 = this.leftGearBoxSlave1.setIdleMode(IdleMode.kBrake);

        netResultSuccess = resultRightMaster == CANError.kOK && resultRightSlave1 == CANError.kOK
                    && resultLeftMaster == CANError.kOK && resultLeftSlave1 == CANError.kOK;

        if (! netResultSuccess) {
            logger.warn("BRAKE MODE FAILED to put all motors into BRAKE mode.  Results: " + 
                "rightMaster: {}, rightSlave1: {}, leftMaster: {}, leftSlave1: {}",
                resultRightMaster, resultRightSlave1, resultLeftMaster, resultLeftSlave1);
        } else {
            logger.info("Motors now in BRAKE MODE");
        }

        return netResultSuccess;

    }
}
