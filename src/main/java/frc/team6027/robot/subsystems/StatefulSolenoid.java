package frc.team6027.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class StatefulSolenoid extends DoubleSolenoid {

    private DoubleSolenoid.Value state;
    
    public StatefulSolenoid(final int forwardChannel, final int reverseChannel) {
        super(forwardChannel, reverseChannel);
    }
    
    public StatefulSolenoid(final int moduleNumber, final int forwardChannel,
            final int reverseChannel) {
        super(moduleNumber, forwardChannel, reverseChannel);
    }
    
    @Override
    public void set(final Value value) {
        super.set(value);
        if (value != Value.kOff) {
            this.state = value;
        }
    }
    public void toggleForward() {
        this.set(DoubleSolenoid.Value.kForward);
        this.state = DoubleSolenoid.Value.kForward;
    }
    
    public void toggleReverse() {
        this.set(DoubleSolenoid.Value.kReverse);
        this.state = DoubleSolenoid.Value.kReverse;
    }
    
    public void toggleOff() {
        this.set(DoubleSolenoid.Value.kOff);
    }
    
    public DoubleSolenoid.Value getState() {
        return state;
    }
    
}