package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class NavxGyroSensor implements Gyro {
    
    protected AHRS mAHRS;

    //protected Rotation2d mAngleAdjustment = Rotation2d.identity();
    protected double mAngle;
    protected double mYawDegrees;
    protected double mYawRateDegreesPerSecond;
    protected final long kInvalidTimestamp = -1;
    protected long mLastSensorTimestampMs;
    
    
    public NavxGyroSensor() {
        initialize();
    }

    @Override
    public void calibrate() {
       // mAHRS.registerCallback(new Callback(), null);
    }

    protected void initialize() {
        mAHRS = new AHRS(RobotConfigConstants.GYRO_I2C_PORT);
        reset();
    }
    @Override
    public void reset() {
        mLastSensorTimestampMs = kInvalidTimestamp;
        mYawDegrees = 0.0;
        mYawRateDegreesPerSecond = 0.0;
    }

    @Override
    public double getAngle() {
         return mAHRS.getAngle();
    }

    @Override
    public double getRate() {
        return 0;
    }

    @Override
    public void free() {
        
    }


}
