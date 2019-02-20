package frc.team6027.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team6027.robot.subsystems.ArmSubsystem;
import frc.team6027.robot.subsystems.ArmSubsystem.MotorDirection;

public class ArmMotorCommand extends Command {

    private ArmSubsystem arm;
    private double power;
    private MotorDirection spinDirection;

    public ArmMotorCommand(ArmSubsystem armSubsystem, MotorDirection spinDirection) {
        this(.20, spinDirection, armSubsystem);
    }

    public ArmMotorCommand(double power, MotorDirection spinDirection, ArmSubsystem armSubsystem) {
        this.arm = armSubsystem;
        this.power = power;
    }

    @Override
    public void execute() {
        this.arm.spin(this.power, this.spinDirection);
    }

    @Override
    public void cancel() {
        this.arm.stop();
        super.cancel();
    }

    @Override
    protected boolean isFinished() {
        return this.isCanceled();
    }
}