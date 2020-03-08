package frc.team6027.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team6027.robot.subsystems.MotorDirection;
import frc.team6027.robot.subsystems.Shooter;

public class SpinShooterCommand extends CommandBase {

    private Shooter shooter;
    private double speed;

    private boolean done = false;

    public SpinShooterCommand(Shooter shooter, double speed) {
        this.shooter = shooter;
        this.speed = speed;
    }

    @Override
    public void initialize() {
        this.done = false;
    }

    @Override
    public void execute() {
        if (! this.done) {
            this.shooter.spin(this.speed, MotorDirection.Forward);
        } else {
            this.shooter.stop();
        }
    }

    public void stop() {
        this.done = true;
        this.shooter.stop();
    }

    public boolean isStopped() {
        return this.done;
    }
    
    @Override
    public boolean isFinished() {
        return this.done;
    }
}