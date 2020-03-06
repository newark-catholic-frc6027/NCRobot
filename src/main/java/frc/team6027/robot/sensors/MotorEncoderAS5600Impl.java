package frc.team6027.robot.sensors;

import com.ctre.phoenix.motorcontrol.SensorCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.PIDSourceType;

public class MotorEncoderAS5600Impl implements MotorEncoder<Object> {
    private final Logger logger = LogManager.getLogger(getClass());

    private int mark;
    private boolean negate = false;
    private final SensorCollection sensors;
    private volatile int lastValue = Integer.MIN_VALUE;

    public MotorEncoderAS5600Impl(SensorCollection sensors) {
        this.sensors = sensors;
    }

    public int getMinValue() {
        return 0;
    }

    public int getMaxValue() {
        return 4096;
    }

    public int getTotalUnits() {
        return getMaxValue() - getMinValue();
    }
    
    private int getRawPosition() {
        int raw = sensors.getPulseWidthRiseToFallUs();
        // TODO: Add code to check min and max bounds and throw out values out of bounds
        if (raw == 0) {
            return this.lastValue == Integer.MIN_VALUE ? 0 : this.lastValue;
        }

        this.lastValue = raw;
        return raw;
    }

    private int getPwmPosition() {
        int raw = sensors.getPulseWidthRiseToFallUs();
        if (raw == 0) {
            int lastValue = this.lastValue;
            if (lastValue == Integer.MIN_VALUE) {
                return 0;
            }
            return lastValue;
        }
        int actualValue = Math.min(4096, raw - 128);
        logger.trace("Turret raw value: {}, actualValue: {}", raw, actualValue);
        lastValue = actualValue;
        return actualValue;
    }

    @Override
    public void markPosition() {
        this.mark = this.getRawPosition();
    }

    @Override
    public void resetMark() {
        this.markPosition();
    }

    @Override
    public void reset() {
        this.resetMark();
    }

    @Override
    public double getLastMarkPosition() {
        return this.mark;
    }

    @Override
    public double getPosition() {
        return this.getRawPosition();
    }

    @Override
    public double getRelativePosition() {
        return this.getRawPosition() - this.getLastMarkPosition();
    }

    @Override
    public double getDistance() {
        throw new UnsupportedOperationException("getDistance not supported");
    }

    @Override
    public double getRelativeDistance() {
        throw new UnsupportedOperationException("getRelativeDistance not supported");
    }

    @Override
    public double getVelocity() {
        throw new UnsupportedOperationException("getVelocity not supported");
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return null;
    }

    @Override
    public double pidGet() {
        return 0;
    }

}
