package frc.team6027.robot.sensors;

import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.PIDSourceType;

public class MotorEncoderCANImpl implements MotorEncoder<CANEncoder> {

    private CANEncoder encoder;
    private double mark;

    public MotorEncoderCANImpl(CANEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public double getPosition() {
        return this.encoder.getPosition();
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
        return this.getPosition() - this.getLastMarkPosition();
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
}