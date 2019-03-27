package frc.team6027.robot.sensors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.team6027.robot.RobotConfigConstants;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSource;

public class NavxGyroSensor implements PIDCapableGyro {
    private final Logger logger = LogManager.getLogger(getClass());

    protected AHRS ahrs;

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
    }

    protected void initialize() {
        logger.info("Initializing Gyro sensor...");
        ahrs = new AHRS(RobotConfigConstants.GYRO_ALT_PORT);
        reset();
    }
    @Override
    public void reset() {
        logger.warn(">>>>>>>>>>>>>>>>> GYRO RESET <<<<<<<<<<<<<<<<");
        lastSensorTimestampMs = invalidTimestamp;
        yawDegrees = 0.0;
        yawRateDegreesPerSecond = 0.0;
        ahrs.reset();
        ahrs.zeroYaw();
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

    @Override
    public void close() throws Exception {
        this.ahrs.close();
    }



}
