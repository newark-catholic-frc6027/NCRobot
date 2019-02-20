package frc.team6027.robot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import frc.team6027.robot.subsystems.ArmSubsystem;
import frc.team6027.robot.subsystems.ArmSubsystem.MotorDirection;

public class ArmMotorCommand extends Command {
    private final Logger logger = LogManager.getLogger(getClass());

    private ArmSubsystem arm;
    private double power;
    private MotorDirection spinDirection;

    public ArmMotorCommand(ArmSubsystem armSubsystem, MotorDirection spinDirection) {
        this(.50, spinDirection, armSubsystem);
    }

    public ArmMotorCommand(double power, MotorDirection spinDirection, ArmSubsystem armSubsystem) {
        this.arm = armSubsystem;
        this.power = power;
        this.spinDirection = spinDirection;
    }

    @Override
    public void execute() {
        this.logger.trace("ArmMotorCommand power: {}, direction:{}", this.power, this.spinDirection);
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