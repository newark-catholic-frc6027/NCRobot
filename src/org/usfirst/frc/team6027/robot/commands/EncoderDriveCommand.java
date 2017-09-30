package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team6027.robot.OperatorDisplay;
import org.usfirst.frc.team6027.robot.RobotConfigConstants;
import org.usfirst.frc.team6027.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Command;

public class EncoderDriveCommand extends Command {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final double DISTANCE_PER_REVOLUTION = 18.84;//Math.PI * 6;
    public static final double PULSE_PER_REVOLUTION = 1024;
    public static final double DISTANCE_PER_PULSE = DISTANCE_PER_REVOLUTION / PULSE_PER_REVOLUTION;

    private DrivetrainSubsystem driveTrain;
    private OperatorDisplay operatorDisplay;


    private Encoder rightEncoder = new Encoder(
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);

    public EncoderDriveCommand(DrivetrainSubsystem driveTrain, OperatorDisplay operatorDisplay) {
        this.driveTrain = driveTrain;
        this.operatorDisplay = operatorDisplay;
        requires(driveTrain);
    }

    @Override
    protected void initialize() {
        rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
        rightEncoder.reset();
    }

    @Override
    protected void execute() {
        this.getOperatorDisplay().setNumericFieldValue(OperatorDisplay.DISTANCE_FIELD_NAME, rightEncoder.getDistance());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    public DrivetrainSubsystem getDriveTrain() {
        return driveTrain;
    }

    public void setDriveTrain(DrivetrainSubsystem driveTrain) {
        this.driveTrain = driveTrain;
    }

    public OperatorDisplay getOperatorDisplay() {
        return operatorDisplay;
    }

    public void setOperatorDisplay(OperatorDisplay operatorDisplay) {
        this.operatorDisplay = operatorDisplay;
    }

}
