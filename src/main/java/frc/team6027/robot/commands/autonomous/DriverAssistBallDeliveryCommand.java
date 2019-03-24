package frc.team6027.robot.commands.autonomous;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import frc.team6027.robot.commands.ElevatorCommand;
import frc.team6027.robot.field.LevelSelection;
import frc.team6027.robot.sensors.SensorService;
import frc.team6027.robot.subsystems.DrivetrainSubsystem;
import frc.team6027.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriverAssistBallDeliveryCommand extends CommandGroup implements KillableAutoCommand {
    private final Logger logger = LogManager.getLogger(getClass());

    private SensorService sensorService;
    private ElevatorSubsystem elevatorSubsystem;
    boolean done = false;

    
    public DriverAssistBallDeliveryCommand(LevelSelection levelSelection, DrivetrainSubsystem drivetrainSubsystem,
			ElevatorSubsystem elevatorSubsystem, SensorService sensorService) {
        this.elevatorSubsystem = elevatorSubsystem;

        this.addSequential(makeElevatorCommand(levelSelection));
	}

    @Override
    public void start() {
        this.registerAsKillable();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command STARTING", this.getClass().getSimpleName());
        super.start();
    }

	@Override
    protected boolean isFinished() {
        return true;
    }
    

    protected Command makeElevatorCommand(LevelSelection levelSelection) {
        String prefName = null;
        switch(levelSelection) {
            case Middle:
                prefName = "rocketBall.middleLevel";
                break;
            case Upper:
                prefName = "rocketBall.upperLevel";
                break;
            case Lower:
            default:
                prefName = "rocketBall.lowerLevel";
                break;
        }

        Command cmd = new ElevatorCommand(prefName, "F-P0-DriverAssist-Ball", this.sensorService, this.elevatorSubsystem);
        return cmd;
    }

    @Override
    public void registerAsKillable() {
        this.default_registerAsKillable();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void end() {
        // When it ends peacefully, clean up the Killable command
        this.default_onComplete();
        super.end();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command ENDED", this.getClass().getSimpleName());
    }

    @Override
    public void cancel() {
        this.onComplete();
        super.cancel();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command CANCELED", this.getClass().getSimpleName());
    }

    @Override
    protected void interrupted() {
        this.onComplete();
        super.interrupted();
        this.logger.info(">>>>>>>>>>>>>>>>>>>> {} command INTERRUPTED", this.getClass().getSimpleName());
    }

}
