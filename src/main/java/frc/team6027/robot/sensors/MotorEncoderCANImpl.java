package frc.team6027.robot.sensors;

import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.PIDSourceType;

public class MotorEncoderCANImpl implements MotorEncoder<CANEncoder> {

    private CANEncoder encoder;
    private double mark;
    private boolean negate = false;

    public MotorEncoderCANImpl(CANEncoder encoder) {
        this.encoder = encoder;
        this.mark = this.getPosition();
    }

    public MotorEncoderCANImpl(CANEncoder encoder, boolean negate) {
        this.encoder = encoder;
        this.negate = negate;
        this.mark = this.getPosition();
    }

    @Override
    public double getPosition() {
        return (negate ? -1 : 1) * this.encoder.getPosition();
    }

    public double getRelativePosition() {
        return this.getPosition() - this.getLastMarkPosition();
    }

    @Override
    public double getVelocity() {
        return this.encoder.getPosition();
    }

    @Override
    public void markPosition() {
        this.mark = this.getPosition();
    }

    @Override
    public double getDistance() {
        double dist = 1.127532 * this.getPosition();
        return dist;
    }

    public double getRelativeDistance() {
        double dist = 1.127532 * this.getRelativePosition();
        return dist;
    }


	@Override
	public double getLastMarkPosition() {
        return this.mark;
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
    public void setPIDSourceType(PIDSourceType pidSource) {
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet() {
        return this.getPosition();
	}

    @Override
    public int getMinValue() {
        throw new UnsupportedOperationException("getMinValue not implemented yet");
    }

    @Override
    public int getMaxValue() {
        throw new UnsupportedOperationException("getMinValue not implemented yet");
    }

    @Override
    public int getTotalUnits() {
        throw new UnsupportedOperationException("getMinValue not implemented yet");
    }
}