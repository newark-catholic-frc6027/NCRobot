package org.usfirst.frc.team6027.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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



    private Encoder rightEncoder = new Encoder(
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_A, 
            RobotConfigConstants.RIGHT_OPTICAL_ENCODER_DIO_CHANNEL_B, 
            false, EncodingType.k4X);

    public EncoderDriveCommand(DrivetrainSubsystem driveTrain) {
        this.driveTrain = driveTrain;
        requires(driveTrain);
    }

    @Override
    protected void initialize() {
        // TODO: define as constants
        getRightEncoder().setMaxPeriod(.1);
        getRightEncoder().setMinRate(10);
        getRightEncoder().setReverseDirection(true);
        getRightEncoder().setSamplesToAverage(7);
        getRightEncoder().setDistancePerPulse(DISTANCE_PER_PULSE);
        getRightEncoder().reset();
    }

    @Override
    protected void execute() {
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

    public void setRightEncoder(Encoder rightEncoder) {
        this.rightEncoder = rightEncoder;
    }

    public Encoder getRightEncoder() {		
        return rightEncoder;
    }

}
