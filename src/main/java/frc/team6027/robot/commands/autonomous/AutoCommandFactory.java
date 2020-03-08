package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team6027.robot.commands.SpinShooterCommand;
import frc.team6027.robot.commands.ToggleBallLatchCommand;
import frc.team6027.robot.commands.TurretTurnToPositionCommand;
import frc.team6027.robot.subsystems.Ballpickup;
import frc.team6027.robot.subsystems.MotorDirection;
import frc.team6027.robot.subsystems.Pneumatics;
import frc.team6027.robot.subsystems.Shooter;
import frc.team6027.robot.subsystems.Turret;

public class AutoCommandFactory {
    private final static Logger logger = LogManager.getLogger(AutoCommandFactory.class);

    public static final String EMPTYBALLMAG_BALLPICKUP_POWER = "emptyBallMagCommand.ballpickupPower";
    public static final String EMPTYBALLMAG_BALLPICKUP_BACKDRIVE_MS = "emptyBallMagCommand.ballpickupBackdriveMs";
    public static final String EMPTYBALLMAG_BALLPICKUP_FOWARDDRIVE_MS = "emptyBallMagCommand.ballpickupForwarddriveMs";
    public static final String EMPTYBALLMAG_SHOOTER_SPIN_MS = "emptyBallMagCommand.shooterSpinMs";
    public static final String EMPTYBALLMAG_INITIAL_DELAY_MS = "emptyBallMagCommand.initialDelayMs";

    private static Preferences prefs = Preferences.getInstance();


    public static Command emptyBallMagCommand(Shooter shooter, Pneumatics pneumatics, Ballpickup ballpickup, Turret turret) {
        Double ballpickupPower = prefs.getDouble(EMPTYBALLMAG_BALLPICKUP_POWER, 0.5);
        Double ballpickupBackdriveMs = prefs.getDouble(EMPTYBALLMAG_BALLPICKUP_BACKDRIVE_MS, 0.25);
        Double ballpickupForwardDriveMs = prefs.getDouble(EMPTYBALLMAG_BALLPICKUP_FOWARDDRIVE_MS, 4.0);
        Double initialDelayMs = prefs.getDouble(EMPTYBALLMAG_INITIAL_DELAY_MS, .25);

        Double shooterSpinMs = prefs.getDouble(EMPTYBALLMAG_SHOOTER_SPIN_MS, 4.0);

        TurretTurnToPositionCommand turretCommand = new TurretTurnToPositionCommand(turret);
        SpinShooterCommand spinCommand = new SpinShooterCommand(shooter, 1.0);

        return new ParallelCommandGroup(
            // Parallel command 1: get turret on target.
            // TODO: Needs a little more testing
            turretCommand.withInterrupt(() -> {
                boolean onTarget = turretCommand.isOnTarget();
                if (onTarget) {
                    logger.debug("Turret is on target, interrupting"); 
                }
                return onTarget; 
            }),  // get on target, turn off light

            // Parallel command 2: Open ball latch
            new ToggleBallLatchCommand(pneumatics, true),

            // Parallel command 3: Spin ball shooter until told to stop
            spinCommand.withInterrupt(() -> spinCommand.isStopped()),

            // Parallel command group 4: shoot the balls
            new SequentialCommandGroup(
                // Sequential 4a: Wait for shooter to reach max RPM
                new RunCommand(() -> {}).withInterrupt(() -> {
                    boolean atMaxRpm = shooter.isAtMaxRPM();
                    if (atMaxRpm) {
                        logger.debug("Shooter reached max rpm"); 
                    }
                    return atMaxRpm;
                }),
                // Sequential 4b: Back drive ballpickup just a hair
                new RunCommand( () -> ballpickup.spin(ballpickupPower, MotorDirection.Forward), ballpickup).withTimeout(ballpickupBackdriveMs),
                // Sequential 4c: Drive ballpickup to shoot balls
                // TODO: once limit switch is wired up, stop this command on timeout OR when counts 3 balls
                new RunCommand( () -> ballpickup.spin(ballpickupPower, MotorDirection.Reverse), ballpickup).withTimeout(ballpickupForwardDriveMs),
                // Sequential 4d: Stop shooter spin
                new InstantCommand(() -> {
                    logger.debug("Stopping shooter"); 
                    spinCommand.stop();
                })
                //,
                // Parallel command 4e: Open ball latch
                //new ToggleBallLatchCommand(pneumatics, false)

            )


        );
    }
}