package org.usfirst.frc.team6027.robot.sensors;

import org.usfirst.frc.team6027.robot.RobotConfigConstants;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSource;

public class NavxGyroSensor implements PIDCapableGyro {
    
    protected AHRS ahrs;

    //protected Rotation2d mAngleAdjustment = Rotation2d.identity();
    protected double angle;
    protected double yawDegrees;
    protected double yawRateDegreesPerSecond;
    protected final long invalidTimestamp = -1;
    protected long lastSensorTimestampMs;
    
    
    public NavxGyroSensor() {
        initialize();
    }
    
    @Override
    public void calibrate() {
       // mAHRS.registerCallback(new Callback(), null);
    }

    protected void initialize() {
        ahrs = new AHRS(RobotConfigConstants.GYRO_PORT);
        reset();
    }
    @Override
    public void reset() {
        lastSensorTimestampMs = invalidTimestamp;
        yawDegrees = 0.0;
        yawRateDegreesPerSecond = 0.0;
        ahrs.reset();
    }

    @Override
    public double getAngle() {
         return ahrs.getAngle();
    }
    

    @Override
    public double getRate() {
    	return this.ahrs.getRate();
    	//return 0;
    }

    @Override
    public void free() {
        
    }

    @Override
    public PIDSource getPIDSource() {
        return this.ahrs;
    }

    @Override
    public double getYawAngle() {
        return this.ahrs.getYaw();
    }



}
